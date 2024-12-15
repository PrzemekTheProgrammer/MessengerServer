package pszerszenowicz.messenger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private Server server;

    HttpServerInitializer(Server server) {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        int clientNumber = server.addChannel(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new HttpFrameHandler(server,clientNumber));
    }
}
