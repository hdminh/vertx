package vert.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ServerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/api/hello/:name").handler(this::helloName);
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private void helloName(RoutingContext ctx) {
        String name = ctx.pathParam("name");
        System.out.println("I'm in C");
        ctx.request().response().end(String.format("%s dep trai!", name));
    }

}
