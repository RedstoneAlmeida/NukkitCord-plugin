package kord.pg.network.protocol;

public class ConnectionPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.CONNECTION_PACKET;

    public String name;
    public String password;
    public long serverId;
    public int slots = 0;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.name = this.getString();
        this.password = this.getString();
        this.serverId = this.getLong();
        this.slots = this.getInt();
    }

    @Override
    public void encode() {
        this.putString(name);
        this.putString(password);
        this.putLong(serverId);
        this.putInt(slots);
    }

}
