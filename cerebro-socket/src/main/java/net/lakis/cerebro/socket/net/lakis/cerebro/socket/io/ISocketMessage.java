package net.lakis.cerebro.socket.net.lakis.cerebro.socket.io;

public interface  ISocketMessage {
    public String getName();
    public int getCount();
    public void decode(String[] data);
    public String[] encode();
}
