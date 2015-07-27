package tr.com.t2.labs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tr.com.t2.labs.netty.NettyServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mertcaliskan
 * on 27/03/15.
 */
public class Main {

    public static List<String> ipList = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Nodes IP list should be provided!");
        }

        for (int i = 0; i < args.length; i++) {
            ipList.add(args[i]);
        }
        AnnotationConfigApplicationContext spring = new AnnotationConfigApplicationContext(Config.class);
        NettyServer nettyServer = spring.getBean(NettyServer.class);
        nettyServer.start();
    }
}