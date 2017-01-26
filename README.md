# Yggio

[![codecov](https://codecov.io/gh/Supamiu/yggio/branch/master/graph/badge.svg)](https://codecov.io/gh/Supamiu/yggio)
[![Build Status](https://travis-ci.org/Supamiu/yggio.svg?branch=master)](https://travis-ci.org/Supamiu/yggio)

A simple open source lib to create and send packets easily on your MMO server. It builds an abstract layer on top of java.nio.

Projects using it: 
 - [Yggdrasil](https://github.com/supamiu/yggdrasil) (mmo server)

## How to use it:

First of all, you need to create a new Packet implementation for each packet you want to manage:

```java
@PacketOpCode(10)//Here is the opCode of your packet, it has to be unique.
public class ExamplePacket extends Packet{

    @Override
    public void decodeTcp(Session session) throws IOException {
        //What to do when it comes through TCP.
    }

    @Override
    public void decodeUdp(Session session, ByteBuffer udpBuffer) throws IOException {
        //What to do when it comes though UDP.
    }
}
```

Then you start a new server (you can start only of of the two protocols, depending on what you need):

```java
public class Example{
    public static void main(String[] args){
        Packet.add(new ExamplePacket());//You have to do this for each packet you want to handle,
                                        //it'll be refactored later on.
        //...
        new TcpServer(5008);//Arbitrary port, you can chosse your own ofc.
        new UdpServer(5009);//Arbitrary port, you can chosse your own ofc.
        //...
    }
}
```

As both server handle their current instance in a singleton, you don't have to store them, you can access the current
`TcpServer` by calling directly static methods.