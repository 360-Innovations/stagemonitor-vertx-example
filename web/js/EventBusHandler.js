var EventBusHandler = function () {
    var bus;
    var eventBusHandlerState = vertx.EventBus.CLOSED;

    function init(){
        bus = new vertx.EventBus("/eventbus");
        bus.onopen = function () {
            bus.sockJSConn;
            eventBusHandlerState = vertx.EventBus.OPEN;
        };
        bus.onclose = function () {
            console.warn("Eventbus closed");
            eventBusHandlerState = vertx.EventBus.CLOSED;
        };
    }

    function send(address, message, replyHandler, failureHandler) {
        if(eventBusHandlerState === vertx.EventBus.OPEN){
            bus.send(address, message, replyHandler, failureHandler);
        }
    }


    return{
        init:init,
        send:send
    }
}();
