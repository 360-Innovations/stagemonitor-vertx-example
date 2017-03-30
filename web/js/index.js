
document.addEventListener('DOMContentLoaded', function(){
    DOM.queryFirst('#btnLogin').addEventListener('click', function () {
        var username = DOM.queryFirst('#txtUsername').value;
        var password = DOM.queryFirst('#txtPassword').value;

        NET.post({
            url: window.location + 'login',
            headers:[
                {header: 'Content-Type', value: 'application/json; charset=UTF-8'}
            ],
            success: function (req) {
                var response = JSON.parse(req.response);
                if(response.code === 200){
                    window.location += "pages/mainPage.html"
                }
                else{
                    alert(response.result);
                }
            },
            data: JSON.stringify({username: username, password: password})
        });

    })
});