package org.stagemonitor.vertx.example.verticles.rxjava;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
    }

    @Override
    public void start() throws Exception {
        super.start();
        System.out.println("starting server on " + Integer.toString(DEFAULT_PORT));
        JsonObject webServerConfig = new JsonObject("{"
                + "\"web_root\":\"web\","
                + "\"bridge\":true,"
                + "\"route_matcher\":true,"
                + "\"port\":" + DEFAULT_PORT + ","
                + "\"index_page\":\"index.html\","
                + "\"sjs_config\": {"
                    + "\"prefix\":\"/eventbus\""
                + "},"
                + "\"inbound_permitted\":[ { \"address_re\":\".+\" } ]"
        + "}"
        );

        DeploymentOptions opt = new DeploymentOptions(){{
            setWorker(true);
            setHa(true);
            setConfig(webServerConfig);
        }};

        DeploymentOptions optWorker = new DeploymentOptions(){{
            setWorker(true);
            setHa(true);
        }};

        DeploymentOptions optHA = new DeploymentOptions(){{
            setHa(true);
        }};

        vertx.deployVerticle("org.stagemonitor.vertx.example.verticles.rxjava.DbService", optWorker);
        vertx.deployVerticle("org.stagemonitor.vertx.example.verticles.rxjava.BookService", optHA);
        vertx.deployVerticle("org.stagemonitor.vertx.example.verticles.rxjava.WebServer", opt);
    }
}
