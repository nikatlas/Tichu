<?

include_once("lib.php");

$id = $_REQUEST['myId'];
$tid = $_REQUEST['tableId'];

$table = new Table();
$table->get($tid);

$a = $table->data("ids");
$r = explode('|',$a);		
connect();
$user = new User();
$user->get($r[$id]);
$diff = strtotime($user->data("date")) - time();
$user->update();
echo $diff;

?>