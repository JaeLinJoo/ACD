var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var ejs = require('ejs');
var mysql = require('mysql');

var connection = mysql.createConnection({
    //connectionLimit: 10,
    host: 'localhost',
    port: '3306',
    user: 'root',
    password: 'asnalr34',
    database: 'mydb',
    debug: false
}); 
 
app.get('/test/:position', function(req, res) {
//받은 position 수많큼 객체를 생성해서 JSON 배열로 리턴
        var position = req.params.position;
        var st = position.split(',')
        //console.log(req.params.position);
        //console.log(st[0]);
        var id = st[0];
        var password = st[1];
        var result;
        
        //results.push({id: id, password: content});
    
        connection.query('SELECT * from tb', function(err, rows, fields) {
        if (!err)
        {
            var login = 0;
            rows.forEach(function(i){
                if(id==i.name){
                  login=1;
                }
            });
            console.log(login);
            if(login == 0){
                connection.query('Insert into tb(name, password) values(?, ?)',[id, password], function(err, rows, fields) {});
                result = {check: true};       
                console.log(result);
                res.json(result);
            }
            else{
                result = {check: false};
                console.log(result);
                res.json(result);
            }
        }
        });
        
});

app.get('/login/:position', function(req, res){
    var position = req.params.position;
    var st = position.split(',')
    var id = st[0];
    var password = st[1];
    var result;

    connection.query('SELECT * from tb', function(err, rows){
        var login = 0;
        rows.forEach(function(i){
            if(id==i.name&&password==i.password){
                login = 1;
            }
        });
        if(login){
            result = {check: true};       
            console.log(result);
            res.json(result);
        }
        else{
            result = {check: false};
            console.log(result);
            res.json(result);
        }
    });
});
 
app.listen(3002, function() {
        console.log('Example app listend on port 3002!');
});
