package kord.pg.network;

import kord.pg.BungeeServer;
import kord.pg.Loader;
import kord.pg.network.protocol.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ASUS on 19/02/2018.
 */
public class Network {

    @SuppressWarnings("unchecked")
    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private Queue<DataPacket> packetsToProcess = new LinkedList<>();
    private Queue<DataPacket> packetsToWrite = new LinkedList<>();

    private final BungeeServer server;

    public Network(BungeeServer server){
        this.registerPackets();
        this.server = server;
    }

    public Queue<DataPacket> getPacketsToProcess() {
        return packetsToProcess;
    }

    public Queue<DataPacket> getPacketsToWrite() {
        return packetsToWrite;
    }

    public void processPacket(byte id, byte[] buffer){
        DataPacket pk = getPacket(id);
        pk.setBuffer(buffer);
        pk.decode();
        getPacketsToProcess().add(pk);
    }

    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                System.out.println("Logger");
            }
        }
        return null;
    }

    public void putPacket(DataPacket packet){
        getPacketsToWrite().add(packet);
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public void registerPackets(){
        this.registerPacket(ProtocolInfo.CONNECTION_PACKET, ConnectionPacket.class);
        this.registerPacket(ProtocolInfo.HANDLER_PACKET, HandlerPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECTION_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.INFORMATION_PACKET, InformationPacket.class);
        this.registerPacket(ProtocolInfo.SERVER_INFORMATION_PACKET, ServerInformationPacket.class);
    }

}
