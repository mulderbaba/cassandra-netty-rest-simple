package tr.com.t2.labs.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;

/**
 * Created by mertcaliskan
 * on 27/03/15.
 */
public class UtsHeadersChannelHandler extends SimpleChannelInboundHandler<NettyHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyHttpRequest request) throws Exception {
        request.getResponse().getOutputHeaders().add("Access-Control-Allow-Origin", "*");
        request.getResponse().getOutputHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        request.getResponse().getOutputHeaders().add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length");

        ctx.fireChannelRead(request);
    }
}