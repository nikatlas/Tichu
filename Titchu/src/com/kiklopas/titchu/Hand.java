package com.kiklopas.titchu;

import java.util.Vector;

public class Hand {
	public static final int    DOGS = -1,
	 						   NOTHING  = 0,
							   SINGLE   = 1,
							   PAIR     = 2,
							   STEPS    = 3,
							   STRAIGHT	= 4,
							   BOMB     = 5,
							   FULL     = 6,
							   THREE    = 7;
	
	public Vector<Card> cards;
	private boolean bomb = false;
	public int combination = 0;
	private int phoenixVal = -1;
	
	public int by;
	public Hand(){
		cards = new Vector<Card>();
	}
	public Hand( Vector<Card> hand){
		cards = new Vector<Card>();
		cards = hand;
		bomb = isBomb();
	}
	
	public void setHand( Vector<Card> hand){
		cards = new Vector<Card>();
		cards = hand;
		bomb = isBomb();
	}
	public void setPhoenix( ){
		if( this.cards.size() == 0 )return;
		if( this.cards.get(0).id == Card.PHOENIX )
			this.cards.get(0).weight = phoenixVal;
		sortCards(this.cards);
	}
	public void resetPhoenix( ){
		for(int z=0;z<cards.size();z++)if( cards.get(z).id == Card.PHOENIX )cards.get(z).weight = -1;
		sortCards(this.cards);
	}
	public boolean isBetter( Hand hand ){
		this.setCombination(this.getCombination());
		if( this.getCombination() == Hand.NOTHING ){
			return false;
		}
		if( hand == null )return true;
		else if( hand.combination == Hand.NOTHING || hand.combination == Hand.DOGS )return true;
		if ( (this.combination != hand.combination || this.combination == NOTHING) && this.combination != Hand.BOMB )return false;
		Hand.sortCards(this.cards);
		switch( this.combination ){		
			case Hand.SINGLE:
				if( this.cards.get(0).id == Card.PHOENIX ){
					phoenixVal = hand.cards.get(0).weight;
				}
				return this.cards.get(0).weight > hand.cards.get(0).weight || (this.cards.get(0).id == Card.PHOENIX && hand.cards.get(0).weight < 15) ;
			case Hand.PAIR:
				if( this.cards.get(1).weight <= hand.cards.get(1).weight )return false;
				if( this.cards.get(0).id == Card.PHOENIX )phoenixVal = this.cards.get(1).weight;
				return this.cards.get(1).weight > hand.cards.get(1).weight;
			case Hand.STRAIGHT:
				// TODO CHECK STRAIGHT COMPARISON 
				if( this.cards.get(0).id == Card.PHOENIX ) this.cards.get(0).weight = phoenixVal;
				Hand.sortCards(cards);
				boolean res =  (this.cards.size() == hand.cards.size() && this.cards.get(0).weight > hand.cards.get(0).weight );
				for(int z=0;z<cards.size();z++)if( cards.get(z).id == Card.PHOENIX )cards.get(z).weight = -1;
				Hand.sortCards(cards);
				return res;
			case Hand.STEPS:
				if( this.cards.get(0).id == Card.PHOENIX) this.cards.get(0).weight = phoenixVal;
				Hand.sortCards(cards);
				boolean res1 =(this.cards.size() == hand.cards.size() && this.cards.get(0).weight > hand.cards.get(0).weight); 
				for(int z=0;z<cards.size();z++)if( cards.get(z).id == Card.PHOENIX )cards.get(z).weight = -1;
				Hand.sortCards(cards);
				return res1;
			case Hand.THREE:
				if( this.cards.get(0).id == Card.PHOENIX )phoenixVal = this.cards.get(1).weight;
				return this.cards.get(1).weight > hand.cards.get(1).weight;
			case Hand.FULL:
				return this.cards.get(2).weight > hand.cards.get(2).weight;
			case Hand.BOMB:
				return isBombBetter(this,hand);

		}
		return true;
	}
	
	public boolean isBomb(){
		if( cards.size() < 4 )return false;
		if( cards.size() == 4 ){
			for(int i=1;i<4;i++)
				if( cards.get(i).weight != cards.get(i-1).weight )return false;				
			return true;
		}
		cards = sortCards(cards);
		// TO-DO add color comparison! 
		for(int i=1;i<cards.size();i++){
			if( (cards.get(i).weight != cards.get(i-1).weight + 1 )|| (cards.get(i).color != cards.get(i-1).color) )
				return false;
		}
		return true;
	}
	public void setCombination(int type){
		this.combination = type;
		return;
	}
	public int getCombination(){
		cards = sortCards(cards);
		if( isBomb() )return Hand.BOMB;
		if( cards.size() == 1 ){
			if( cards.get(0).id ==  Card.DOGS )return Hand.DOGS;
			return Hand.SINGLE;
		}
		else if( cards.size() == 2 ){
			if( isPair() )return Hand.PAIR;
			return Hand.NOTHING;
		}
		else if( cards.size() == 3 ){
			if( isThree() )return Hand.THREE;
			return Hand.NOTHING;
		}
		else if( cards.size() == 4 ){
			if( isSteps() )return Hand.STEPS;
		}
		else if( cards.size() == 5 ){
			if( isFoul() )return Hand.FULL;
			if( isStraight() )return Hand.STRAIGHT;
		}
		else if( cards.size() > 5 ){
			if( isStraight() )return Hand.STRAIGHT;
			else if( cards.size() % 2 == 0 ){
				if( isSteps() ) return Hand.STEPS;
			}
		}
		return Hand.NOTHING;
	}
	private boolean isStraight() {
		boolean flag = false;
		if( cards.get(0).id == Card.PHOENIX  )flag = true;
		for(int i=1;i<cards.size()-1;i++){
			if( cards.get(i).weight != cards.get(i+1).weight-1 ){
				if(flag){
					if( cards.get(i).weight == cards.get(i+1).weight-2 ){
						flag = false;
						phoenixVal = cards.get(i+1).weight-1;
						continue;
					}
					else{
						phoenixVal = -1;
						return false;
					}
				}
				else{
					return false;
				}
			}
		}
		if( flag ){
			phoenixVal = cards.get(cards.size()-1).weight + 1;
			if( phoenixVal > 14 ){
				phoenixVal = cards.get(1).weight-1;
			}
		}
		return true;
	}
	// TODO FIX PHOENIX WEIGHT
	private boolean isFoul() {
		int mx = -1;
		if( cards.get(0).id == Card.PHOENIX ){
			if( cards.get(1).weight == cards.get(2).weight )
				cards.get(0).weight = mx = cards.get(3).weight;
			else cards.get(0).weight = mx = cards.get(1).weight;
		}
		sortCards(cards);
		boolean case1 = cards.get(0).weight == cards.get(1).weight && cards.get(1).weight != cards.get(2).weight && cards.get(2).weight == cards.get(3).weight && cards.get(3).weight == cards.get(4).weight;
		boolean case2 = cards.get(0).weight == cards.get(1).weight && cards.get(1).weight == cards.get(2).weight && cards.get(2).weight != cards.get(3).weight && cards.get(3).weight == cards.get(4).weight;
		// reset PHOENIX
		if( case1 || case2 )phoenixVal = mx; 
		for(int z=0;z<cards.size();z++)if( cards.get(z).id == Card.PHOENIX )cards.get(z).weight = -1;
		sortCards(cards);
		return case1 || case2;
	}
	/// STEPS PLACES PHOENIX.weight TO THE RIGHT ONE
	private boolean isSteps() {
		// IF NO PHOENIX NO PROBLEM 
		if ( cards.get(0).id != Card.PHOENIX ){
			if ( !isPair(cards.get(0),cards.get(1)) )return false;
			//if( isPair(cards.get(1),cards.get(2) ) ) return false;
			for(int i=2;i<cards.size();i+=2){
				if( cards.get(i).weight - cards.get(i-1).weight != 1 )return false;
				if( !isPair(cards.get(i) , cards.get(i+1) ) )return false;
				if( isPair(cards.get(i-1),cards.get(i) ) )return false;
			}	
		}// WHAT ABOUT WITH PHOENIX?
		else{
			Vector<Integer> vals = new Vector<Integer>();
			for( int i = 1; i < cards.size(); i++){
				if( vals.contains(cards.get(i).weight) )vals.remove(vals.indexOf(cards.get(i).weight));
				else{
					vals.add(cards.get(i).weight);
				}
			}
			if( vals.size() != 1 )System.out.print("FAILED TO FIND PHOENIX VALUE ON STEPS\n DEBUG HAND.java LINE 175 or near!");
			cards.get(0).weight = vals.get(0);
			sortCards(cards);
			// TYPICAL CHECKING
			if( !isPair(cards.get(0) , cards.get(1) ) ){
				// REMEMBER TO GET PHOENIX BACK IF FALSE
				this.resetPhoenix();
				return false;
			}
			for(int i=2;i<cards.size();i+=2){
				if( cards.get(i).weight - cards.get(i-1).weight != 1 ){this.resetPhoenix();return false;}
				if( !isPair(cards.get(i) , cards.get(i+1) ) ){
					// REMEMBER TO GET PHOENIX BACK IF FALSE
					this.resetPhoenix();
					return false;
				}
				if( isPair(cards.get(i-1),cards.get(i))){
					this.resetPhoenix();
					return false;
				}
			}
			phoenixVal = vals.get(0);
			for(int z=0;z<cards.size();z++)if( cards.get(z).id == Card.PHOENIX )cards.get(z).weight = -1;
			sortCards(cards);
		}
		return true;
	}
	private boolean isPair(Card a , Card b) {// TODO THERE MAY BE SOME BUGS WITH isFULL isSTEPS That set Phoenix.weight  and do a real weight check 
		return a.weight == b.weight || a.id == Card.PHOENIX;
	}
	private boolean isPair() {
		return cards.get(0).weight == cards.get(1).weight || cards.get(0).id == Card.PHOENIX;
	}
	private boolean isThree() {
		return isPair(cards.get(0) ,cards.get(1) ) && isPair(cards.get(1),cards.get(2));
	}
	static public Vector<Card> sortCards( Vector<Card> vec){
		try{
		int l = vec.size();
		for(int i = 0;i<l;i++){
			for(int j = vec.size()-1;j>i;j--)
				if( vec.get(j-1).weight > vec.get(j).weight  ){
					Card temp = vec.get(j);
					vec.set(j, vec.get(j-1));
					vec.set(j-1, temp);
				}
		}
		}catch(Exception e){}
		return vec;
	}
	static public Vector<Card> sortCardsByColor( Vector<Card> vec){
		try{
		int l = vec.size();
		for(int i = 0;i<l;i++){
			for(int j = vec.size()-1;j>i;j--)
				if( vec.get(j-1).color > vec.get(j).color ){
					Card temp = vec.get(j);
					vec.set(j, vec.get(j-1));
					vec.set(j-1, temp);
				}
				else if( vec.get(j-1).color == vec.get(j).color && vec.get(j-1).weight > vec.get(j).weight ){
					Card temp = vec.get(j);
					vec.set(j, vec.get(j-1));
					vec.set(j-1, temp);
				}
		}
		}
		catch(Exception e){
			
		}
		return vec;
	}
	public void getHand(Vector<CardActor> vec){
		cards.clear();
		int l=vec.size();
		for(int i=0;i<l;i++){
			if( vec.get(i).selected ) cards.add(vec.get(i).card);
		}
		bomb = isBomb();
		return;
	}
	public String getStringCards() {
		String r="";
		if(this.cards.size()==0)return r;
		
		if( this.cards.get(0).id == Card.PHOENIX ){
			r = String.valueOf( cards.get(0).id )+"."+this.cards.get(0).weight;
		}
		else{
			r = String.valueOf(cards.get(0).id);
		}
		for(int i=1;i<this.cards.size();i++){
			if( this.cards.get(i).id == Card.PHOENIX ){
				r += ","+this.cards.get(i).id+"."+this.cards.get(i).weight;
			}
			else{
				r += ","+this.cards.get(i).id;
			}
		}
		return r;
	}
	public static boolean isBombBetter(Hand hand, Hand currentHand) {
			if(  hand.getCombination() != Hand.BOMB ) return false;
			if( currentHand.getCombination() != Hand.BOMB )return true;  
			if( hand.cards.size() > currentHand.cards.size() )return true;
			if( hand.cards.size() < currentHand.cards.size() )return false;
			
			Hand.sortCards(hand.cards);
			Hand.sortCards(currentHand.cards);

			if( hand.cards.get(0).weight > currentHand.cards.get(0).weight )return true;
			return false;
	}
	public static Vector<Integer> countCards(Vector<Card> cards){
		Vector<Integer> res = new Vector<Integer> ();
		res.add(0);
		res.add(0);
		for( int i=2;i<=14;i++)
			res.add(0);
		for( Card a: cards ){
			if( a.id == Card.PHOENIX )res.set(0,1);
			if( a.weight > 0 && a.weight < 15)
				res.set(a.weight , res.get(a.weight)+1);
		}
		return res;
	}
	public static Hand findBestCombinationWithCard(Vector<Card> cards , int val,int comb , int size){
		Hand temp = new Hand();
		Vector<Card> tempCards = new Vector<Card>();
		Vector<Integer> count = Hand.countCards(cards);
		int t = 0;int t2=0;
		switch(comb){
			case Hand.BOMB:
				//TODO
				break;
			case Hand.NOTHING:case Hand.DOGS:
			case Hand.SINGLE:
				if( count.get(val) > 0 ){
					for( Card a : cards ){
						if( a.weight == val ){
							tempCards.add(a);
							break;
						}
					}
				}
				break;
			case Hand.PAIR:
				if( count.get(val) >= 2 || ( count.get(val) >= 1 && count.get(0) == 1 )  ){
					t = 0;
					for( Card a : cards ){
						if( a.weight == val ){
							tempCards.add(a);
							t++;
							if( t == 2 )break;
							else if( t == 1 && count.get(0) == 1 ){
								for( Card b : cards )if ( b.id == Card.PHOENIX ){tempCards.add(b);break;}
								break;
							}
						}
					}	
				}
				break;
			case Hand.THREE:
				if( count.get(val) >= 3 || ( count.get(val) >= 2 && count.get(0) == 1 )  ){
					t = 0;
					for( Card a : cards ){
						if( a.weight == val ){
							tempCards.add(a);
							t++;
							if( t == 3 )break;
							else if( t == 2 && count.get(0) == 1 ){
								for( Card b : cards )if ( b.id == Card.PHOENIX ){tempCards.add(b);break;}
								break;
							}
						}
					}						
				}
				break;
			case Hand.STRAIGHT:
				t = 0;t2 = 0;
				for( int i=val;i <= 14;i++){
					if( count.get(i)==0 )break;
					t++;
				}
				for( int i=val-1;i>=2;i--){
					if( count.get(i)==0 )break;
					t2++;
				}
				// Have native straight bigger or equal
				if( t + t2 >= size && count.get(0) == 0 ){
					int p = 0;
					for( int i=val;p<size && p < t && i <= 14;i++){
						tempCards.add(Hand.getFromWeight(cards, i));
						p++;
					}
					for( int i=val-1;i>=2 && p < size && p < t+t2;i-- ){
						tempCards.add(Hand.getFromWeight(cards, i));
						p++;
					}
				}
				else if ( count.get(0) == 1 ){
					int tt = t+1;
					if( val + tt > 14 )tt--;
					for( int i=val+tt;i <= 14;i++){
						if( count.get(i)==0 )break;
						tt++;
					}
					int tt2 = t2+1;
					if( val - 1 - tt2 < 2 )tt2--;
					for( int i=val-1-tt2;i>=2;i--){
						if( count.get(i)==0 )break;
						tt2++;
					}
					if ( tt + t2 >= size ){
						int p = 0;
						for( int i=val;p<size && p < tt && i <= 14;i++){
							if( count.get(i)== 0  )tempCards.add(Hand.getFromId(cards, Card.PHOENIX));
							else tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
						for( int i=val-1; p < size && p < tt+t2 && i >= 2;i-- ){
							tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
					}
					else if( tt2 + t >= size ){
						int p = 0;
						for( int i=val;p<size && p < t && i <= 14;i++){
							tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
						for( int i=val-1; p < size && p < t+tt2 && i >=2;i-- ){
							if( count.get(i)== 0  )tempCards.add(Hand.getFromId(cards, Card.PHOENIX));
							else tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
					}
				}
				break;
			case Hand.FULL:
				if( count.get(val) > 1 ){
					if( count.get(0) == 1 ){
						int z = 14;
						for( z=14; z >= 2 ; z--){
							if( z==val )continue;
							if( count.get(z) >= 2 )break;
						}
						if( z > 1 ){
							tempCards.add(Hand.getFromId(cards, Card.PHOENIX));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, val));
							tempCards.add(Hand.getFromWeight(cards, val));
						}
					}
					else{
						int z = 14;
						for( z=14; z >= 2 ; z--){
							if( z==val )continue;
							if( count.get(z) >= 3 )break;
						}
						if( z > 1 ){
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, val));
							tempCards.add(Hand.getFromWeight(cards, val));
						}
						if( tempCards.size() == 0 && count.get(val) >= 3 ){
							z = 14;
							for( z=14; z >= 2 ; z--){
								if( z==val )continue;
								if( count.get(z) >= 2 )break;
							}
							if( z > 1 ){
								tempCards.add(Hand.getFromWeight(cards, z));
								tempCards.add(Hand.getFromWeight(cards, z));
								tempCards.add(Hand.getFromWeight(cards, val));
								tempCards.add(Hand.getFromWeight(cards, val));
								tempCards.add(Hand.getFromWeight(cards, val));
							}
						}
					}
				}
				if( count.get(val) == 1 && count.get(0) == 1){
						int z = 14;
						for( z=14; z >= 2 ; z--){
							if( z==val )continue;
							if( count.get(z) >= 3 )break;
						}
						if( z > 1 ){
							tempCards.add(Hand.getFromId(cards, Card.PHOENIX));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, z));
							tempCards.add(Hand.getFromWeight(cards, val));
						}	
				}
				break;
			case Hand.STEPS://TODO
				size = size/2;
				t = 0;t2 = 0;
				for( int i=val;i <= 14;i++){
					if( count.get(i)<2 )break;
					t++;
				}
				for( int i=val-1;i>=2;i--){
					if( count.get(i)<2 )break;
					t2++;
				}
				// Have native straight bigger or equal
				if( t + t2 >= size && count.get(0) == 0 ){
					int p = 0;
					for( int i=val;p<size && p < t && i <= 14;i++){
						tempCards.add(Hand.getFromWeight(cards, i));
						tempCards.add(Hand.getFromWeight(cards, i));
						p++;
					}
					for( int i=val-1;i>=2 && p < size && p < t+t2;i-- ){
						tempCards.add(Hand.getFromWeight(cards, i));
						tempCards.add(Hand.getFromWeight(cards, i));
						p++;
					}
				}
				else if ( count.get(0) == 1 ){
					int tt = t+1;
					if( val + tt > 14 )tt--;
					for( int i=val+tt;i <= 14;i++){
						if( count.get(i)<2 )break;
						tt++;
					}
					int tt2 = t2+1;
					if( val - 1 - tt2 < 2 )tt2--;
					for( int i=val-1-tt2;i>=2;i--){
						if( count.get(i)<2 )break;
						tt2++;
					}
					if ( tt + t2 >= size ){
						int p = 0;
						for( int i=val;p<size && p < tt && i <= 14;i++){
							if( count.get(i)<2  ){tempCards.add(Hand.getFromId(cards, Card.PHOENIX));tempCards.add(Hand.getFromWeight(cards, i));}
							else{ tempCards.add(Hand.getFromWeight(cards, i));tempCards.add(Hand.getFromWeight(cards, i));}
							p++;
						}
						for( int i=val-1; p < size && p < tt+t2 && i >= 2;i-- ){
							tempCards.add(Hand.getFromWeight(cards, i));
							tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
					}
					else if( tt2 + t >= size ){
						int p = 0;
						for( int i=val;p<size && p < t && i <= 14;i++){
							tempCards.add(Hand.getFromWeight(cards, i));
							tempCards.add(Hand.getFromWeight(cards, i));
							p++;
						}
						for( int i=val-1; p < size && p < t+tt2 && i >=2;i-- ){
							if( count.get(i)== 0  ){tempCards.add(Hand.getFromId(cards, Card.PHOENIX));tempCards.add(Hand.getFromWeight(cards, i));}
							else{ tempCards.add(Hand.getFromWeight(cards, i));tempCards.add(Hand.getFromWeight(cards, i));}
							p++;
						}
					}
				}
				break;
		}
		// DEBUG
		Hand.sortCards(tempCards);
		System.out.print("\n");
		for( Card a: tempCards ){
			System.out.print(a.weight + " , ");
		}
		temp.cards = tempCards;
		return temp;		 
	}
	public static Card getFromWeight(Vector<Card> cards , int val){
		for( Card a : cards){
			if( a.weight == val )return a;
		}
		return null;
	}
	public static Card getFromId(Vector<Card> cards , int id){
		for( Card a : cards){
			if( a.id == id)return a;
		}
		return null;
	}
	public boolean canPlayAskedCard(Vector<Card> cards, Hand now , int asked) {
		if( now.combination == Hand.NOTHING )return true;
		Hand best = Hand.findBestCombinationWithCard(cards, asked, now.getCombination() , now.cards.size() );
		if( best.isBetter(now) )return true;
		return false;
	}
}