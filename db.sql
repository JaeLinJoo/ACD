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
img char(50)
);