package ru.practicum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import ru.practicum.StatsClient;

import java.net.URI;

@Configuration
public class StatisticsClientConfig {


    @Value("${stats-server.service-id:stats-server}") // id сервиса в Eureka
    private String statsServiceId;


    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
    @Bean
    public StatsClient statsClient(DiscoveryClient discoveryClient,
                                   RetryTemplate retryTemplate) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatsClient(builder.build(),  resolveStatsServerUrl(discoveryClient, retryTemplate));
    }

    private String resolveStatsServerUrl(DiscoveryClient discoveryClient, RetryTemplate retryTemplate) {
        ServiceInstance instance = retryTemplate.execute(ctx ->
                discoveryClient.getInstances(statsServiceId).getFirst()
        );
        URI uri = URI.create("http://" + instance.getHost() + ":" + instance.getPort());
        return uri.toString();
    }
}
