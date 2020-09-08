import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class ReceiverVerticle extends AbstractVerticle {
    @Override
    public void start(){

        vertx.eventBus().consumer("hello.vertx", msg ->{
            String msgBody = msg.body().toString();
            System.out.println(msgBody + " from B");
            HttpRequest<String> request = WebClient.create(vertx)
                    .get(8080, "localhost", "/api/hello/" + msgBody)
                    .putHeader("Accept", "text/plain")
                    .as(BodyCodec.string());

            Promise<String> promise = Promise.promise();

            request.send(asyncResult -> {
                if (asyncResult.succeeded()) {
                    HttpResponse<String> response = asyncResult.result();
                    System.out.println(response.toString() + " from C");
                    msg.reply("msg from C: " + response.body());
                    promise.complete(asyncResult.result().toString());
                }
                else {
                    promise.fail(asyncResult.cause());
                }
            });
        });
    }

}
