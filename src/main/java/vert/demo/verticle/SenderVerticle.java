package vert.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SenderVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/api/all").handler(this::getAll);
        router.get("/api/get/:id").handler(this::getById);
        router.post("/api/add").handler(this::addPeople);
        router.put("/api/update").handler(this::updatePeople);
        router.delete("/api/delete/:id").handler(this::deletePeople);

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router);
        server.listen(config().getInteger("http.port", 8080),
                result -> {
                    if (result.succeeded()) {
                        log.info("server started at port 8080");
                        promise.complete();
                    } else {
                        promise.fail(result.cause());
                    }
                });
    }

    private void updatePeople(RoutingContext ctx) {
        JsonObject people = ctx.getBody().toJsonObject();
        vertx.eventBus().request("vertx.updatePeople", people,
                reply -> ctx.request().response().setStatusCode(200).end());
    }

    private void deletePeople(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        vertx.eventBus().request("vertx.deletePeople", id,
                reply -> ctx.request().response().setStatusCode(200).end());
    }

    private void addPeople(RoutingContext ctx) {
        JsonObject people = ctx.getBody().toJsonObject();
        vertx.eventBus().request("vertx.addPeople", people,
                reply -> ctx.request().response().setStatusCode(200).end());
    }

    private void getById(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        vertx.eventBus().request("vertx.getById", id,
                reply -> {
                    if (reply.succeeded()) {
                        ctx.request().response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(((JsonObject) reply.result().body()).encode());
                    } else {
                        ctx.request().response().end(reply.cause().getMessage());
                    }
                });
    }

    void getAll(RoutingContext ctx) {
        vertx.eventBus().request("vertx.getAll", null,
                reply -> {
                    if (reply.succeeded()) {
                        ctx.request().response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(((JsonArray) reply.result().body()).encode());
                    } else {
                        ctx.request().response().end(reply.cause().getMessage());
                    }
                });
    }


}
