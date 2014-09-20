package com.kiklopas.titchu;

import java.util.Vector;

public class Pile {
	Vector<Card> cards;
	
	Pile(){
		init();
	}

	private void init() {
		cards = new Vector<Card>();
	}
	
	public int getPoints(){
		return 0;
	}
	
	public void addCards(Vector<Card> temp){
		cards.addAll(temp);
	}
		
}
