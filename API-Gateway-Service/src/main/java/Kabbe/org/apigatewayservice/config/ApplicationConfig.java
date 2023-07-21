package Kabbe.org.apigatewayservice.config;

import Kabbe.org.apigatewayservice.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableHystrix
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Autowired
    AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("AUTHENTICATION-SERVICE", r -> r.path("/api/v1/authentications/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://AUTHENTICATION-SERVICE"))

                .route("USER-MANAGEMENT-SERVICE", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://USER-MANAGEMENT-SERVICE"))
                .build();
    }
}
