<?
if( !isset( $_REQUEST["function"] ) ){
	exit("f");
}
else if( !isset( $_REQUEST["tableId"] ) ){
	exit("t");
}
else if( !isset( $_REQUEST["myId"] ) ){
	exit("id");
}
else{
	$f = $_REQUEST["function"];
	$t = $_REQUEST["tableId"];	
	$id = $_REQUEST["myId"];
}
include_once("functions.php");

switch( $f ){
	case "mt":
		performTrades($t);
		break;
	case "bomb":
		bomb($t,$id);
		break;
	case "makeMove":
		makeMove($t , $id);
		break;
	case "setTradeSet":
		setTradeSet($t , $id);
		break;
	case "setState":
		setState($t); 
		break;
	case "sendDragon":
		sendDragon($t,$id);  
		break;		
	case "performTrades":
		performTrades($t); 
		break;
	case "setHand":
		setHand($t,$id);
		break;
	case "callTichu":
		setTichu($t,$id);
		break;
	case "fold":
		fold($t,$id);
		break;
	case "destroy":
		destroy($t , $id);
		break;	
	case "leaveSeat":
		leaveSeat($t , $id);
		break;		
	case "register":
		register();
		break;
	default: 
		exit("fm&|".$f."|");
		break;	
}

