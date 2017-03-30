# Stagemonitor Vertx Example

This project is a example project for the stagemonitor-vertx plugin. The project is very basic it's only a login page and a book search page. The goal is to show how the plugin work in a Vertx environment. This project have a Vertx vanilla version and a version with the Vertx RxJava plugin.

## Usage
To use the plugin correctly you must enter a valid elasticsearch url in stagemonitor.properties.

### Start vanilla version
To start the vanilla version you must run the server module with those config:
- Main Class: io.vertx.core.Starter
- Vm Options: -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4jLogDelegateFactory
- Program arguments: run org.stagemonitor.vertx.example.verticles.MainVerticle

### Start RxJava version
To start the vanilla version you must run the server module with those config:
- Main Class: io.vertx.core.Starter
- Vm Options: -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4jLogDelegateFactory
- Program arguments: run org.stagemonitor.vertx.example.verticles.rxjava.MainVerticle

### Use the app
To use the app go on [localhost:8080](http://localhost:8080)

The possible login option are:
- User: test1 Password: test1
- User: test2 Password: test2

The books available are:
- book1
- book2