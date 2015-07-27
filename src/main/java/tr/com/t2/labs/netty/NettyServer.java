package tr.com.t2.labs.netty;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Collection;

/**
 * Created by mertcaliskan
 * on 21/04/15.
 */
@Component
public class NettyServer implements Lifecycle {

    private final ApplicationContext ctx;

    private UtsNettyJaxrsServer netty = null;

    private int applicationPort = 8080;
    private String applicationContextPath = "/";

    @Autowired
    public NettyServer(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void start() {
        ResteasyDeployment dp = new ResteasyDeployment();
        Collection<Object> controllers = ctx.getBeansWithAnnotation(Controller.class).values();
        dp.getResources().addAll(controllers);

        netty = new UtsNettyJaxrsServer();
        netty.setDeployment(dp);
        netty.setPort(applicationPort);
        netty.setRootResourcePath(applicationContextPath);
        netty.start();
    }

    @Override
    public void stop() {
        netty.stop();
    }

    @Override
    public boolean isRunning() {
        return netty != null;
    }
}
