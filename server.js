var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var mysql = require('mysql');
var multer = require('multer');
var storage;
var path = require('path');
var crypto = require('crypto');
var fs = require('fs');
let Duplex = require('stream').Duplex;
function bufferToStream(buffer){
    let stream = new Duplex();
    stream.push(buffer);
    stream.push(null);
    return stream;
}

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

app.post('/task',function(req, res){
    var result = req.body.task;
    result = {check: true};
    res.json(result);
    console.log(result);
})

app.post('/images/upload',multer({storage: storage}).single('image'),function(req,res){
    var result;
    var id = req.body.id;

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
                    
                    result = {name: name, path: data.toJSON().data, can: can};
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
    var can = parseInt(req.body.can);
    var ss = req.body.objectives.split(';');
    connection.query('SELECT * from team where name = ?',[req.body.teamName],function(err, rows){
        if(!err){
            if(!rows[0]){
                connection.query('Insert into team(name, objective, objectives, admit, pay, time, intro, start, end, mentor, member_count, category1, category2, img, leader, user, state, current) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',[req.body.teamName, req.body.objective, req.body.objectives, req.body.admit, pay, req.body.time, req.body.intro, req.body.start, req.body.end, req.body.mentor, count, req.body.category1, req.body.category2, req.file.filename, id, id, '1', 1]);
                connection.query('Insert into teamUserInfo(id, name, can) values(?, ?, ?)',[id, req.body.teamName, can]);
                for(var i = 0; i < ss.length; i++){
                    connection.query('Insert into teamObjective(id, name, objective, isadmit, img) values(?, ?, ?, ?, ?)',[id, req.body.teamName, ss[i], '인증 필요', 'null']);
                }
                connection.query('SELECT * from tb where name=?',[id],function(err, rows){
                    if(!err){
                        connection.query('UPDATE tb SET can = ? where name = ?',[rows[0].can - can, id]);
                    }
                })
                result = {check: true};
            }
            else{
                result = {check: false};
            }
            res.json(result);
        }
    })
})

app.post('/admit',multer({storage: storage}).single('image'),function(req, res){
    var result;
    var id = req.body.id;
    var name = req.body.teamname;
    var obj = req.body.objective;

    connection.query('UPDATE teamObjective SET img = ? where id =? AND name =? AND objective =?',[req.file.filename ,id, name, obj])
    connection.query('UPDATE teamObjective SET isadmit = ? where id =? AND name =? AND objective =?',['인증 됨',id, name, obj])
    result={check: true, message:'인증 완료'};
    res.json(result);
})

app.post('/showAdmit',function(req,res){
    var result;
    var id = req.body.id;
    var name = req.body.teamname;
    var obj = req.body.objective;

    connection.query('SELECT * from teamObjective where id = ? AND name = ? AND objective = ?',[id, name, obj],function(err, rows){
        if(!err){
            if(rows[0].img == 'null'){
                str = null;
            }
            else{
                data = fs.readFileSync(__dirname +"\\uploads\\" + rows[0].img);
                data.toJSON().data;
            }
            result = {img: str, isadmit: rows[0].isadmit};
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
                var mentor = rows[i].pay.toString();
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
                str = data.toJSON().data;
                
                result.push({name: name, content: content, mainimg: str, category1: category1, category2: category2, state: state, count: count, user: user, mentor_pay: mentor, mentor: rows[i].mentor});
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
            str = data.toJSON().data;
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
            var lock = 0;
            var n = rows[0].user.split(';');
            for(var i = 0;i<n.length;i++){
                if(n[i]==id){
                    lock = 1;
                }
            }
            if(rows[0].mentor == '0' && req.body.isMentor==true){//멘토가 필요없으면 false
                result = {check: false, message: '멘토가 필요없는 소모임 입니다!'};
                res.json(result);
            }
            else if(lock == 1){//이미 팀에 속하면 false
                result = {check: false, message: '이미 소모임에 가입 하셨습니다!' };
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
                    
                    var objs = rows[0].objectives.split(';');//개인별 목표 db만들기
                    for(var i =0; i<objs.length; i++){
                        connection.query('INSERT into teamObjective(id, name, objective, isadmit, img) values (?, ?, ?, ?, ?)',[id, req.body.teamname, objs[i], '인증 필요', 'null']);
                    }

                    connection.query('SELECT * from tb where name=?',[id],function(err, rows){//캔 차감
                        if(!err){
                            connection.query('UPDATE tb SET can = ? where name = ?',[rows[0].can - req.body.can, id]);
                        }
                    })
                    
                    result = {check: true};
                    res.json(result);
                }
                else{//팀 인원이 꽉찼을 경우
                    result = {check: false, message: '소모임 인원이 꽉찼습니다.'};
                    res.json(result);
                }
            }
        }
    })
})


app.post('/getGroupId',function(req,res){
    var groupid=req.body.targetGroup;
    var id = req.body.targetID;
    var result;
    var can;
    connection.query('SELECT * from teamUserInfo where id = ? AND name = ?',[id,groupid],function(err, rows){
        can = rows[0].can;
    })
    connection.query('SELECT * from team where name=?',[groupid],function(err,rows){
        if(!err){
            var group=rows[0];
            var category = group.category1 + ' / ' + group.category2;
            data = fs.readFileSync(__dirname +"\\uploads\\" + rows[0].img);
            str = data.toJSON().data;
            result={
                category: category,
                can: can,
                objectives:group.objectives,
                img: str
            };
            res.json(result);
        }
    })
})

app.post('/admitlist',function(req, res){
    var name = req.body.teamname;
    var result = [];

    connection.query('SELECT * from teamObjective where name = ?',[name],function(err, rows){
        for(var i = 0;i < rows.length;i++){
            if(rows[i].img=='null'){
                str = null;
            }
            else{
                data = fs.readFileSync(__dirname +"\\uploads\\" + rows[i].img);
                str = data.toJSON().data;
            }
            result.push({img: str, id: rows[i].id, objective: rows[i].objective})
        }
        res.json(result);
    })
})

app.post('/calculateObjective',function(req, res){
    var result;
    var id = req.body.id;
    var name = req.body.teamname;
    var yesin = 0;
    var noin = 0;
    var yesgr = 0; 
    var nogr = 0;

    connection.query('SELECT * from teamObjective where name = ?',[name],function(err, rows){
        if(!err){
            for(var i = 0; i < rows.length; i++){
                if(rows[i].id==id){
                    if(rows[i].isadmit == '인증 됨'){
                        yesin++;
                    }
                    else{
                        noin++;
                    }
                }
                if(rows[i].isadmit == '인증 됨'){
                    yesgr++;
                }
                else{
                    nogr++;
                }
            }
            var individual = parseInt(yesin*100 / (yesin+noin));
            var group = parseInt(yesgr*100 / (yesgr+nogr));
            connection.query('SELECT * from teamAttend where name = ? AND state = ?',[name, '마감'],function(err, rows){
                if(!err){
                    if(rows.length==0){
                        result = {individual: individual, group: group, aindividual: 0, agroup: 0};
                        res.json(result);
                    }
                    else{
                        var date = 0;
                        var indi, grou;
                        var gr = 0;
                        for(var i = 0; i < rows.length; i++){
                            if(rows[i].user.match(id)==id){
                                date++;
                            }
                            gr = gr + rows[i].value;
                        }
                        indi = parseInt(date*100/rows.length);
                        grou = parseInt(gr / rows.length);
                        result = {individual: individual, group: group, aindividual: indi, agroup: grou};
                        res.json(result);
                    }
                }
            })

            //result = {individual: parseInt(individual), group: parseInt(group)};
        }
    })
})

app.get('/getcan/:id',function(req, res){
    var id = req.params.id;
    var result;
    connection.query('SELECT * from tb where name=?',[id],function(err, rows){
        if(!err){
            result={can: rows[0].can}
            res.json(result);
        }
    })
})

app.get('/testimg/:id',function(req, res){
    var data = fs.readFileSync(__dirname +"\\uploads\\" + 'asdf.jpg');
    console.log(data.buffer);
    var result = {type: 'String', data: data.buffer};
    res.json(result);
})

app.post('/addday',function(req, res){
    var name = req.body.name;
    var date = req.body.date;
    var time = req.body.time;

    connection.query('SELECT * from team where name = ?',[name],function(err, rows){
        if(!err){
            var d = rows[0].date;
            var t = rows[0].time1;
            if(d==null){
                d = date;
                t = time;
            }
            else{
                d = d+';'+date;
                t = t+';'+time;
            }
            connection.query('UPDATE team SET date = ?, time1 = ? where name = ?',[d, t, name]);
        }
    })
    connection.query('INSERT into teamAttend(name, date, state, time) values (?, ?, ?, ?)',[name, date, '예정', time]);
    res.json({check: true, message: '일정 추가'});
})
 
app.post('/showdate',function(req, res){
    var name = req.body.name;
    var result = [];

    connection.query('SELECT * from teamAttend where name = ?',[name],function(err, rows){
        if(!err){
            for(var i = 0; i<rows.length; i++){
                var str;
                if(rows[i].img==null){
                    str = null;
                }
                else{
                    data = fs.readFileSync(__dirname +"\\uploads\\" + rows[i].img);
                    str = data.toJSON().data;
                }
                result.push({img: str,
                            date: rows[i].date,
                            time: rows[i].time,
                            user: rows[i].user,
                            state: rows[i].state})
            }
            res.json(result);
        }
    })
})

app.post('/getusers',function(req, res){
    var name = req.body.name;
    var result;

    connection.query('SELECT * from team where name = ?',[name],function(err, rows){
        if(!err){
            result = {users: rows[0].user};
        }
        res.json(result);
    })
});

app.post('/getattend', function(req, res){
    var name = req.body.name;
    var date = req.body.date;
    var result;

    connection.query('SELECT * from teamAttend where name = ? AND date = ?',[name, date],function(err, rows){
        if(!err){
            var str;
            if(rows[0].img==null){
                str = null;
            }
            else{
                data = fs.readFileSync(__dirname +"\\uploads\\" + rows[0].img);
                str = data.toJSON().data;
            }

            result = {img: str, user: rows[0].user};
            res.json(result);
        }
    })
})

app.post('/uploadAttendImg',multer({storage: storage}).single('image'),function(req, res){
    var img = req.file.filename;
    var name = req.body.name;
    var date = req.body.date;
    var result;

    connection.query('UPDATE teamAttend SET img = ?, state = ? where name = ? AND date = ?',[img, '마감', name, date]);
    result = {check: true, message: '업로드 성공'};
    res.json(result);
})

app.post('/updateattend',function(req, res){
    var name = req.body.name;
    var date = req.body.date;
    var user = req.body.user;
    
    connection.query('UPDATE teamAttend SET user = ? where name = ? AND date = ?', [user, name, date]);
    connection.query('SELECT * from team where name = ?', [name], function(err, rows){
        if(!err){
            var value;
            var n = 0;
            var users = rows[0].user.split(';');

            for(var i = 0; i < users.length; i++){
                if(user.match(users[i])==users[i]){
                    n++;
                }
            }
            value = parseInt(n*100 / users.length);
            
            connection.query('UPDATE teamAttend SET value = ? where name = ? AND date = ?', [value, name, date]);
        }
    })
    result = {check: true, message: '수정 성공'};
    res.json(result);
})

app.post('/calculateAttend',function(req, res){
    var id = req.body.id;
    var name = req.body.name;
    var result;

    connection.query('SELECT * from teamAttend where name = ? AND state = ?',[name, '마감'],function(err, rows){
        if(!err){
            if(rows.length==0){
                result = {individual: 0, group: 0};
                res.json(result);
            }
            else{
                var date = 0;
                var indi, group;
                var gr = 0;
                for(var i = 0; i < rows.length; i++){
                    if(rows[i].user.match(id)==id){
                        date++;
                    }
                    gr = gr + rows[i].value;
                }
                indi = parseInt(date*100/rows.length);
                group = parseInt(gr / rows.length);
                result = {individual: indi, group: group};
                res.json(result);
            }
        }
    })
})

app.listen(3002, function() {
        console.log('Example app listend on port 3002!');
});
