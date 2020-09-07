import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HelloVerticle extends AbstractVerticle {

    @Override
    public void start(){
        Router router = Router.router(vertx);

        router.get("/api/hi").handler(this::hiVertx);

        vertx.createHttpServer().requestHandler(router).listen(8080);

        vertx.eventBus().consumer("hello.vertx", msg ->{
            String msgBody = msg.body().toString();
            System.out.println(msgBody + "from B");
            msg.reply("Hello vertx");
        });
    }

    private void hiVertx(RoutingContext ctx) {
        vertx.eventBus().request("hi.vertx", "hello", reply -> {
            ctx.request().response().end((String) reply.result().body());
        });
    }
}
