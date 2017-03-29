var NET = function(){
    function get(config) {
        var request = new XMLHttpRequest();
        request.open('GET', config.url, true);
        config.headers && config.headers.forEach(function (header) {
            request.setRequestHeader(header.header,header.value);
            request.onreadystatechange = function() {
                if (this.readyState === 4) {
                    if (this.status >= 200 && this.status < 400) {
                        config.success && config.success(this);
                    } else {
                        config.error && config.error(this);
                    }
                }
            };
        });
        request.send();
    }

    function post(config) {
        var request = new XMLHttpRequest();
        request.open('POST', config.url, true);
        config.headers && config.headers.forEach(function (header) {
            request.setRequestHeader(header.header,header.value);
            request.onreadystatechange = function() {
                if (this.readyState === 4) {
                    if (this.status >= 200 && this.status < 400) {
                        config.success && config.success(this);
                    } else {
                        config.error && config.error(this);
                    }
                }
            };
        });
        request.send(config.data);
    }

    return {
        get:get,
        post:post
    }
}();
