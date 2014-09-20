<?
include_once("lib.php");

$tid = $_REQUEST['tableId'];


$table = new Table();
$table->get($tid);

$a = $table->data("ids");
$r = explode('|',$a);		
connect();
for($i=0;$i<4;$i++){
	$user = new User();
	$user->get($r[$i]);
	$diff = strtotime($user->data("date")) - time();
	echo $diff." ";
}


?>