package com.viabill.graphqltest.criipto.configuration;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.RequestExecutionSpringReactiveImpl;
import com.viabill.criipto.signature.generated.MutationExecutorCriiptoSignature;
import com.viabill.criipto.signature.generated.QueryExecutorCriiptoSignature;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class CriiptoGraphqlConfiguration {

    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 25000;
    private static final int WRITE_TIMEOUT = 5000;
    private static final int RESPONSE_TIMEOUT = 25000;
    private static final int BUFFER_SIZE = 16 * 1024 * 1024; // 16 mb;

    @Bean
    GraphQLConfiguration graphQLConfigurationCriiptoSignature(WebClient criiptoSignatureWebClient) {
        return new GraphQLConfiguration(
                new RequestExecutionSpringReactiveImpl(
                        "https://signatures-api.criipto.com/v1/graphql", null,
                        criiptoSignatureWebClient, null,
                        null, null));
    }

    @Bean
    WebClient criiptoSignatureWebClient(final WebClient.Builder webClientBuilder,
            @Value("${criipto.authentication-token}") String criiptoAuthenticationToken) {
        // create reactor netty HTTP client
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)));
//      For Fiddler
//        Http11SslContextSpec http11SslContextSpec =
//                Http11SslContextSpec.forClient()
//                        .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));
//        httpClient = httpClient
//            .proxyWithSystemProperties()
//            .secure(spec -> {
//                spec.sslContext(http11SslContextSpec);
//            });

        // create a client http connector using above http client
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        //We need a maximum data buffer otherwise default buffer is not enough sometimes and throws exception during exchange
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                .build();
        return webClientBuilder
                .baseUrl("https://signatures-api.criipto.com/v1/graphql")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, criiptoAuthenticationToken)
                .exchangeStrategies(strategies)
                .clientConnector(connector)
                .build();
    }

    @Bean
    MutationExecutorCriiptoSignature mutationExecutorCriiptoSignature(GraphQLConfiguration graphQLConfigurationCriiptoSignature) {
        return new MutationExecutorCriiptoSignature(graphQLConfigurationCriiptoSignature);
    }

    @Bean
    QueryExecutorCriiptoSignature queryExecutorCriiptoSignature(GraphQLConfiguration graphQLConfigurationCriiptoSignature) {
        return new QueryExecutorCriiptoSignature(graphQLConfigurationCriiptoSignature);
    }


}
