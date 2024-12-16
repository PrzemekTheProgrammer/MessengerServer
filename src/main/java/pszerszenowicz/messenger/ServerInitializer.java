package pszerszenowicz.messenger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private Server server;

    ServerInitializer(Server server) {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        int clientNumber = server.addChannel(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new StringHandler(server,clientNumber));
    }
}
