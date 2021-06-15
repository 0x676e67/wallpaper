import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:54
 */
public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                       .GET()
                                       .uri(URI.create("https://pic.netbian.com"))
                                       .build();
        HttpRequest.newBuilder()
                   .GET()
                   .uri(URI.create("https://pic.netbian.com/e/extend/downpic.php"));
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                                .body();
        System.out.println(body);
    }
}
