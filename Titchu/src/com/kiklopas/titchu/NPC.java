package com.kiklopas.titchu;

import java.util.List;
import java.util.Vector;

public class NPC {
	Vector<Card> cards;
	Vector<Card> tradeSet,oldTradeSet;
	Hand hand;
	
	NPC(){
		init();
	}
	
	private void init() {
		cards = new Vector<Card>();
		tradeSet = new Vector<Card>();
		oldTradeSet = new Vector<Card>();
		hand = new Hand();
	}
	
	public void setHand(){	
		
	}
	public void display(){
		
	}
	public void setHand(List<Card> subList) {
		cards = new Vector<Card>(subList);
	}
	public void getHand(Vector<CardActor> actors){
		hand.getHand(actors);
	}
	public void takeCard(Card card) {
		cards.add(card);
	}
	public List<Card> getTradeSet() {
		return (tradeSet.size() == 3) ? tradeSet : null;
	}
	public boolean hasBomb(){
		Hand.sortCards(cards);
		String s = "";
		for(int i=0;i<cards.size();i++){
			try{
				s += cards.get(i).weight;
			}
			catch(Exception e){
				
			}
		}
		for(int i=2;i<=14;i++){
			String p = String.valueOf(i)+ String.valueOf(i) + String.valueOf(i) + String.valueOf(i);
			if( s.contains(p) )	return true;
		}
		s = "";
		Hand.sortCardsByColor(cards);
		for(int i=0;i<cards.size();i++){
			try{
				s += String.valueOf(cards.get(i).weight) + String.valueOf(cards.get(i).color) ;
			}
			catch(Exception e){
				
			}
		}
		// TODO  get The BOMB hand and keep that. Get the best bomb. HAVE DECIDED TO KEEP AS IS AND THE PERSON CHOOSES THE BOMB!
		// 		 Exw to String kai arkei  mia anazitisi sta fila(this.cards) kai sugkrisi weight kai color!
		for(int c=0;c<4;c++){
			for(int i=2;i<=10;i++){
				String p = String.valueOf(i) + String.valueOf(c) + String.valueOf(i+1) + String.valueOf(c) + String.valueOf(i+2)+ String.valueOf(c) + String.valueOf(i+3) + String.valueOf(c) + String.valueOf(i+4) + String.valueOf(c);
				if( s.contains(p) )return true;
			}
		}
		return false;
	}
	public void setTradeSet(Vector<CardActor> trades) {
	if( trades.size() != 3 )return;		
		tradeSet.add(0, trades.get(0).dropped.card);
		tradeSet.add(1, trades.get(1).dropped.card);
		tradeSet.add(2, trades.get(2).dropped.card);
		System.out.println("TradeSet Has been set!");
		oldTradeSet.addAll(tradeSet);
		return;
	}

	public void removeTradeSetFromCards() {
		if( tradeSet.size() != 3 )return;
		for( Card a: tradeSet ){
			cards.remove(a);
		}
	}

	public void addTradeSetToCards() {
		if( tradeSet.size() != 3 )return;
		for( Card a: tradeSet ){
			cards.add(a);
		}		
	}

	public Hand getBestBomb() {
		Hand temp = new Hand();
		
		return null;
	}
	
	
}
