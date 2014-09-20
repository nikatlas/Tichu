<?php
$con = "";
function connect($ip="localhost:3306",$username="schoolik_tichu",$password="akis0349",$database="schoolik_tichu"){
//function connect($ip="localhost:3306",$username="qualit42_wpz",$password="zak!24#$",$database="qualit42_admin"){
	
	$con = mysql_connect($ip,$username,$password);
	if(!$con){
	echo "CONNECT ERROR";
  	return -1;
	}	
	$db =  mysql_select_db($database,$con);
	if(!$db){
	echo "DB";
  	return -2;
	}
	return 0;
}

function disconnect(){
	mysql_close($con);
}

function query($query){
	$q_s = mysql_query($query);
	if(!$q_s){
  	return -1;
	}
	return 0;	
}

function select($column="*",$table,$where){
	if($where==""){$where = 1;}	
	 	$q = mysql_query("SELECT $column FROM $table WHERE $where");
	
	if(!$q){
  	return -1;
	}

	return 	mysql_fetch_array($q);
}
	
function insert($table,$columns,$values){
	$q = mysql_query("INSERT INTO $table ($columns) VALUES ($values)");
	if(!$q){
  	return -1;
	}
	return 	0;
}

function update($table,$set,$where){
	$q = mysql_query("UPDATE $table SET $set WHERE $where");
	if(!$q){
  	return -1;
	}
	return 	0;
}

function delete($table,$where){
	$q = mysql_query("DELETE FROM $table  WHERE $where");
	if(!$q){
  	return -1;
	}
	return 	0;
}
function q_gr(){
	$q_s = mysql_query("SET NAMES GREEK");
	if(!$q_s){
  	return -1;
	}
	return 0;	
}

?>
