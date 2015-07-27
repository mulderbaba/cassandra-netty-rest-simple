package tr.com.t2.labs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tr.com.t2.labs.cassandra.PersistenceManagerJavaConfig;

/**
 * Created by mertcaliskan
 * on 21/04/15.
 */
@Configuration
@Import(PersistenceManagerJavaConfig.class)
@ComponentScan(basePackages = "tr.com.t2.labs")
public class Config {
}