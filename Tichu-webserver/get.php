<?
if( !isset( $_REQUEST["function"] ) ){
	exit("f");
}
/*else if( !isset( $_REQUEST["tableId"] ) ){
	exit("t");
}
else if( !isset( $_REQUEST["myId"] ) ){
	exit("id");
}

if( isset($_REQUEST['tableId']) && isset($_REQUEST['myId']) ){
	if( $_REQUEST['myId'] < 4 && $_REQUEST['myId'] >= 0 ){
		include_once ("valid.php");	
	}
}

*/



$f = $_REQUEST["function"];
$t = $_REQUEST["tableId"];
$id = $_REQUEST["myId"];

include_once("functions.php");

switch( $f ){
	case "getNames":
		getNames($t,$id);
		break;
	case "drawCards":
		drawCards($t , $id);
		break;
	case "getRandomTable":
		getRandomTable();
		break;
	case "getMyId":
		if( !isset($_REQUEST['originalID']) ){
			exit( "e&NO ORIGINAL ID SET!WHO ARE U? ");
		}
		$orid = $_REQUEST['originalID'];
		getMyId($t,$orid);
		break;
	case "getTradeSet":
		getTradeSet($t , $id);
		break;
	case "getState":
		getState($t,$id);
		break;
	case "getCurrent":
		getCurrent($t,$id);
		break;	
	case "findPlayer":
		findPlayer($t);
		break;
	case "getCard":
		getCard($id);
		break;
	case "getScore":
		getScore($t);
		break;
	case "login":
		login();
		break;
	default:
		exit("fm");
		break;	
}




?>