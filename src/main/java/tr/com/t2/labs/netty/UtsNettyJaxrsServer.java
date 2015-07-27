package tr.com.t2.labs.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ResourceLeakDetector;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Created by mertcaliskan
 * on 27/03/15.
 */
public class UtsNettyJaxrsServer extends NettyJaxrsServer {

    private EventLoopGroup eventLoopGroup;
    private EventLoopGroup eventExecutor;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 8;
    private int executorThreadCount = 256;
    private SSLContext sslContext;
    private int maxRequestSize = 1024 * 1024 * 10;
    private int backlog = 10 * 1024;

    @Override
    public void setSSLContext(SSLContext sslContext) { this.sslContext = sslContext; }

    @Override
    public void setIoWorkerCount(int ioWorkerCount) { this.ioWorkerCount = ioWorkerCount; }

    @Override
    public void setExecutorThreadCount(int executorThreadCount) { this.executorThreadCount =  executorThreadCount; }

    @Override
    public void setMaxRequestSize(int maxRequestSize) { this.maxRequestSize  = maxRequestSize; }

    public void setBacklog(int backlog) { this.backlog = backlog; }

    static {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    @Override
    public void start() {
        eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
        eventExecutor = new NioEventLoopGroup(executorThreadCount);
        deployment.start();
        final RequestDispatcher dispatcher = new RequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(), deployment.getProviderFactory(), domain);
        // Configure the server.
        if (sslContext == null) {
            bootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpObjectAggregator(maxRequestSize));
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, RestEasyHttpRequestDecoder.Protocol.HTTP));
                            ch.pipeline().addLast(new UtsHeadersChannelHandler());
                            //TODO ch.pipeline().addLast(new RestEasyHttpResponseEncoder(dispatcher));
                            ch.pipeline().addLast(eventExecutor, new RequestHandler(dispatcher));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE);
        } else {
            final SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(false);
            bootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addFirst(new SslHandler(engine));
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpObjectAggregator(maxRequestSize));
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, RestEasyHttpRequestDecoder.Protocol.HTTPS));
                            ch.pipeline().addLast(new UtsHeadersChannelHandler());
                            //TODO ch.pipeline().addLast(new RestEasyHttpResponseEncoder(dispatcher));
                            ch.pipeline().addLast(eventExecutor, new RequestHandler(dispatcher));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
        }
        bootstrap.bind(port).syncUninterruptibly();
    }
}