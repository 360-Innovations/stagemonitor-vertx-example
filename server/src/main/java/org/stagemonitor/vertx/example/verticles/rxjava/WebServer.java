package org.stagemonitor.vertx.example.verticles.rxjava;


import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.handler.impl.StaticHandlerImpl;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;

public class WebServer extends BaseVerticle {
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_ADDRESS = "0.0.0.0";
    public static final String DEFAULT_WEB_ROOT = "web";
    public static final String DEFAULT_INDEX_PAGE = "index.html";
    public static final boolean CACHING_ENABLED = true;

    private Router router;

    @Override
    public void start() throws Exception {
        super.start();
        logger = LoggerFactory.getLogger("WebServer");

        router = Router.router(this.vertx);

        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/login").handler(new LoginHandler());

        if(getOptionalBooleanConfig("bridge", false)){
            SockJSHandler sockJSHandler = SockJSHandler.create(vertx);

            BridgeOptions options = new BridgeOptions();
            options.addInboundPermitted(new PermittedOptions(){{
                setAddressRegex(".*");
            }});
            options.addOutboundPermitted(new PermittedOptions(){{
                setAddressRegex(".*");
            }});

            sockJSHandler.bridge(options);

            router.route("/eventbus/*").handler(sockJSHandler);
        }

        router.route().handler(staticHandler());

        HttpServer server = vertx.createHttpServer(new HttpServerOptions(){{
            setMaxWebsocketFrameSize(Integer.MAX_VALUE);
            setCompressionSupported(true);
        }});

        server.requestHandler(it -> router.accept(it)).listen(
            getOptionalIntConfig("port", DEFAULT_PORT),
            getOptionalStringConfig("host", DEFAULT_ADDRESS),
            ar -> {
                if(!ar.succeeded()) {
                    logger.error(ar.cause().toString());
                }
            }
        );
    }

    private StaticHandler staticHandler() {
        return new StaticHandler(new StaticHandlerImpl(){{
            setWebRoot(getOptionalStringConfig("web_root", DEFAULT_WEB_ROOT));
            setIndexPage(getOptionalStringConfig("index_page", DEFAULT_INDEX_PAGE));
            setCachingEnabled(getOptionalBooleanConfig("caching", CACHING_ENABLED));
        }});
    }
}
