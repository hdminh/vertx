import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SenderVerticle extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/api/hello").handler(this::helloVertx);
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    void helloVertx(RoutingContext ctx){
       vertx.setTimer(2000, id ->
        vertx.eventBus()
                .request("hello.vertx", "Minh",
               reply -> ctx.request().response().end((String) reply.result().body())));
    }
}
