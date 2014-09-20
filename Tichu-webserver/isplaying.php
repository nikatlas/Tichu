<?
include_once("lib.php");

$tid = $_REQUEST['tableId'];


$table = new Table();
$table->get($tid);

echo $table->isPlaying();
?>