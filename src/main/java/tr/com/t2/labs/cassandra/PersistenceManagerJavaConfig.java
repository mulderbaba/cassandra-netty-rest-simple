package tr.com.t2.labs.cassandra;

import com.datastax.driver.core.Cluster;
import info.archinnov.achilles.configuration.ConfigurationParameters;
import info.archinnov.achilles.persistence.AsyncManager;
import info.archinnov.achilles.persistence.PersistenceManager;
import info.archinnov.achilles.persistence.PersistenceManagerFactory;
import info.archinnov.achilles.type.ConsistencyLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import tr.com.t2.labs.Main;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by mertcaliskan
 * on 21/04/15.
 */
@Configuration
@PropertySource("classpath:/cassandra.properties")
public class PersistenceManagerJavaConfig {

    @Value("${achilles.entity.packages}")
    private String entityPackages;

    @Value("${achilles.cassandra.keyspace.name}")
    private String keyspaceName;

    @Value("${achilles.consistency.read.default}")
    private String consistencyLevelReadDefault;

    @Value("${achilles.consistency.write.default}")
    private String consistencyLevelWriteDefault;

    @Value("${achilles.ddl.force.table.creation}")
    private String forceTableCreation;

    @Autowired
    private Cluster cluster;

    private PersistenceManagerFactory pmf;

    @PostConstruct
    public void initialize() {
        Map<ConfigurationParameters, Object> configMap = extractConfigParams();
        pmf = PersistenceManagerFactory.PersistenceManagerFactoryBuilder.build(cluster, configMap);
    }

    private Map<ConfigurationParameters, Object> extractConfigParams() {
        Map<ConfigurationParameters, Object> configMap = new HashMap<>();
        configMap.put(ConfigurationParameters.ENTITY_PACKAGES, entityPackages);

        if (isNotBlank(keyspaceName)) {
            configMap.put(ConfigurationParameters.KEYSPACE_NAME, keyspaceName);
        }

        if (isNotBlank(consistencyLevelReadDefault)) {
            configMap.put(ConfigurationParameters.CONSISTENCY_LEVEL_READ_DEFAULT, ConsistencyLevel.valueOf(consistencyLevelReadDefault));
        }
        if (isNotBlank(consistencyLevelWriteDefault)) {
            configMap.put(ConfigurationParameters.CONSISTENCY_LEVEL_WRITE_DEFAULT, ConsistencyLevel.valueOf(consistencyLevelWriteDefault));
        }


        configMap.put(ConfigurationParameters.FORCE_TABLE_CREATION, Boolean.valueOf(forceTableCreation));

        return configMap;
    }

    @Bean
    public PersistenceManager getPersistenceManager() {
        return pmf.createPersistenceManager();
    }

    @Bean
    public AsyncManager getAsyncManager() {
        return pmf.createAsyncManager();
    }

    @Bean(destroyMethod = "close")
    public Cluster cassandraNativeClusterDev() {
        final Cluster cluster = Cluster.builder()
                .addContactPoints(Main.ipList.toArray(new String[Main.ipList.size()]))
                .build();

        String keyspaceCreation = "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3}";

        cluster.connect().execute(keyspaceCreation);

        return cluster;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }


}