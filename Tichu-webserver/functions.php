<?
include_once("lib.php");

function register(){
	if( !isset($_REQUEST['username']) || !isset($_REQUEST['password']) || !isset($_REQUEST['email']) )exit("e&NOT SET");
	connect();
	$user = new User();
	$user->set("username" , $_REQUEST['username']);
	$user->set("password" , md5($_REQUEST['password']));
	$user->set("email" , $_REQUEST['email']);
	$user->set("createdDate" , date("Y-m-d H:i:s") );
	
	$q = mysql_query("SELECT * FROM users WHERE username='".$_REQUEST['username']."' OR email='".$_REQUEST['username']."'");
	$r = mysql_fetch_array($q);
	if( $r != false )$p1 = "1";
	else $p1 = "0";	
	$q = mysql_query("SELECT * FROM users WHERE username='".$_REQUEST['email']."' OR email='".$_REQUEST['email']."'");
	$r = mysql_fetch_array($q);
	if( $r != false )$p2 = "1";
	else $p2 = "0";
	if( $p1 == "1" || $p2 == "1" ) exit($p1.$p2);
	
	$user->add();
	
	$_SESSION['id'] = $user->data("id");
	$_SESSION['username'] = $user->data("username");
	echo "00";echo $user->data("id");
	
}
function login(){
	if( !isset($_REQUEST['username']) || !isset($_REQUEST['password']) )exit("e&NOT SET");
	connect();
	$q = mysql_query("SELECT * FROM users WHERE username='".$_REQUEST['username']."' OR email='".$_REQUEST['username']."'");
	$r = mysql_fetch_array($q);
	if( $r['password'] == md5($_REQUEST['password']) ){
		$_SESSION['id'] = $r['id'];
		$_SESSION['username'] = $r['username'];
		$_SESSION['email'] = $r['email'];
		 echo "00";echo $r['id'];
	}
	else{
		exit("ee");	
	}
}
function bomb($t,$id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	if( $table->data("bomb") >=0 && $table->data("bomb") < 4 ){echo "e2";return;}
	$table->set("turn",$id);
	$table->set("bomb",$id);
	$table->set("state",$table->data("state")+1);
	$table->update();
	echo "00";
}
function leaveSeat($t , $id ){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	if( $id < 0 || $id > 3 ) {echo "e";return;}
	
	$ids = $table->data("ids");
	$ids = explode("|",$ids);
	$ids[$id] = "0";
	$ids = implode("|",$ids);
	
	$table->set("players" , $table->data("players") -1 );
	$table->set("seats" , $table->data("seats") +1 );
	if( $table->data("seats") == 4 )$table->drop();
	$table->set("ids",$ids);
	$table->update();
	echo "00";
}
function getNames($t,$myId){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}

	$a = $table->data("ids");
	$r = explode('|',$a);		
	connect();
	for($i=0;$i<4;$i++){
		$q = mysql_query("SELECT * FROM users WHERE id = ".$r[$i] );
		$s = mysql_fetch_array($q);
		$res[$i] = $s['username'];
	}
	$res = implode('|',$res);
	echo $res;
}
function getMyId($t,$orid){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	if( $table->state == "0" && $table->data("players") < 4 ){
		$nid = $table->getNewId($orid);
		//$_SESSION['myId'] = $nid;
		echo $nid;
		if( $table->data("players") == 3 ){
			$table->set("state" , 1 );
		}
		$table->set("players",$table->data("players")+1);
		$table->set("seats",$table->data("seats")-1);
		$table->update();
	}
	else{
		echo "e";
	}
}
function getRandomTable(){
	connect();
	$table = new Table(); 
	$r = select("*","tables","(seats > 0 OR players < 4) AND state = 0 ORDER BY seats ASC");
	if( $r ){
		$table->get($r['id']);
	}
	else{
		if( ! $table->add() ){echo "e";return;}
		$table->drawCards();
		$table->set("ids","0|0|0|0");
		$table->update(); 
	}
	if( $table->data("seats") > 4 ){echo "e&Alot seats!";return;}
	//$table->set("seats", ($table->data("seats")-1 <= 0)? 0 : ($table->data("seats")-1) );
	$table->set("state",0);
	$table->set("tichus","0000");
	$table->set("points","|||");
	$table->update();	
	echo $table->data("id"); 
}
function getState($t,$id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	
	if( isset($_REQUEST['sendState']) ){
		$ss = $_REQUEST['sendState'];
		switch( $ss ){
			case 5:
				reGetTradeSet($table,$id);
				$msg = "Trying to reget&ID:".$_SESSION['myId'];
				break;
		}
	}
	echo $table->state; 
	echo "&".$table->data("tichus")."&";
	echo $table->data("players")."&";
	echo $table->data("ids")."&";
	echo $msg;
}
function drawCards($t,$id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	if( $table->playercards == "" ){echo "e";return;}
	$temp = explode( "|" , $table->playercards );
	echo "0&";
	$r = explode(',',$temp[$id]);
	for($i=0;$i<14;$i++)echo $r[$i].'&';
	return;
}
function reGetTradeSet($table,$id){
	$r = $table->getTradeSet($id);
	if( sizeof($r) == 3 ){
		for($i=0;$i<4;$i++){
			$r[$i] = $table->getTradeSet($i);	
			$res[$i] = implode(',',$r[$i]);
		}
		$res[$id]='';
		$res = implode('|',$res);
		if( $table->data("setsgot") == 3 ){
			$table->set("state",3);	
			$table->findPlayer();
		}	
		$table->set("setsgot",$table->data("setsgot")+1);
		$table->set("getsets",$res);
		$table->update();	
		echo "CC&".$id."&".$table->data("getsets");
	}	
	return;
}
function getTradeSet($t , $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	$r = $table->getTradeSet($id);
	if( sizeof($r) != 3 ){echo "e&!Already Got or TradeSet wrong, SIZEOF";return;}
	echo "0&";
	for($i=0;$i<3;$i++)echo $r[$i]."&";
	for($i=0;$i<4;$i++){
		$r[$i] = $table->getTradeSet($i);	
		$res[$i] = implode(',',$r[$i]);
	}
	$res[$id]='';
	$res = implode('|',$res);
	if( $table->data("setsgot") == 3 ){
		$table->set("state",3);	
		$table->findPlayer();
	}	
	$table->set("setsgot",$table->data("setsgot")+1);
	$table->set("getsets",$res);
	$table->update();
	return;
}
function getCurrent($t,$id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	$s = "0&";
	for($i=0;$i<4;$i++){
		$arr = $table->getPlayerCards($i);
		$s.= sizeof($arr)."&";	
	}	
	$s.=$table->data("combination")."&";
	$s.=$table->data("handBy")."&";
	$s.=$table->data("currentHand")."&";
	$s.=$table->data("askedCard")."&";
	$s.=$table->data("turn")."&";
	$s.=$table->data("dragon")."&"; 
	$s.=$table->data("bomb")."&"; 	
	$s.=$table->data("pointsTo")."&"; 	
	$s.=$table->isPlaying()."&"; 	
	$s.=$table->getPlayerCardsCommas($id)."&";  
	$tp=explode('|',$table->data("teamPoints"));
	if( $table->data("teamPoints") == "" ){
		$tp[0] = 0;$tp[1] = 0;
	}
	$s.=$tp[0]."&".$tp[1]."&"; 
	$tich = $table->data("tichus");
	$s.=$tich[0].",".$tich[1].",".$tich[2].",".$tich[3];



	echo $s;
}
function getScore($t){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	$tp = explode("|",$table->data("teamPoints"));
	echo $tp[0].",".$tp[1];
}
function findPlayer($t){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}	
	$table->findPlayer();
	echo $table->data("turn");
}

//// SET FUNC // TODO
function setTradeSet($t , $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	if( !isset($_REQUEST["cards"]) ){echo "e2";return;}
	$c = $_REQUEST['cards'];
	$arr = explode(',' , $c );
	if( sizeof($arr) < 3 ){echo "e3";return;}
	$cards = $table->getPlayerCards($id);	
	for($i=0;$i<3;$i++){
		if( !in_array( $arr[$i],$cards ) ){echo "e4&Wrong Cards";return;}
	}
	$r = explode("|",$table->data("tradesets"));
	$r = $r[0]."|".$r[1]."|".$r[2]."|".$r[3];
	$r = explode("|",$r);
	if($r[$id] != "" ){if( $r[$id] == $c ){exit("0");}else{echo "e5&Already Set";return;}}
	$r[$id] = $c;
	$res = implode( '|' , $r );
	$table->set("tradesets", $res);
	$table->set("trades",$table->data("trades")+1);
	$table->update();
	if( $table->data("trades") == 4 ){
		$table->performTrades();
		$table->update();
		$table->set("state",2);
		$table->update();
	}
	echo "0";
}
function setState($t){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	$table->set("state", $_REQUEST['state'] );
	$table->update();
	echo "0";
}
function sendDragon($t, $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "et";return;}
	if( $table->data("turn") != $id ){echo "-2";return;}	
	if( !isset($_REQUEST['to']) ){echo "e2";return;}
	$table->applyPointsToPlayer($table->data("handBy")+$_REQUEST['to']);
	$table->set("dragon",0);
	$table->set("pointsTo",$table->getPlayerId($table->data("handBy")+$_REQUEST['to']));
	if( $table->isOut($id) !== false ){
			$newTurn = ($table->data("turn")-1)%4;
			if( $newTurn < 0 ) $newTurn += 4;
			while( $table->isOut($newTurn)!==false ){
				$newTurn = ($newTurn-1)%4;
				if( $newTurn < 0 ) $newTurn += 4;
			}
			$table->set("turn",$newTurn);
	}
	$table->set("state",$table->data("state")+1);
	$table->update();
	echo "0";
}
function setTichu($t, $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "et";return;}
	if( !isset($_REQUEST['val']) ){echo "e&WrongVal";return;}
	if( sizeof( $table->getPlayerCards($id) ) < 14 ){echo "-1&YOU HAVE THROWN CARDS";return;}
	$val = $_REQUEST['val'];
	$tichus = $table->data("tichus");
	if( $tichus[$id] != 0 ){echo "-3&u have called!";return;}
	$tichus[$id] = $val;
	
	$table->set( "tichus", $tichus );
	$table->update();
}
function setHand($t,$id){
	$table = new Table();
	if( ! $table->get($t) ){echo "et";return;}
	if( $table->data("turn") != $id ){echo "-2";return;}
	if( !isset($_REQUEST['combination']) || !isset($_REQUEST['cards']) ){echo "e2";return;}
	$askedCard = $table->data("askedCard");
	if( $askedCard > 0 ){
		$c = $_REQUEST['cards'];
		$arr = explode(',',$c);
		$deck = new Deck();
		foreach($arr as $cid ){
			$cc = explode(".",$cid);
			$ca = $deck->getCard($cc[0]);
			if( $ca->weight == $askedCard ){
				$askedCard = -1;
			}			
		}
	}
	// FIND DRAGON
		$c = $_REQUEST['cards'];
		$arr = explode(',',$c);
		$deck = new Deck();
		$dragon = 0 ;
		foreach($arr as $cid ){
			$cc = explode(".",$cid);
			$ca = $deck->getCard($cc[0]);
			if( $ca->weight == 15 ){
				$dragon = 1;
			}
		}			
	////
	///
	if( isset( $_REQUEST['askedCard'] ) ){
		if( $_REQUEST['askedCard'] != -1 ) {
			$askedCard = $_REQUEST['askedCard'];	
		}
	}
	$comb = $_REQUEST['combination'];
	$cards = $_REQUEST['cards'];
	
	$table->applyHand($cards ,$comb ,$id);
	$newTurn = ($table->data("turn")-1)%4;
	if( $comb == -1 ){
		$newTurn = ($newTurn-1)%4;
	}
	if( $newTurn < 0 ) $newTurn += 4;
	while( $table->isOut($newTurn)!==false ){
		$newTurn = ($newTurn-1)%4;
		if( $newTurn < 0 ) $newTurn += 4;
	}
	
	/// IS OVER ? ? ? ? ?
	$tempArr = $table->getPlayerCards($id);
	if( sizeof($tempArr) == 1 && $tempArr[0] == ""  || sizeof($tempArr) == 0 ){
		$table->set("outs",$table->data("outs").$id);	
		$outs = $table->data("outs");
		$tichus = $table->data("tichus");
		if( strlen($outs) == 2 ){
			if( (intval($outs[0])-intval($outs[1]))%2 == 0 ){
					//over 1-2 1nd TEAM									
					$teamPoints = $table->data("teamPoints");
					$teamPoints = explode("|",$teamPoints);
					$teamPoints[$outs[0]%2] += 200;
					$teamPoints[$outs[0]%2] += $tichus[$outs[0]]*200;
					for($i=0;$i<4;$i++){
						$teamPoints[$i%2] -= $tichus[$i]*100;	
					}
					$teamPoints = implode('|',$teamPoints);
					$table->set("teamPoints",$teamPoints);
					$table->update();
					$table->over();
					$table->update();
					exit("0");
			}
		}
		else if( strlen($outs) > 2 ){
				$outs[3] = 6- $outs[0] - $outs[1] - $outs[2];
				if( (intval($outs[0])-intval($outs[2]))%2 == 0 ){
						//over 1-3 1nd TEAM						
						$teamPoints = $table->data("teamPoints");
						$teamPoints = explode("|",$teamPoints);
						$points = $table->data("points");
						$points = explode("|" , $points);
						$teamPoints[$outs[1]%2] += $points[$outs[1]];
						$teamPoints[$outs[0]%2] += 100 - $points[$outs[1]];					

						$teamPoints[$outs[0]%2] += $tichus[$outs[0]]*100;
						$teamPoints[$outs[1]%2] -= $tichus[$outs[1]]*100;
						$teamPoints[$outs[2]%2] -= $tichus[$outs[2]]*100;
						$teamPoints[$outs[3]%2] -= $tichus[$outs[3]]*100;	
						
						$teamPoints = implode('|',$teamPoints);
						$table->set("teamPoints",$teamPoints);

						$table->update();
						$table->over();	
						$table->update();
						exit("0");							
				}
				else{
					$teamPoints = $table->data("teamPoints");
					$teamPoints = explode("|",$teamPoints);
					$points = $table->data("points");
					$points = explode("|" , $points);
					$teamPoints[$outs[0]%2] += $points[intval($outs[0])] + $points[intval($outs[3])];
					$teamPoints[$outs[1]%2] += 100 - $points[intval($outs[0])] - $points[intval($outs[3])];					
		
					$teamPoints[$outs[0]%2] += $tichus[$outs[0]]*100;
					$teamPoints[$outs[1]%2] -= $tichus[$outs[1]]*100;
					$teamPoints[$outs[2]%2] -= $tichus[$outs[2]]*100;
					$teamPoints[$outs[3]%2] -= $tichus[$outs[3]]*100;		
					
					$teamPoints = implode('|',$teamPoints);
					$table->set("teamPoints",$teamPoints);	

					$table->update();
					$table->over();
					$table->update();
					exit("0");					
				}
		}
	}
	
	
	/*if( $newTurn == $table->data("handBy") ){
		if( $dragon == 0 )$table->applyPoints();
	}*/
	$table->set("askedCard" , $askedCard );
	$table->set("turn" , $newTurn );
	$table->set("dragon",$dragon);	
	$table->set("state",$table->data("state")+1);
	$table->update();	
	echo "0";	
}
function fold($t , $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e";return;}
	if( $table->data("turn") != $id ){echo "-2";return;}
	$newTurn = $table->data("turn");
	if( $table->data("combination") != 0 ){
		$newTurn = ($newTurn-1)%4;
	}
	if( $newTurn < 0 ) $newTurn += 4;
	if( $newTurn == $table->data("handBy") ){
			$table->applyPoints();
	}
	while( $table->isOut($newTurn)!==false && $table->data("dragon") == 0 ){
		$newTurn = ($newTurn-1)%4;
		if( $newTurn < 0 ) $newTurn += 4;
		if( $newTurn == $table->data("handBy") ){
			$table->applyPoints();
		}
	}
	
	if( $newTurn == $table->data("handBy") ){
		if( $table->data("dragon") == 0 ){
			$table->applyPoints();
		}
	}
	
	
	$table->set("turn" , $newTurn );
	$table->set("state",$table->data("state")+1);
	$table->update();	
	echo "0";		
}
function performTrades($t){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	$table->performTrades();
	$table->update();
	echo "OK";
}
function destroy($t , $id){
	$table = new Table();
	if( ! $table->get($t) ){echo "e1";return;}
	$table->set("state" , -$id-1);
	$table->update();
}
///
function getCard($id){
	$deck = new Deck();
	$t = $deck->getCard($id);
	echo "CARD ID:".$id."<br>\nWEIGHT:".$t->weight."\n<br>COLOR:".$t->color;
}
?>