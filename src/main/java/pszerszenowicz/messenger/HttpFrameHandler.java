package pszerszenowicz.messenger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;


public class HttpFrameHandler extends SimpleChannelInboundHandler<String> {

    private Server server;
    private Integer clientNumber;

    HttpFrameHandler(Server server, int clientNumber) {
        this.server = server;
        this.clientNumber = clientNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("Your client number: " + clientNumber.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.deleteChannel(clientNumber);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        if(message.startsWith("connect ")) {
            int receiver = Integer.parseInt(message.split(" ")[2]);
            server.addConnection(clientNumber,receiver);
            channelHandlerContext.channel().writeAndFlush("Connected!");
        }
        else {
            List<Channel> connections = server.getConnections(clientNumber);
            connections.forEach((channel) -> {
                channel.writeAndFlush(message);
            });
        }
    }
}
