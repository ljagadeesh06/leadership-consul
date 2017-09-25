# Leadership Consul

## Installation

Put into Your ```gradle.build```

```groovy
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    // ...
    compile "com.github.kinguinltdhk:leadership-consul:0.0.4"
}

```

## Example of plain Java usage

```java
public class App {
    public static void main(String[] args) {
        Session session = new Session(15);
        Cluster cluster = new Cluster(session, new ClusterConfiguration());

        new MultiMode(cluster, 5);

        // long time thread
    }
}
```

Above code will create Consul session with TTL = 15 seconds and recreate it every 7 seconds. `MultiNode` class will try to attach leader to current node (every 5 econds).

## Spring example

```java
@Configuration
@Profile("multiinstance")
@ConditionalOnConsulEnabled
@EnableConfigurationProperties(value = {ClusterProperties.class, ConsulProperties.class})
public class MultiInstance {
    @Autowired
    private ClusterProperties clusterProperties;

    @Autowired
    private ConsulProperties consulProperties;

    @Bean
    public SessionConsulClient sessionConsulClient() {
        return new SessionConsulClient(consulProperties.getHost(), consulProperties.getPort());
    }

    @Bean
    public Session session(SessionConsulClient sessionConsulClient) {
        return new Session(clusterProperties.getLeader().getSessionTtl(), sessionConsulClient);
    }

    @Bean
    public KeyValueConsulClient keyValueConsulClient() {
        return new KeyValueConsulClient(consulProperties.getHost(), consulProperties.getPort());
    }

    @Bean
    public Cluster cluster(
        Session session,
        KeyValueConsulClient keyValueConsulClient
    ) {
        return new Cluster(session, clusterProperties.getLeader(), keyValueConsulClient);
    }

    @Bean
    @Primary
    public ClusterMode multiinstance(Cluster cluster) {
        return new MultiMode(cluster, clusterProperties.getLeader().getLeadershipAttemptsInterval());
    }
}
```

**Note:** Above configuration only works when Consul is enabled cause of `@ConditionalOnConsulEnabled`. You can remove this annotation.

## Configuration

```java
@ConfigurationProperties(prefix = "cluster")
public class ClusterProperties {
    private ClusterConfiguration leader;

    public ClusterConfiguration getLeader() {
        return leader;
    }

    public void setLeader(ClusterConfiguration leader) {
        this.leader = leader;
    }
}
```

In `application.yml` put section:

```yml
cluster:
    leader:
        serviceName: cluster
        leaderKeyTemplate: services/%s/leader
        serviceId: node-1
        sessionTtl: 15
        leadershipAttemptsInterval: 10
```
