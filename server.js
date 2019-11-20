var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var mysql = require('mysql');
var multer = require('multer');
var storage;
var path = require('path');
var crypto = require('crypto');
var fs = require('fs');

storage = multer.diskStorage({
    destination: './uploads/',
    filename: function(req, file, cb) {
      return crypto.pseudoRandomBytes(16, function(err, raw) {
        if (err) {
          return cb(err);
        }
        return cb(null, "" + (raw.toString('hex')) + (path.extname(file.originalname)));
      });
    }
  });

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

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
        var st = position.split(',');
        console.log(req.params.position);
        //console.log(st[0]);
        var id = st[0];
        var password = st[1];
        var telenumber = st[2];
        var email = st[3];
        var name = st[4];
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
            //console.log(login);
            if(login == 0){
                connection.query('Insert into tb(name, password, telenumber, email, realname, can) values(?, ?, ?, ?, ?, ?)',[id, password, telenumber, email, name, 100], function(err, rows, fields) {});
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

app.get('/id/:position',function(req, res){
    var id = req.params.position;
    var telenumber;
    var result;

    connection.query('SELECT * from tb where name=?',[id],function(err, rows){
        if(!err){
            user = rows[0];
            result = {telenumber: user.telenumber};
            console.log(result);
            res.json(result);
        }
    })
})

app.post('/task',function(req, res){
    var result = req.body.task;
    result = {check: true};
    res.json(result);
    console.log(result);
})

app.post('/images/upload',multer({storage: storage}).single('image'),function(req,res){
    var result;
    var id = req.body.id;

    console.log(req.file.filename);
    var img = fs.readFileSync(__dirname + "/uploads/" + req.file.filename);

    connection.query('UPDATE tb SET img=? WHERE name=?',[req.file.filename, id],function(err, rows){
        if(!err){
            result = {check: true};
            res.json(result);
        }
    })
})

app.get('/myinfo/:position',function(req,res){
    var id = req.params.position;
    
    connection.query('SELECT * from tb where name=?',[id],function(err,rows){
        if(!err){ 
            var str, result;
            fs.readFile(__dirname +"/uploads/" + rows[0].img,function(err, data){
                if(!err){
                    var name = rows[0].realname;
                    var can = rows[0].can;
                    str = bin2String(data);
                    result = {name: name, path: str, can: can};
                    res.json(result);
                }
                else{
                    var name = rows[0].realname;
                    var can = rows[0].can;
                    result = {name: name, path: null, can: can};
                    res.json(result);
                }
            })
           
        }
    })
})
 
app.listen(3002, function() {
        console.log('Example app listend on port 3002!');
});

function bin2String(array) {
    return String.fromCharCode.apply(String, array);
}