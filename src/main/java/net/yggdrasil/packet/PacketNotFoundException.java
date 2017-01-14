package net.yggdrasil.packet;

/**
 * Created by Miu on 10/01/2017.
 */
public class PacketNotFoundException extends Exception {

    public PacketNotFoundException(String type, int opCode) {
        super("[" + type.toUpperCase() + "] - A packet could not be found with the opcode of: " + opCode);
    }
}
