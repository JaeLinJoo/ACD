ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'asnalr34';
create database mydb;
use mydb;
create table tb(
name char(10), 
password char(10)
);