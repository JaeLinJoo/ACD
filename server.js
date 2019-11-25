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

app.post('/postteam',multer({storage: storage}).single('image'),function(req, res){
    var result;
    var id = req.body.id;
    var pay = parseInt(req.body.pay);
    var count = parseInt(req.body.member_count);
    connection.query('SELECT * from team where name = ?',[req.body.teamtheme],function(err, rows){
        if(!err){
            if(!rows[0]){
                connection.query('Insert into team(name, objective, objectives, admit, pay, time, intro, start, end, mentor, member_count, category1, category2, img, leader, user, state, current) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',[req.body.teamName, req.body.objective, req.body.objectives, req.body.admit, pay, req.body.time, req.body.intro, req.body.start, req.body.end, req.body.mentor, count, req.body.category1, req.body.category2, req.file.filename, id, id, '1', 1]);
                result = {check: true};
            }
            else{
                result = {check: false};
            }
            res.json(result);
        }
    })
})

app.get('/showTeamList/:position',function(req, res){
    var result=[];
    var id = req.params.position;
    connection.query('SELECT * from team', function(err, rows){
        if(!err){
            for(var i = 0; i < rows.length; i++){
                var state;
                var name = rows[i].name;
                var content = rows[i].intro;
                var category1 = rows[i].category1;
                var category2 = rows[i].category2;
                var user = rows[i].user;
                if(rows[i].state=='1'){
                    state = '모집중';
                }
                if(rows[i].state=='2'){
                    state = '진행중'
                }
                var s = rows[i].user.split(';');
                var count = s.length.toString()+' / '+rows[i].member_count;
                var str, data;
                
                data = fs.readFileSync(__dirname +"\\uploads\\" + rows[i].img);
                str = bin2String(data);
                
                result.push({name: name, content: content, mainimg: str, category1: category1, category2: category2, state: state, count: count, user: user});
            }
            res.json(result);
        }
    })
})

app.get('/join/:position',function(req, res){
    var name = req.params.position;
    var result;

    connection.query('SELECT * from team where name = ?',[name], function(err, rows){
        if(!err){
            var peroid = rows[0].start +' ~ ' +rows[0].end;
            var s = rows[0].user.split(';');
            var count = s.length.toString() + ' / ' + rows[0].member_count.toString();
            data = fs.readFileSync(__dirname +"\\uploads\\" + rows[0].img);
            str = bin2String(data);
            result = {teamname: rows[0].name,
                    member_count: count,
                    category1: rows[0].category1,
                    category2: rows[0].category2,
                    teamname1: rows[0].name,
                    intro: rows[0].intro,
                    peroid: peroid,
                    obj: rows[0].objective,
                    admit: rows[0].admit,
                    mentor_pay: rows[0].pay.toString(),
                    time: rows[0].time,
                    objlist: rows[0].objectives,
                    img: str,
                    ismentor: rows[0].mentor}
                
            res.json(result);
        }
    })
})

app.post('/submit',function(req, res){
    var id = req.body.id;
    var result;

    connection.query('SELECT * from team where name = ?',[req.body.teamname], function(err, rows){
        if(!err){
            if(rows[0].mentor == '0' && req.body.isMentor==true){//멘토가 필요없으면 false
                result = {check: false};
                res.json(result);
            }
            else{
                var user = rows[0].user;
                var users = user.split(';');
                if(users.length!=rows[0].member_count){//팀등록 성공
                    user = user + ';' + id;
                    connection.query('UPDATE team SET user = ? where name = ?',[user, req.body.teamname]);//팀의 유저 리스트 등록
                    connection.query('SELECT * from tb where name = ?',[id], function(err, rows){
                        if(!err){
                            var teamn1;
                            var teamn = rows[0].team;
                            if(teamn==null){
                                teamn1 = req.body.teamname;
                            }
                            else{
                                teamn1 = teamn +';'+ req.body.teamname;
                            }
                            connection.query('UPDATE tb SET team = ? where name = ?',[teamn1, id]);//유저의 팀 리스트 등록
                        }
                    })
                    if(req.body.isMentor){//멘토면 멘토등록
                        connection.query('UPDATE team SET mentorname = ? where name = ?',[id, req.body.teamname]);
                        connection.query('UPDATE team SET mentor = ? where name = ?',['0', req.body.teamname]);
                    }

                    if((users.length+1)==rows[0].member_count){//꽉 찼을시
                        connection.query('UPDATE team SET state = ? where name = ?', ['2', req.body.teamname]);
                    }
                    
                    connection.query('INSERT into teamUserInfo(id, name, can) values (?, ?, ?)',[id, req.body.teamname, req.body.can]);//팀 정보에 유저가 입력한 캔 수 업데이트
                    
                    result = {check: true};
                    res.json(result);
                }
                else{//팀 인원이 꽉찼을 경우
                    result = {check: false};
                    res.json(result);
                }
            }
        }
    })
})
 
app.listen(3002, function() {
        console.log('Example app listend on port 3002!');
});

function bin2String(array) {
    return String.fromCharCode.apply(String, array);
}