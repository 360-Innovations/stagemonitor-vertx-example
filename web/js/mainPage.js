document.addEventListener('DOMContentLoaded', function(){
    EventBusHandler.init();

    DOM.queryFirst('#btnSearch').addEventListener('click', function () {
        var bookName = DOM.queryFirst("#txtSearch").value;
        var json = {
            action: "getBookInfo",
            name: bookName
        };
        EventBusHandler.send('example.bookService', json, function (response) {
            console.log(response);
            if(!response.code){
                DOM.queryFirst("#bookName").innerHTML = response.name;
                DOM.queryFirst("#bookAuthor").innerHTML = response.author;
                DOM.queryFirst("#bookPrice").innerHTML = response.price.toFixed(2) + "$";

                generateAvailabilitiesTable(response.availabilities);
                DOM.queryFirst("#bookInfo").style.display = 'block';
            }
            else{
                alert(response.result.message);
            }
        });
    });
});

function generateAvailabilitiesTable(avail) {
    var availTable = DOM.queryFirst('tbody', DOM.queryFirst("#availabilities"));
    availTable.innerHTML = '';

    for(var city in avail){
        var row = document.createElement('tr');
        var cityCol = document.createElement('td');
        var countCol = document.createElement('td');

        cityCol.innerHTML = city;
        countCol.innerHTML = avail[city];
        countCol.classList.add('centered');
        row.appendChild(cityCol);
        row.appendChild(countCol);

        availTable.appendChild(row)
    }
}
