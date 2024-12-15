package pszerszenowicz.messenger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private int lastNumber = 0;
    private final int port = 8080;
    private Map<Integer, Channel> channels = new HashMap<>();
    private Map<Integer, List<Channel>> connections = new HashMap<>();

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer(Server.this));
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public Channel getChannel(int clientNumber) {
        return channels.get(clientNumber);
    }

    public int addChannel(Channel channel) {
        lastNumber = lastNumber + 1;
        channels.put(lastNumber, channel);
        connections.put(lastNumber, new ArrayList<>());
        return lastNumber;
    }

    public void deleteChannel(int clientNumber) {
        Channel channel = getChannel(clientNumber);
        connections.forEach((k,v) ->{
            v.remove(channel);
        });
        channels.remove(clientNumber);
    }

    public void addConnection(int clientNumber, int receiver) {
        Channel channel = getChannel(receiver);
        connections.get(clientNumber).add(channel);
    }

    public List<Channel> getConnections(int clientNumber) {
        return connections.get(clientNumber);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
    }

}
