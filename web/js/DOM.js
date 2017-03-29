var DOM = function(){
    function queryFirst(selector, start){
        var start = start || document;
        return start.querySelector(selector);
    }

    function queryLast(selector, start){
        var start = start || document;
        var nodelist = start.querySelectorAll(selector);
        return nodelist[nodelist.length-1];
    }

    function queryAll(selector, start){
        var start = start || document;
        return nodeListToArray(start.querySelectorAll(selector));
    }

    function nodeListToArray(nl){
        var arr = [];
        for(var i=-1,l=nl.length;++i!==l;arr[i]=nl[i]); //m/thode la plus rapide pour convertir un nodelist en array
        return arr;
    }

    return {
        queryFirst: queryFirst,
        queryLast: queryLast,
        queryAll: queryAll
    }
}();