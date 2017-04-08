create user 'thunderbolt'@'%' identified by '';
GRANT ALL PRIVILEGES ON * . * TO 'thunderbolt'@'%';

create database student;
use student;


	create table result(
	REVDT varchar(15),
	DIST varchar(15),
	DIST_NAME varchar(15),
	SCHL varchar(15),
	REGNO varchar(15),
	NAME varchar(15),
	SEX varchar(15),
	DOB varchar(15),
	COM varchar(15),
	RELIGION varchar(15),
	PH_HAND varchar(15),
	MED varchar(15),
	SMED varchar(15),
	MARK01 varchar(15),
	MARK02 varchar(15),
	MARK03 varchar(15),
	MARK04 varchar(15),
	THEO1 varchar(15),
	PRAC1 varchar(15),
	MARK05 varchar(15),
	TOTAL varchar(15),
	PASS varchar(15),
	WITHD varchar(15),
	SCH_NAME varchar(35) ,
	TMRCODE varchar(10));



./mysqlimport --ignore-lines=1 --fields-terminated-by=, --verbose --local  -u thunderbolt -p -h 139.59.39.173 student /home/thunderbolt/gitRepos/DB-retrival/DataSet/result.csv