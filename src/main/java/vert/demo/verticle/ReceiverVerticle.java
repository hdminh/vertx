package vert.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import lombok.extern.log4j.Log4j2;
import vert.demo.exeption.StudentIdExistedExeption;
import vert.demo.model.PeopleModel;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ReceiverVerticle extends AbstractVerticle {
    Map<String, PeopleModel> peopleMap;

    public ReceiverVerticle() {
        peopleMap = new HashMap<>();
        List<PeopleModel> peopleList = new ArrayList<>(Arrays.asList(
                PeopleModel.builder().id("1").name("Dinh Minh").age(21).address("HCM").build(),
                PeopleModel.builder().id("2").name("Ho Chi Minh").age(91).address("HCM").build(),
                PeopleModel.builder().id("3").name("Thanh Binh").age(22).address("HCM").build()
        ));
        peopleList.forEach(people -> {
            peopleMap.put(people.getId(), people);
        });
    }

    @Override
    public void start() {
        vertx.eventBus().consumer("vertx.hello", this::hello);
        vertx.eventBus().consumer("vertx.getAll", this::getAllPeople);
        vertx.eventBus().consumer("vertx.getById", this::getPeopleById);
        vertx.eventBus().consumer("vertx.addPeople", this::addPeople);
        vertx.eventBus().consumer("vertx.updatePeople", this::updatePeople);
        vertx.eventBus().consumer("vertx.deletePeople", this::deletePeople);
    }

    private void updatePeople(Message<JsonObject> msg) {
        JsonObject people = msg.body();
        String id = people.getString("id");
        try {
            if (peopleMap.containsKey(id)) {
                log.info("UPDATE " + people.getString("name"));
                PeopleModel curPeople = peopleMap.get(id);
                curPeople.setId(people.getString("id"));
                curPeople.setName(people.getString("name"));
                curPeople.setAge(people.getInteger("age"));
                curPeople.setAddress(people.getString("address"));
                msg.reply(null);
            } else {
                msg.fail(500, new RuntimeException().getMessage());
            }
        } catch (RuntimeException e) {
            msg.fail(500, e.getMessage());
        }
    }

    private void deletePeople(Message<String> msg) {
        String id = msg.body();
        try {
            if (peopleMap.containsKey(id)) {
                log.info("DELETE " + peopleMap.get(id).getName());
                peopleMap.remove(id);
                msg.reply(null);
            }
        } catch (StudentIdExistedExeption e) {
            msg.fail(404, e.getMessage());
        }
    }

    private void addPeople(Message<JsonObject> msg) {
        JsonObject people = msg.body();
        try {
            if (!peopleMap.containsKey(people.getString("id"))) {
                log.info("ADD " + people.getString("name"));
                peopleMap.put(people.getString("id"), PeopleModel.builder()
                        .id(people.getString("id"))
                        .name(people.getString("name"))
                        .age(people.getInteger("age"))
                        .address(people.getString("address"))
                        .build());
                msg.reply(null);
            } else {
                msg.fail(500, new RuntimeException().getMessage());
            }
        } catch (RuntimeException e) {
            msg.fail(500, e.getMessage());
        }
    }

    private void getPeopleById(Message<String> msg) {
        String id = msg.body();
        try {
            if (peopleMap.containsKey(id)) {
                msg.reply(createPeople(peopleMap.get(id)));
                log.info("GET " + peopleMap.get(id).getName());
            }
        } catch (RuntimeException e) {
            msg.fail(404, e.getMessage());
        }
    }

    private void getAllPeople(Message<String> msg) {
        try {
            msg.reply(new JsonArray(peopleMap.values().stream()
                    .map(this::createPeople).collect(Collectors.toList())));
            log.info("GET ALL");
        } catch (RuntimeException e) {
            msg.fail(404, e.getMessage());
        }
    }

    private JsonObject createPeople(PeopleModel people) {
        return new JsonObject()
                .put("id", people.getId())
                .put("name", people.getName())
                .put("age", people.getAge())
                .put("address", people.getAddress());
    }

    private void hello(Message<String> msg) {
        String msgBody = msg.body();
        System.out.println(msgBody + " from B");
        HttpRequest<String> request = WebClient.create(vertx)
                .get(8080, "localhost", "/api/hello/" + msgBody)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string());

        Promise<String> promise = Promise.promise();

        request.send(asyncResult -> {
            if (asyncResult.succeeded()) {
                HttpResponse<String> response = asyncResult.result();
                System.out.println(response.toString() + " from C");
                msg.reply("msg from C: " + response.body());
                promise.complete(asyncResult.result().toString());
            } else {
                promise.fail(asyncResult.cause());
            }
        });
    }
}
