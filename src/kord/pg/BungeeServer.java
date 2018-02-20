package kord.pg;

import cn.nukkit.command.ConsoleCommandSender;
import kord.pg.network.Network;
import kord.pg.network.protocol.*;
import kord.pg.utils.ConfigStruct;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ASUS on 19/02/2018.
 */
public class BungeeServer {

    private Loader plugin;
    private Network network;
    private int tick = 20;
    private BungeeServer server;
    private boolean firstStart = true;
    private Socket socket;

    public BungeeServer(Loader plugin){
        this.plugin = plugin;
        this.network = new Network(this);
        this.server = this;
    }

    public Network getNetwork() {
        return network;
    }

    public void start(){
        Thread thread = new Thread(() -> {
            try {
                ConfigStruct struct = this.plugin.getStruct();
                socket = new Socket(struct.getAddress(), struct.getPort());

                ConnectionPacket pk = new ConnectionPacket();
                pk.name = struct.getName();
                pk.password = struct.getPassword();
                pk.serverId = struct.getServerId();
                pk.slots = plugin.getServer().getMaxPlayers();
                server.getNetwork().putPacket(pk);

                tick();

                new Thread(() -> {
                    while (true) {
                        try {
                            DataInputStream entrada = new DataInputStream(socket.getInputStream());
                            byte id = entrada.readByte();
                            int lenght = entrada.readInt();
                            byte[] buffer = new byte[lenght];
                            entrada.readFully(buffer);
                            getNetwork().processPacket(id, buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                new Thread(() -> {
                    while (true) {
                        while (!server.getNetwork().getPacketsToWrite().isEmpty()) {
                            DataPacket pkt = server.getNetwork().getPacketsToWrite().poll();
                            pkt.encode();
                            try {
                                DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                                saida.writeByte(pkt.pid());
                                byte[] buffered = pkt.getBuffer();
                                saida.writeInt(buffered.length);
                                saida.write(buffered);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void tick(){
        Thread tickThread = new Thread(() -> {
            while (true){
                try {
                    if(!getNetwork().getPacketsToWrite().contains(new HandlerPacket()) && getNetwork().getPacketsToWrite().isEmpty()){
                        getNetwork().getPacketsToWrite().add(new HandlerPacket());
                    }
                    tick = 20 - getNetwork().getPacketsToProcess().size();
                    processPackets();
                    Thread.sleep(1000);
                    tick = 20;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tickThread.start();
    }

    public void processPackets(){
        while (!getNetwork().getPacketsToProcess().isEmpty()){
            DataPacket pk = getNetwork().getPacketsToProcess().poll();
            handlePacket(pk);
        }
    }

    public void handlePacket(DataPacket packet){
        switch (packet.pid()){
            case ProtocolInfo.CONNECTION_PACKET:
                break;
            case ProtocolInfo.HANDLER_PACKET:
                System.out.println("Recebendo HandlerPacket");
                break;
            case ProtocolInfo.DISCONNECTION_PACKET:
                System.out.println("Disconnected");
                plugin.getServer().shutdown();
                break;
            case ProtocolInfo.INFORMATION_PACKET:
                InformationPacket info = (InformationPacket) packet;
                plugin.getServer().dispatchCommand(new ConsoleCommandSender(), "say " + info.message.replace("say ", ""));
                break;
        }
    }

}
