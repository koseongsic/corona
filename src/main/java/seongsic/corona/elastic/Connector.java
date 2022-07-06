package seongsic.corona.elastic;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

//import org.elasticsearch.client.ElasticsearchClient;
@Component
public class Connector {

    private CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private RestClient restClient;
    @Value("${elastic.id}")
    private String id;
    @Value("${elastic.pw}")
    private String pw;

    public RestClient getRestClient() throws IOException {
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(id, pw));
        if(restClient==null){
            RestClientBuilder esBuilder = RestClient.builder(
                            new HttpHost("localhost", 9200, "https"))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });
            RestClient restClient = esBuilder.build();
            return restClient;
        }else{
            return restClient;
        }
    }
}




