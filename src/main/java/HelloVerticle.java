import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class HelloVerticle extends AbstractVerticle {

    @Override
    public void start(){
        Router router = Router.router(vertx);

        vertx.createHttpServer().requestHandler(router).listen(8080);

        vertx.eventBus().consumer("hello.vertx", msg ->{
            String msgBody = msg.body().toString();
            System.out.println(msgBody + " from B");
            HttpRequest<String> request = WebClient.create(vertx)
                    .get(8080, "localhost", "/api/hello/" + msgBody)
                    .putHeader("Accept", "text/plain")
                    .as(BodyCodec.string());

            request.send(asyncResult -> {
                if (asyncResult.succeeded()) {
                    System.out.println(asyncResult + " from C");
                    msg.reply("msg from C: " + asyncResult);
                }
            });
        });
    }

}
