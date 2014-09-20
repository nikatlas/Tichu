<?php
session_start();
//error_reporting(E_ALL | E_STRICT | E_NOTICE );
//ini_set("display_errors", 1);
include_once( 'sql_lib.php' );

class Card{
	public function	__construct($w , $c , $id){
		$this->weight = $w;
		$this->color = $c;
		$this->id = $id;
	}
}
class Deck {
	public function	__construct(){
		$this->init();
	}
	private function init() {
		$r = fopen("deck.txt",'r');
		//(echo "READING!<br>";
		if( !$r ) {exit("Cannot Find Deck!!!");}
		$this->cards = array();
		for($i=0;!feof($r);$i++){
			$w = intval(fgets($r));
			$c = intval(fgets($r));
			$temp = new Card($w,$c, $i);
			//echo "WEIGHT".$temp->weight."color".$temp->color."ID".$temp->id."<br>";
			array_push($this->cards , $temp );
			//echo "WEIGHT".$this->cards[$i]->weight."color".$this->cards[$i]->color."ID".$this->cards[$i]->id."<br>";
		}
		fclose($r);
	}
	public function getCard($id){
		//$card = new Card(); 
		for($i=0;$i<sizeof($this->cards);$i++){
			if( $this->cards[$i]->id == $id )return new Card($this->cards[$i]->weight , $this->cards[$i]->color ,$this->cards[$i]->id);
		}
		return NULL;
	}
	public function shuffle() {
		/*for($i=0;$i<sizeof($this->cards);$i++){
			$c = rand(0,99999999)%sizeof($this->cards);
			$temp1 = array_slice($this->cards , 0 , $c);
			$temp2 = array_slice($this->cards , $c );
			$this->cards = array_merge($temp1,$temp2);
		}*/
		return shuffle($this->cards);
	}	
}

class Table extends Element{
	public function __construct() {
		parent::setTable("tables");
		$this->checkTable();
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {		
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {	
		$this->arr = array();
	}
	public function getPlayerCards($id){
		if( $id < 0 || $id > 3 ) return NULL;
		$r = explode('|',$this->data("playercards") );	
		$r = explode(',' , $r[$id]);
		return $r;
	}
	public function getAllPlayerCards($id){
		if( $id < 0 || $id > 3 ) return NULL;
		$r = explode('|',$this->data("allplayercards") );	
		$r = explode(',' , $r[$id]);
		return $r;
	}
	public function getTradeCards($id){
		if( $id < 0 || $id > 3 ) return NULL;
		$r = explode('|',$this->data("tradesets") );	
		$r = explode(',' , $r[$id]);
		return $r;
	}
	public function getTradeSet($id){
		if( $id < 0 || $id > 3 ) return NULL;
		$r = explode('|',$this->data("getsets") );	
		$r = explode(',' , $r[$id]);
		return $r;
	}
	public function findPlayer(){
		for($i=0;$i<4;$i++){
			$arr = $this->getPlayerCards($i);
			if( in_array('0',$arr) || in_array(0,$arr) ){
				$turn = $i;break;
			}
		}
		$this->set("turn",$turn);
		$this->update();
	}
	public function applyHand($cards,$comb, $id){
		if( $comb == 0 )return;
		if( $this->data("combination") != $comb && $this->data("combination") != 0 && $this->data("combination") != -1 ){exit("e3&errorLib");}			
		$this->set("combination" , $comb );
		$this->set("currentHand" , $cards);
		$deck = new Deck();
		$points = 0;
		$plcards = $this->getAllPlayerCards($id);
		$c = explode("," , $cards);
		foreach( $c as $Card){
			$temp = $deck->getCard($Card);
			if( $temp->weight == 5 || $temp->weight == 10 )$points+=$temp->weight;
			else if( $temp->weight == 13 )$points+=10;
			else if( $temp->weight == -1 )$points-=25;
			else if( $temp->weight == 15 )$points+=25; 
			$k = array_search( $temp->id , $plcards ); 
			if ($k == false)exit("eC");
			unset($plcards[$k]);
		}
		
		for($i=0;$i<4;$i++){
			if( $i == $id ){
				$plc[$i] = $plcards;	
			}
			else{
				$plc[$i] = $this->getPlayerCards($i);
			}
			$plc[$i] = implode("," , $plc[$i]);
		}
		$plc = implode("|" , $plc);
		
		$this->set("playercards" , $plc );
		$this->set("pointsOn" , $this->data("pointsOn") + $points );		
		$this->set("handBy", $id);
	}
	public function applyPoints(){
		$points = explode("|" , $this->data("points"));
		$pointsOn = $this->data("pointsOn");
		$points[$this->data("handBy")] += $pointsOn;
		$points = implode("|" , $points);
		 
		$this->set("combination" , $comb );
		$this->set("currentHand" , "");
		$this->set("points" , $points);
		$this->set("pointsOn", 0 );
	}
	public function drawCards(){
		$d = new Deck();
		$d->shuffle();
		$pcards = array();
		for($j=0;$j<4;$j++){ 
			$pcards[$j] = array();
			for($i=0;$i<14;$i++){
				array_push($pcards[$j] , $d->cards[$j*14 + $i]->id);
			}
		}
		for($j=0;$j<4;$j++)$pcards[$j] = implode(',',$pcards[$j]);
		$pcards = implode('|',$pcards);
		$this->set("playercards",$pcards);
		$this->set("allplayercards",$pcards);
		//echo "CARDS:<br>".$this->data("playercards")."<br>";
		return;
	}
	private function debug($arr){
		for($i=0;$i<sizeof($arr);$i++)echo $arr[$i]."\n";	
	}
	public function performTrades(){
		$s = array();
		for($i=0;$i<4;$i++){
			$r[$i] = $this->getPlayerCards($i);
			$t[$i] = $this->getTradeCards($i);
			array_push($s, array());
		}
		for($i=0;$i<4;$i++){
			for($j=0;$j<3;$j++){
				if(($key = array_search($t[$i][$j], $r[$i])) !== false) {
					unset($r[$i][$key]);
				}	
				else{
					exit("e\nWrong Cards!Delayed Detection!");
				}
			}
		}
		for($i=0;$i<4;$i++){
			for($j=1;$j<=3;$j++){
				array_push($r[($i+$j)%4] ,$t[$i][$j-1] );
				array_push($s[($i+$j)%4] ,$t[$i][$j-1] );
			}
		}
		for($i=0;$i<4;$i++){
			$res[$i] = implode(',',$r[$i]);
			$sets[$i] = implode(',',$s[$i]);
		}
		$res = implode('|',$res);
		$sets = implode('|',$sets);
		$this->set("playercards" , $res );
		$this->set("getsets" , $sets );
		return;
	}
	
	public function checkTable(){
		connect();
		$q = mysql_query("show tables like '$this->table'");
		if(!mysql_fetch_array($q)){$this->createTable();}
	}
	public function createTable(){
		$a = "
CREATE TABLE tables
(
id int NOT NULL AUTO_INCREMENT,
seats int,
players int,
playercards TEXT,
trades int,
tradesets TEXT,
setsgot int,
getsets TEXT,
turn int,
combination int,
currentHand TEXT,
handBy int,
points TEXT,
pointsOn int,
state TEXT,
date DATETIME,
PRIMARY KEY (id)
)
		";
		$q = mysql_query($a);	
		if( !$q ){
			exit("false");	
		}
		//exit("table Created");
	}
}

class Points extends Element{
	public function __construct() {
		parent::setTable("points");
		$this->checkTable();
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {		
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {	
		$this->arr = array();
	}
	
	public function checkTable(){
		$q = mysql_query("show tables like '$this->table'");
		if(!mysql_fetch_array($q)){$this->createTable();}
	}
	public function createTable(){
		$a = "
CREATE TABLE points
(
id int NOT NULL AUTO_INCREMENT,
tableid int,
points TEXT,
date DATETIME, 
PRIMARY KEY (id)
)
		";
		$q = mysql_query($a);	
		if( !$q ){
			exit("false");	
		}
		//exit("table Created");
	}
}
class Move extends Element{
	public function __construct() {
		parent::setTable("moves");
		$this->checkTable();
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {		
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {	
		$this->arr = array();
	}
	public function loadFromRequest(){
		connect();		
		$this->arr['name'] = $_REQUEST['name'];
		$this->arr['value'] = $_REQUEST['value'];
	}
	public function checkTable(){
		$q = mysql_query("show tables like '$this->table'");
		if(!mysql_fetch_array($q)){$this->createTable();}
	}
	public function createTable(){
		$a = "
CREATE TABLE moves
(
id int NOT NULL AUTO_INCREMENT,
tableid int,
currenthandid int,
turn int,
date DATETIME,
PRIMARY KEY (id)
)
		";
		$q = mysql_query($a);	
		if( !$q ){
			exit("false");	
		}
		//exit("table Created");
	}
}

class TradeSet extends Element{
	public function __construct() {
		parent::setTable("trades");
		$this->checkTable();
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {		
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {	
		$this->arr = array();
	}
	
	public function checkTable(){
		$q = mysql_query("show tables like '$this->table'");
		if(!mysql_fetch_array($q)){$this->createTable();}
	}
	public function createTable(){
		$a = "
CREATE TABLE trades
(
id int NOT NULL AUTO_INCREMENT,
tableid int,
players int,
cards TEXT,
date DATETIME,
PRIMARY KEY (id)
)
		";
		$q = mysql_query($a);	
		if( !$q ){
			exit("false");	
		}
		//exit("table Created");
	}
}

class Hand extends Element{
	public function __construct() {
		parent::setTable("hands");
		$this->checkTable();
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {		
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {	
		$this->arr = array();
	}
	public function loadFromRequest(){
		connect();		
		$this->arr['name'] = $_REQUEST['name'];
		$this->arr['value'] = $_REQUEST['value'];
	}
	public function checkTable(){
		$q = mysql_query("show tables like '$this->table'");
		if(!mysql_fetch_array($q)){$this->createTable();}
	}
	public function createTable(){
		$a = "
CREATE TABLE hands
(
id int NOT NULL AUTO_INCREMENT,
tableid int,
combination int,
cards TEXT,
date DATETIME,
PRIMARY KEY (id)
)
		";
		$q = mysql_query($a);	
		if( !$q ){
			exit("false");	
		}
		//exit("table Created");
	}
}
///////////////////////////
///////////////////////////
class Element{
	public function setTable($i){
		$this->table = $i;	
	}
	public function __construct() {
		$this->arr['id_island'] = $_SESSION['island_id'];
		$a = func_get_args(); 
		$i = func_num_args(); 
		if (method_exists($this,$f='__construct'.$i)) { 
		    call_user_func_array(array($this,$f),$a); 
		} 
	}
	public function __construct1( $id ) {	
		$this->arr['id_island'] = $_SESSION['island_id'];
		$this->arr = array();
        $a = $this->get( $id );
		if( !$a )exit('Error getting Paralia!');
	}
	public function __construct0() {		
		$this->arr['id_island'] = $_SESSION['island_id'];
		$this->arr = array();
	}
	public function isfilled(){
		return true;		
	}
	public function publish($i = 1){
		connect();
		mysql_query("SET CHARACTER SET 'utf8'");
		mysql_query("SET NAMES 'utf8'");
		$q = update($this->table, "published = " . $i ,"id = ".$this->arr['id'] );
		if( $q == -1 ){
			exit("Error publishing paralia!  -- > ERROR : " . mysql_error() );	
		}
	}
	public function unpublish(){
		return $this->publish(0);	
	}
	public function get( $id , $col="id" ){
		global $LANG;
		connect();
		mysql_query("SET CHARACTER SET 'utf8'");
		mysql_query("SET NAMES 'utf8'");
		
		$s = select( "*" , $this->table , "$col='$id'" );
		if( $s < 0 || $s == false ){
			exit('e');
			//echo  "Error getting" ;
			//echo $id."-".$col."//";
			//echo $this->table." error!";
			return false;
		}		
		$this->arr = $s;
		/*/ML PARSER
		$parser = new MLParser();		
		foreach( $this->arr as &$a ){
			if( !$parser->isML($a) )continue;
			$parser->breakCode($a);
			$a = $parser->getLanguage($LANG);
		}
		unset($a);
		//*//////////
		$this->s = array_keys($s);
		$i = 0;
		foreach( $this->s as $k){
			$i++;
			if( ($i % 2) == 1)continue;
			$this->keys[($i-1)/2] = $k;
		}
		unset($k);
		$f = "";
		foreach($this->keys as $k){
			$f .= "$"."this->".$k."=&$"."this->arr['".$k."'];";
		}

		unset($k);
		eval($f);
		//echo $this->id."<hr><BR>";
		return true;
   	}
	public function data($s){
		return $this->arr[$s];	
	}
	public function set($name , $val){
		$this->arr[$name] = $val;
	}
	public function realData($s){
		return $this->arr[$s];	
	}
	public function add(){
		//$this->parse();
		connect();
		q_gr();
		
		if( !$this->isfilled() ){
			exit("Info is missing(paralies ERROR)");
		}	
		
		//$q = mysql_query("SELECT * FROM ".$this->table." WHERE title_tags = '".$this->arr['title_tags']."' AND category = '".$this->arr['category']."' OR link='".$this->arr['link']."'");
		//$s = mysql_fetch_array($q);
		//if(  $s != false ){
		//	exit("Η παραλία/Αξιοθέατο υπάρχει ήδη !" );	
		//}
		
		$s = " date ";
		$v = " NOW() ";
		foreach($this->arr as $key => $val ){
			if( $key == "date" ) continue;
			$s = $s . ",`" .$key."`";
			$v = $v . ",N'" .$val."'"; 
		}
		unset($key,$val);
		$q = insert($this->table , $s, $v);
		if( $q == -1 ){
			exit("Couldnt add_user (ERROR USER)" . $this->arr[0] . " - " . mysql_error() );
		}
		
		$this->arr['id'] = mysql_insert_id();
		$this->get($this->data("id"));
		return true;
	}
	
	public function update(){
		connect();
		q_gr();
		mysql_query("SET CHARACTER SET 'utf8'");
		mysql_query("SET NAMES 'utf8'");
		
		$s = " date = NOW()";
		$l = sizeof($this->keys);
		for( $i = 0 ; $i < $l;$i++ ){
			if( $this->keys[$i] == "date" || $this->keys[$i] == "id" ) continue;
			$s = $s . ",`" . $this->keys[$i] ."` = '" . $this->arr[$this->keys[$i]]."'";
		}
		$q = update($this->table, $s ,"id = ".$this->arr['id'] );
		if( $q == -1 ){
			echo $s;
			exit("Error updating katalima!" . mysql_error() );	
		}
		return true;
	}

	public function delete(){
		if( $this->id == NULL || !is_numeric($this->id) ){
			return false;
		}
		connect();
		q_gr();
		
		$q = delete($this->table,"id = ".$this->arr['id']);
		if( $q == -1 ){
			exit("Error Deleting User(USER ERROR)");
		}
		
		return true;
	}
	public function createForm(){
		echo "
			<form id='elementForm' method='POST'>
			<input type=\"hidden\" name=\"up\" value=\"1\" />
			<input type=\"hidden\" name=\"id\" value=\"".$this->id."\" />
				<ul>";
		foreach($this->keys as $key => $val ){
			if( $val == "date" || $val == "id" || $val == "published" ) continue;
			if( strlen($val) > 80 ){
				echo "<li>
						<label for='".$key."'>".ucfirst($val)."</label>
						<textarea name='".$val."'  id='".$key."' >".$this->arr[$key]."</textarea>
					  </li>";				
			}
			else{
				echo "<li>
						<label for='".$key."'>".ucfirst($val)."</label>
						<input name='".$val."' id='".$key."' value='".$this->arr[$key]."' />
					  </li>";
			}
		}
		unset($key,$val);			
		echo "  	<li>
						<input type='submit' value='Ανανέωση' />
					</li>
				</ul>
			</form>";
	}
}
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
function cnt($table){
		connect();
		q_gr();
		$s = select("COUNT(*) as max",$table,"");	
		return $s['max'];		
}
function cntw($table , $where){
		connect();
		q_gr();
		$s = select("COUNT(*) as max",$table,$where);	
		return $s['max'];		
}
function str_lreplace($search, $replace, $subject)
{
    $pos = strrpos($subject, $search);

    if($pos !== false)
    {
        $subject = substr_replace($subject, $replace, $pos, strlen($search));
    }

    return $subject;
}
function seperate($i,$k){
	$i = explode(' ' , $i);
	$r = "";
	for($p = 0 ; $p < $k ; $p++){
		$r .= $i[$p]." ";	
	}
	return $r;
}
?>
