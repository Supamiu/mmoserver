package net.mmoserver.tcp;

import net.mmoserver.common.Log;
import net.mmoserver.common.Session;
import net.mmoserver.packet.Packet;
import net.mmoserver.packet.PacketOpcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/*
* Copyright (c) 2015
* Christian Tucker.  All rights reserved.
*
* The use of OGServer is free of charge for personal and commercial use. *
*
* THIS SOFTWARE IS PROVIDED 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
* BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE DISCLAIMED.  
* IN NO EVENT SHALL THE AUTHOR  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
* THE POSSIBILITY OF SUCH DAMAGE.
*  
*   * Policy subject to change.
*/

/**
 * The {@link TcpProcessor} class implements the {@link Runnable} interface
 * to aid in the concurrent asynchronous design.
 *
 * @author Christian Tucker
 */
public class TcpProcessor implements Runnable {

    public void run() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            if ((serverSocket.isOpen()) && (selector.isOpen())) {
                try {
                    serverSocket.configureBlocking(false);
                    serverSocket.bind(new InetSocketAddress("0.0.0.0", TcpServer.getPort()));
                    serverSocket.register(selector, SelectionKey.OP_ACCEPT);
                    Log.info("waiting for connections...");
                    while (!Thread.interrupted()) {
                        selector.select();
                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        while (keys.hasNext()) {
                            SelectionKey key = keys.next();
                            keys.remove();
                            if (!key.isValid()) {
                                continue;
                            }
                            try {
                                if (key.isAcceptable()) {
                                    acceptKey(key, selector);
                                } else if (key.isReadable()) {
                                    processData(key);
                                }
                            } catch (IOException e) {
                                if (e.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
                                    Log.info("A connection has been lost for key: " + key);
                                    if ((key.attachment() != null)) {
                                        ((Session) key.attachment()).close();
                                    } else {
                                        key.channel().close();
                                    }
                                    key.cancel();
                                    continue;
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                    if (Thread.interrupted()) {
                        selector.close();
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.error("Failure to initialize the server, perhaps the socket or selector is closed?");
                Log.info("Socket state: " + ((serverSocket.isOpen() ? "Open" : "Closed")) + " || Selector state: " + ((selector.isOpen() ? "Open" : "Closed")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the {@link TcpProcessor} logic thread whenever a {@link SelectionKey}'s
     * {@link SelectionKey#isAcceptable()} value is set to true. The primary function of
     * this method is to accept a connection and prepare it for future network communication.
     *
     * @param key      The {@link SelectionKey} relative to the connection.
     * @param selector The {@link Selector} being used by the server.
     * @throws IOExcepton if something goes wrong with low level io processing
     */
    private void acceptKey(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);

        Log.info("Incoming connection from " + clientSocket.getRemoteAddress());

        SelectionKey clientKey = clientSocket.register(selector, SelectionKey.OP_READ);

        clientKey.attach(new Session(clientKey));
    }

    /**
     * Called by the {@link TcpProcessor} logic thread whenever a {@link SelectionKey}'s
     * {@link SelectionKey#isReadable()} value is set to true. The primary function of this
     * method is to process the incoming {@link PacketOpcode} and locate the correct method
     * to call with the available data.
     *
     * @param key The {@link SelectionKey} relative to the connection.
     * @throws IOException if something goes wrong with low level io processing
     */
    private void processData(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        if (session == null) {
            Log.error("processData was called using a SelectionKey that contains no attacment.");
            key.cancel();
        }

        int bytesReceived = 0;
        boolean endOfStream;
        try {
            assert session != null;
            endOfStream = ((bytesReceived = session.getChannel().read(session.getInputBuffer())) == -1);
            if (endOfStream) {
                session.segment();
                key.cancel();
                return;
            }
        } catch (IOException e) {
            session.close();
        }


        Session.bytesIn += bytesReceived;
        Session.bytesInCurrent += bytesReceived;

        bytesReceived += session.mark();

        // The header that we're sending with our data is 4 bytes long, which is an Integer
        // containing the length of the packet in bytes, if the data that we have available
        // is not at least 4 bytes we will not read anything and wait until the next cycle.
        if (bytesReceived < 4) {
            return;
        }

        if (!session.segmented() && !session.header()) {
            ByteBuffer buffer = session.getInputBuffer().duplicate();
            buffer.flip();
            session.setBlockSize(buffer.getInt());
            session.getInputBuffer().mark();
            session.prime();

            Log.debug("[TCP] - Incoming packet size(in bytes): " + session.blockSize());
        }


        // Verify that we have enough data to process the entire packet.
        // Subtract 4 from the amount of bytes read so we don't process the
        // byte-size of the integer for the packet-length.
        if ((bytesReceived - 4) < session.blockSize()) {
            // Not enough data was received from the network, so we nBecaueed to reset the
            // Buffer's mark to the previous mark, so we can read this data again during
            // the next iteration.
            Log.debug("[TCP] - Current data received: ( " + (bytesReceived - 4) + " / " + session.blockSize() + " )");
            session.mark(bytesReceived);
            session.getInputBuffer().mark();
        } else {
            session.mark(bytesReceived);
            Packet._decode(session);
            session.release();
        }

    }

}
