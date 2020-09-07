import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

public class ServerHttp extends AbstractVerticle {
    HttpRequest<String> request;
    @Override
    public void start(){

        request = WebClient.create(vertx)
                .get(8080, "localhost", "/api/hello")
                .putHeader("Accept", "text/plain")
                .as(BodyCodec.string())
                .expect(ResponsePredicate.SC_OK);
        vertx.setPeriodic(3000, id -> fetch());
    }

    private void fetch() {
        request.send(asyncResult -> {
           if (asyncResult.succeeded()){
               System.out.println(asyncResult.result().body());
           }
        });
    }
}
