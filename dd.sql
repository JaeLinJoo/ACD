ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'asnalr34';
drop database mydb;
create database mydb;
use mydb;
create table tb(
name char(10), 
password char(10),
telenumber char(15),
email char(15),
realname char(10),
can int(4),
img char(50),
team char(200)
);

create table team(
name char(20),
objective char(50),
objectives char(200),
admit char(10),
pay int(4),
time char(20),
intro char(20),
start char(20),
end char(20),
mentor char(2),
member_count int(4),
category1 char(10),
category2 char(10),
img char(50),
leader char(15),
user char(200),
state char(2),
current int(4),
mentorname char(20)
);

create table teamUserInfo(
id char(20),
name char(20),
can int(4)
);

create table teamObjective(
id char(20),
name char(20),
objective char(30),
isadmit char(10),
img char(50)
);