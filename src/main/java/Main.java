import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class Main extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get("/api/hello").handler(this::helloVertx);

        vertx.createHttpServer().requestHandler(router).listen(8080);
        vertx.eventBus().consumer("hi.vertx", msg ->{
            String msgBody = msg.body().toString();
            System.out.println(msgBody + "from A");
            msg.reply("Hi vertx");
        });
    }

    void helloVertx(RoutingContext ctx){
       vertx.eventBus().request("hello.vertx", "hi", reply -> {
           ctx.request().response().end((String) reply.result().body());
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HelloVerticle());
        vertx.deployVerticle(new Main());
    }
}
