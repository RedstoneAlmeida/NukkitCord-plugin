package kord.pg.utils;

import cn.nukkit.utils.Config;

import java.util.Random;

/**
 * Created by ASUS on 20/02/2018.
 */
public class ConfigStruct {

    private Config config;
    private Random random = new Random();

    public ConfigStruct(Config config){
        this.config = config;
    }

    public void set(){
        String[] strings = new String[]{
                "serverName", "password"
        };
        for(String cS : strings){
            if(!config.exists(cS)) config.set(cS, "");
        }
        if(!config.exists("address")){
            config.set("address", "127.0.0.1");
        }
        if(!config.exists("port")){
            config.set("port", 1111);
        }
        if(!config.exists("serverId")){
            config.set("serverId", random.nextLong());
        }
        config.save();
    }

    public String getAddress(){
        return config.getString("address");
    }

    public int getPort(){
        return config.getInt("port");
    }

    public String getName(){
        return config.getString("serverName");
    }

    public long getServerId(){
        return config.getLong("serverId");
    }

    public String getPassword(){
        return config.getString("password");
    }

    public Config getConfig() {
        return config;
    }
}
