package pszerszenowicz.messenger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;


public class HttpFrameHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Server server;
    private Integer clientNumber;

    HttpFrameHandler(Server server, int clientNumber) {
        this.server = server;
        this.clientNumber = clientNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.content().writeBytes(clientNumber.toString().getBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.deleteChannel(clientNumber);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        if(fullHttpRequest.headers().contains("new_connection")) {
            int receiver = Integer.parseInt(fullHttpRequest.headers().get("new_connection"));
            server.addConnection(clientNumber,receiver);
        }
        else {
            List<Channel> connections = server.getConnections(clientNumber);
            String message = fullHttpRequest.content().toString(CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.content().writeBytes(message.getBytes());
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            connections.forEach((channel) -> {
                channel.writeAndFlush(response);
            });
        }
    }
}
