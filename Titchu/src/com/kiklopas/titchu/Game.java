package com.kiklopas.titchu;

import java.util.List;
import java.util.Vector;

public class Game {
	private static final String WEBSERVER = "http://www.schooliki.gr/";
	Internet con;

	Deck deck;
	Vector<NPC> npc;
	Hand currentHand;
	Vector<Pile> pile;
	
	int tableId = -1;
	int myId = 9;// 9 = ADMIN = HOST
	int turn = -1;
	private String state;
	
	Game(){
		init();
	}
	// Functions in ORDER!!!!!!!!
	private void init() {
		con = new Internet();
		deck = new Deck();
		npc = new Vector<NPC>();
		for(int i=0;i<4;i++){
			NPC temp = new NPC();
			npc.add(temp);
		}
		pile = new Vector<Pile>();
		for(int i=0;i<4;i++){
			Pile temp = new Pile();
			pile.add(temp);
		}
	}
	public void drawCards(){
		for(int i=0;i<4;i++){
			npc.get(i).setHand( deck.cards.subList( i*14, (i+1)*14 ) );
		}
	}
	// After getting TradeSets!!!!!
	public void trade(){
		for(int i=0;i<4;i++){
			List<Card> s = npc.get(i).getTradeSet();
			npc.get( (i+1)%4 ).takeCard( s.get(0) );
			npc.get( (i+2)%4 ).takeCard( s.get(1) );
			npc.get( (i+3)%4 ).takeCard( s.get(2) );
		}
	}
	
	public void getFirstPlayer(){
		for(int i=0;i<4;i++){
			for(int j=0;j<npc.get(i).cards.size();j++){
				if( npc.get(i).cards.get(j).weight == 1 ){
						turn = i;
						return;
				}
			}
		}	
	}
	
	@SuppressWarnings("unused")
	private boolean stateChanged() {
		// Get State
		con.setURL(WEBSERVER + "get.php");
		con.setParam("tableId", String.valueOf(tableId));
		con.setParam("function", "getState");
		con.request();
		if( state == con.getFirstLine() ){
			return false;
		}
		state = con.getFirstLine();
		con.clear();
		return true;
	}
	
	private void setCurrentHand(String string) {
		currentHand.cards.clear();
		String arr[] = string.split(",");
		for(int i=0;i<arr.length;i++){
			currentHand.cards.add(deck.getCard(Integer.parseInt(arr[i])));
		}
	}

	@SuppressWarnings("unused")
	private void getCurrent() {
		// Get State
		boolean flag = true;
		while( flag ){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "getCurrent");
			con.request();
			if( con.getFirstLine() == "0" ){
				flag = false;
			}
		}
		/* Get Num of Card Per person Sorted By id
		for(int i=1;i<=4;i++){
			numOfCards[i-1] = Integer.parseInt( con.lines.get(i) );
		}
		*/
		
		// Get Sundiasmo Fulla On the floor
		currentHand = new Hand();
		currentHand.combination = Integer.parseInt( con.lines.get(5) );
		setCurrentHand( con.lines.get(6) );
		
		// Get my cards
		// NOT DONE YET -- MIGHT NOT IN need!
		
		con.clear();
	}
	
	public void gameLoop(){
		// Wait for player turn to play!
		//npc.get(turn).play();
		// TO-DO -> Connect with Class Hand
		//       -> Internet connection
		//       -> Server Side Implementation
		
	}
	
	public void display(){
		
	}
	
}
