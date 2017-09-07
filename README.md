# Leadership Consul

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
@EnableConfigurationProperties(value = ClusterProperties.class)
public class MultiInstance {
    @Autowired
    private ClusterProperties clusterProperties;

    @Bean
    public Session session() {
        return new Session(clusterProperties.getLeader().getSessionTtl());
    }

    @Bean
    public Cluster cluster(Session session) {
        return new Cluster(session, clusterProperties.getLeader());
    }

    @Bean
    @Primary
    public ClusterMode multiinstance(Cluster cluster) {
       return new MultiMode(cluster, clusterProperties.getLeader().getLeadershipAttemptsInterval());
    }
}
```

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

