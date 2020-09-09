package vert.demo;

import io.vertx.core.Vertx;
import vert.demo.verticle.ReceiverVerticle;
import vert.demo.verticle.SenderVerticle;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle(new ServerVerticle());
        vertx.deployVerticle(new ReceiverVerticle());
        vertx.deployVerticle(new SenderVerticle());
    }
}
