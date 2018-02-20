package kord.pg;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import kord.pg.network.protocol.ConnectionPacket;
import kord.pg.network.protocol.DisconnectPacket;
import kord.pg.network.protocol.InformationPacket;
import kord.pg.utils.ConfigStruct;

public class Loader extends PluginBase implements Listener{

    private BungeeServer bungeeServer;
    private ConfigStruct struct;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.struct = new ConfigStruct(getConfig());
        this.struct.set();
        this.bungeeServer = new BungeeServer(this);
        this.bungeeServer.start();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public Config getConfig() {
        return super.getConfig();
    }

    public ConfigStruct getStruct() {
        return struct;
    }

    public BungeeServer getBungeeServer() {
        return bungeeServer;
    }

    @Override
    public void onDisable() {
        bungeeServer.getNetwork().getPacketsToWrite().clear();
        DisconnectPacket pk = new DisconnectPacket();
        pk.serverId = getStruct().getServerId();
        bungeeServer.getNetwork().putPacket(pk);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onCommand(ServerCommandEvent event){
        if(!event.getCommand().contains("say")) return;
        InformationPacket pk = new InformationPacket();
        pk.message = event.getCommand();
        pk.serverId = struct.getServerId();
        bungeeServer.getNetwork().putPacket(pk);
    }
}
