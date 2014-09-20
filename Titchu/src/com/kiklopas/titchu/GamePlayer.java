package com.kiklopas.titchu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;



public class GamePlayer implements Runnable {
	public static final String WEBSERVER = "http://tichu.schooliki.gr/";
	public static final float FAST_INTERVAL = 0.1f;
	public static final float SLOW_INTERVAL = 0.2f;
	private float INTERVAL = 0.5f;
	Deck deck;
	NPC npc;
	Hand currentHand;		
	String state = "";
	int cstate = -1;
	int numOfCards[] = { 0 , 0 , 0,  0};
	
	int tableId = -1; // Must Be -1
	int myId = -1;
	int turn = -1;
	
	// TODO !!! -- > -- > -- > Shuffle not Working on SERVER DONE 
	// 			-- > -- > -- > Read Timeout Fix DONE
	// 			-- > -- > -- > State 2 -> Check 
	// HAVE DONE Phoenix / Majongh implementation 
	// 			 Network Stability ( 2 Internet for async button calls )
	// 			 Tradesets are Right
	//			
	// TODO 	->>> ->>> 	DONE --- Dragon stay on Table to give to another
	// 			->>> -> > 	PROPABLY DONE --- Points are going wrong( Propably a "|||" initiation on SQL would fix that)
	// 			->>> ->>>	DONE ---- Implement a Class MessageBox to show stuff
	//			->>> ->>>   DONE ---- REALLY NEED TO CHANGE FONTS
	// 			->>> ->>> 	DONE ---- NEED TO SWITCH number to nicknames ! Add instructions under the CurrentHand ( who played it etc. ) 
	// 			->>> ->>> 	//Need some fixes with Bombs. Check whats better. Dont take turn etc.
	//			->>> ->>> 	See whats going on OnRoundFinish -> fix panel add a messageBox / Smooth restart!
	//			->>> ->>> 	UI RECONSTRUCTION! - > Publish!
	Internet con;
	Internet Lcon;
	public boolean tradeSetSend = false;
	public boolean switchFlag = false;
	private boolean stateFlag = false;
	private boolean playFlag;
	private Runnable stateChangeRunnable;
	private boolean active = true;
	private boolean pause = false;
	private int sendState = 0;
	public boolean showHand = false;
	public int askedCard = -1;
	public int dragon;
	public int bomb = -1;
	public Vector<Card> cards;
	public int teamPoints = 0;
	public int teamPoints2 = 0;
	private float time = 0;
	public int tichus[];
	public int players;
	public boolean[] activePlayers;
	protected Tichu parent;
	String names[] = {"player","player","player","player"};
	String originalID = "-1";
	private Preferences prefs;
	public int pointsTo;
	int playing = 1;
	
	GamePlayer(Tichu app, Deck d){
		parent = app;
		init(d); 
	}
	// Functions in ORDER!!!!!!!!
	private void init(Deck d) {
		activePlayers = new boolean[4];
		tichus = new int[4];
		cards = new Vector<Card>();
		deck = d;
		npc = new NPC();
		con = new Internet();
		Lcon = new Internet();
		prefs =  Gdx.app.getPreferences("tichuInfo");
		originalID = prefs.getString("id");
	}
	
	private void findRandomTable() {
		/*Ask server for Cards!*/
		if( con.status == "null" ){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("function", "getRandomTable");	
			con.request();
		}
		else if( con.status == "success" ){
			//System.out.print(con.getFirstLine());
			if( con.getFirstLine() != "" ){
				if( con.getFirstLine().charAt(0) != 'e' ){
					tableId = Integer.parseInt(con.getFirstLine());			
					System.out.println("TableId : " + tableId);
					sendState = 1;
				}
			}
			else{
				System.out.println("GETRandomTable returned ''! ?? ");
			}
			con.clear();
		}		
	}
	
	private void getId(){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("function", "getMyId");
			con.setParam("originalID", originalID);
			con.onFinish = new Runnable(){
				@Override
				public void run() {
			if( con.getFirstLine() != "" ){
				if( con.getFirstLine().charAt(0) != 'e' ){
					try{
						myId = Integer.parseInt(con.getFirstLine());			
					}
					catch(Exception e){}
					System.out.println("MyId : " + myId);
					//if( myId < 0 && myId >= 4 ){
						//con.status = "error";
					//}
					//else{
					// TODO
						stateFlag = false;
						sendState = 2;
					//}
				}
				else{
					System.out.println("GetID returned 'e'! ?? ");
					return;
				}
			}
			else{
				System.out.println("GetID returned ''! ?? ");
				delay();
			}
			con.clear();
			//con.disable();
		}
		};
		con.request();
	}
	private void drawCards(){
		//*Ask server for Cards!
		//if( con.status == "null" ){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "drawCards");
			//con.request();
		
		//}else if( con.status == "success" ){
			con.onFinish = new Runnable(){

			
			@Override
			public void run() {
				if( con.getFirstLine() != "" ){
					if( Integer.parseInt(con.getFirstLine()) == 0 ){
						Card temp;
						for(int i=1;i<=14;i++){
							try{
								temp = deck.getCard(Integer.parseInt(con.lines.get(i)));
							}
							catch(Exception e){
								con.clear();
								//con.disable();
								drawCards();
								return;
							}
							npc.cards.add(temp);				
						}						
						//System.out.println(	"CARDS:" );
						//for(int i=0;i<npc.cards.size();i++)
							//System.out.print(	npc.cards.get(i).id + " " );				
						stateFlag = false;
						sendState = 3;
					}			
				}
				else{
					System.out.println("DrawCards returned ''! ?? ");
					delay();
				}
			con.clear();	
			}
			
		//}
		};
		con.request();// added this // TODO CHECK THE ALTERNATIVE WAY IMPLEMENTED ONLY HERE
	}
	
	private void sendTradeSet(){
		if( npc.getTradeSet().size() < 3 )return;

		npc.removeTradeSetFromCards();
		con.setURL(WEBSERVER + "set.php");
		con.setParam("tableId", String.valueOf(tableId));
		con.setParam("myId", String.valueOf(myId));
		con.setParam("function", "setTradeSet");
		final String cards = String.valueOf(npc.getTradeSet().get(0).id) + "," +String.valueOf(npc.getTradeSet().get(1).id + "," + String.valueOf(npc.getTradeSet().get(2).id));
		con.setParam("cards", cards);
		
		con.onFinish = new Runnable(){
			@Override
			public void run() {
				int t = -1;
				try{
					t = Integer.parseInt(con.getFirstLine());
				}
				catch(NumberFormatException e){	
				}
				if( t == 0){
					System.out.println("Successfully Send tradeSet! YEAH!");
					npc.getTradeSet().clear();
					tradeSetSend  = true;
					sendState = 4;
				}
				else{
					System.out.println("ERROR! Send Trade Set! -> Error:"+con.getFirstLine());
					System.out.println(cards);
					System.out.println("RESENDING\n\n");			
				}			
				con.clear();
				stateFlag = false;
		}
		};
		con.request();
	}
	public boolean sendDragon(final int offset){
		if( Math.abs(offset-2) != 1 )return false;//offset = 1 or offset = 3
		//npc.removeTradeSetFromCards();
		Lcon.clear();
		Lcon.sync = true;
		Lcon.setURL(WEBSERVER + "set.php");
		Lcon.setParam("tableId", String.valueOf(tableId));
		Lcon.setParam("myId", String.valueOf(myId));
		Lcon.setParam("function", "sendDragon");
		Lcon.setParam("to", String.valueOf(offset));
		Lcon.onFinish = new Runnable(){
			@Override
			public void run() {
				int t = -1;
				try{
					t = Integer.parseInt(Lcon.getFirstLine());
				}
				catch(NumberFormatException e){	
					System.out.println("ERROR 1 1 1 ! Send DRAGON! -> Error:"+Lcon.getFirstLine());
				}
				if( t == 0){
					System.out.println("SEND DRAGON TO... !");
					resume();
				}
				else{
					System.out.println("ERROR! Send DRAGON! -> Error:"+Lcon.getFirstLine());
					System.out.println("RESENDING\n\n");		
					Lcon.clear();
					sendDragon(offset);
					return;
				}			
				Lcon.clear();
				stateFlag = true;
				//Lcon.disable();
			}
		}; 
		boolean flag = false;
		do {
			flag = Lcon.request();
			delay();
		}while(!flag);
		return true;
	}
	private void getTradeSet() {
		if( con.status == "null" ){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "getTradeSet");
			con.request();
		}
		else if( con.status == "success" ){
				int t = -1;
				try{
					t = Integer.parseInt(con.getFirstLine());
				}
				catch(NumberFormatException e){	
				}
				if( t == 0 ){
					switchFlag = true;
					Card temp;
					for(int i=1;i<=3;i++){
						temp = deck.getCard(Integer.parseInt(con.lines.get(i)));
						npc.tradeSet.add(temp);
					}
					sendState = 5;
				}
				else{
					delay();
				}
				con.clear();
				stateFlag = false;	
		}
	}
	/* Unused
	public void trade(){
		//Send TradeSet to server 
		sendTradeSet();
		// Wait to get!
		//Get New cards!
		getTradeSet();
	}
	*/
	
	public void getPlayer(){
		/*/ Get Player Turn
		boolean flag = true;
		while( flag ){
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "playerTurn");
			boolean a = false;
			do{
				a = con.request();
			}while(!a);
			if( con.getFirstLine() == "0" ){
				flag = false;
				turn = Integer.parseInt(con.lines.get(1));
			}
			else{
				System.out.println("GETTING PLAYER ERROR!");
			}
			con.clear();	
		}*/
		con.setURL(WEBSERVER + "get.php");
		con.setParam("tableId", String.valueOf(tableId));
		con.setParam("myId", String.valueOf(myId));
		con.setParam("function", "playerTurn");
		con.onFinish = new Runnable(){
			public void run(){
				if( con.getFirstLine() == "0" ){
					turn = Integer.parseInt(con.lines.get(1));
				}
				else{
					System.out.println("GETTING PLAYER ERROR!");
				}
				con.clear();
				stateFlag = false;
				//con.disable();
			}
		};
		con.request();

	}
	
	
	private void stateChanged() {
		// Get State
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "getState");
			con.setParam("sendState", String.valueOf(sendState));

			con.onFinish = new Runnable(){
				@Override
				public void run() {
			//con.printLines();
			if( con.getFirstLine() == "CC" ){
				con.printLines();
				return;
			}
			state = con.getFirstLine();
			if( state != "" && state != null ){
				int intState = cstate;
				try{
					intState = Integer.parseInt(state);
					String tichusString = con.lines.get(1);
					for(int i=0;i<4 && i<tichusString.length();i++){
						try{
							tichus[i] = Integer.parseInt(tichusString.substring(i, i+1));
						}
						catch(Exception e){System.out.println("GetCurrent:Tichus not Integer!");}
					}
					players = Integer.parseInt(con.lines.get(2));
					String idsString[] = con.lines.get(3).split("|");
					for(int i=0;i<4 && i<idsString.length;i++){
						if( idsString[i] == "0" ){
							activePlayers[i] = false;
						}
						else{
							activePlayers[i] = true;
						}
					}
					if( cstate == intState ){
						//con.status = "same\n"+cstate;
						con.clear();
					}
					else{
						cstate = intState;
						
						stateFlag = true;
						//con.status = "changed";
					}
				}
				catch(Exception e){
					System.out.print("EXCEPTION STATE\n ST:"+state+"\n PL:");
				}
			}
			//System.out.print("P");
			con.clear();
		}
		};
		con.request();
		return;
	}
	
	private void setCurrentHand(String string) {
		if ( string == null ){currentHand.cards.clear();showHand = true;return;}
		currentHand.cards.clear();
		String[] arr = string.split(",");
		try{
			for(int i=0;i<arr.length;i++){
				if( arr[i].length() < 0 )continue;
				if( arr[i].contains(".") ){
					currentHand.cards.add(deck.getCard(Card.PHOENIX));
					System.out.println("SUBSTR OF PHOENIX:"+ arr[i].substring(2) +"\nSTROF PHOENIX:" + arr[i]);

					currentHand.cards.get(i).weight = Integer.parseInt(arr[i].substring(2));
				}
				else{
					currentHand.cards.add(deck.getCard(Integer.parseInt(arr[i])));
				}
			}
		}
		catch(Exception e){
			System.out.println("SETCURRENTHAND - EXCEPTION!");
		}
		if( this.dragon == 1 )showHand = false;
		else showHand = true;
		Hand.sortCards(currentHand.cards);
	}

	private void getCurrent() {
		if( con.status == "null" ){	
			con.setURL(WEBSERVER + "get.php");
			con.setParam("tableId", String.valueOf(tableId));
			con.setParam("myId", String.valueOf(myId));
			con.setParam("function", "getCurrent");
			con.request();
		}
		else if(con.status == "success" ){
			
				// Get Num of Card Per person Sorted By id
				for(int i=1;i<=4;i++){
					numOfCards[i-1] = Integer.parseInt( con.lines.get(i) );
				}
				// Get Sundiasmo Fulla On the floor
				currentHand = new Hand();
				currentHand.combination = Integer.parseInt( con.lines.get(5) );
				currentHand.by = Integer.parseInt( con.lines.get(6) );
				if( currentHand.combination != 0 ){
					setCurrentHand( con.lines.get(7) );
				}
				else{
					setCurrentHand( null );
				}
				askedCard = Integer.parseInt(con.lines.get(8));
				turn = Integer.parseInt(con.lines.get(9));
				dragon = Integer.parseInt(con.lines.get(10));
				bomb = Integer.parseInt(con.lines.get(11));
				pointsTo = Integer.parseInt(con.lines.get(12));
				playing = Integer.parseInt(con.lines.get(13));
				syncCards( con.lines.get(14) );
				teamPoints = Integer.parseInt(con.lines.get(15));
				teamPoints2 = Integer.parseInt(con.lines.get(16));
				String tichusString[] = con.lines.get(17).split(",");
				for(int i=0;i<4 && i<tichusString.length;i++){
					try{
						tichus[i] = Integer.parseInt(tichusString[i]);
					}
					catch(Exception e){System.out.println("GetCurrent:Tichus not Integer!");}
				}
				
				//MyTurn
				if( turn == myId ){
					//pause();
					playFlag = true;
				}
				else{
					playFlag = false;
				}
				
				// Get my cards
				// NOT DONE YET -- MIGHT NOT IN need!
				con.clear();
				stateFlag = false;
		}		
	}
	// Helps for get Current
	private void syncCards(String string) {
		if( string.equals("-") ){
			return;
		}
		String str[] = string.split(",");
		cards.clear();
		for(int i=0;i<str.length;i++){
			cards.add(deck.getCard(Integer.parseInt(str[i])));
		}
		npc.cards.clear();
		for( Card a : cards ){
			npc.cards.add(a);
		}
	}
	public void fold(){
		Lcon.clear();
		Lcon.setURL(WEBSERVER + "set.php");
		Lcon.setParam("tableId", String.valueOf(tableId));
		Lcon.setParam("myId", String.valueOf(myId));
		Lcon.setParam("function", "fold");		
		Lcon.onFinish = new Runnable(){
			@Override
			public void run() {
				if( Lcon.getFirstLine() != "" ){
					try{
					int t = Integer.parseInt(Lcon.getFirstLine());
						if( t != 0 ){
							if( t == -2 ){
								System.out.print("NotURTurn");
							}
							else{ 
								Lcon.clear();
								//Lcon.disable();
								fold();
								System.out.println("RETRYING TO FOLD !");
							}
						}
						playFlag = false;
						resume();
					}
					catch(Exception e){
						Lcon.clear();
						//Lcon.disable();
						fold();
						return;
					}
				}
				Lcon.clear();
				stateFlag = false;
				//Lcon.disable();
			}
		};
		Lcon.request();
		return;	
	}
	public void callTichu(final int val) {
		Lcon.clear();
		Lcon.setURL(WEBSERVER + "set.php");
		Lcon.setParam("tableId", String.valueOf(tableId));
		Lcon.setParam("myId", String.valueOf(myId));
		Lcon.setParam("val", String.valueOf(val));
		Lcon.setParam("function", "callTichu");
		Lcon.onFinish = new Runnable(){
			@Override
			public void run() {
				if( Lcon.getFirstLine() != "" ){
					try{
						int t = Integer.parseInt(Lcon.getFirstLine());
						if( t != 0 ){
							if( t == -1 ){
								System.out.print("HAVE PLAYED");
							}
							else if( t == -2 ){
								System.out.print("NotURTurn");
							}
							else if( t == -3 ){
								System.out.print("Have Called Already");
							}
							else{
								Lcon.clear();
								callTichu ( val );
								return;
							}
						}
						resume();
					}
					catch(Exception e){
						Lcon.clear();
						//Lcon.disable();
						callTichu(val);
						return;
					}
				}
				Lcon.clear();
				//Lcon.disable();
				System.out.print("Grand called!");
			}
		};
 		Lcon.request();
	}
	private void sendHand( final Hand h , final int askedCard){
		h.setPhoenix();
		System.out.println(h.getStringCards());
		Lcon.clear();
		Lcon.setURL(WEBSERVER + "set.php");
		Lcon.setParam("tableId", String.valueOf(tableId));
		Lcon.setParam("myId", String.valueOf(myId));
		Lcon.setParam("function", "setHand");
		Lcon.setParam("combination", String.valueOf(h.combination));
		Lcon.setParam("cards", h.getStringCards());
		Lcon.setParam("askedCard", String.valueOf(askedCard));
		Lcon.onFinish = new Runnable(){
			@Override
			public void run() {
				if( Lcon.getFirstLine() != "" ){
					try{
					int t = Integer.parseInt(Lcon.getFirstLine());
						if( t != 0 ){
							if( t == -1 ){
								System.out.print("WrongHand");
							}
							else if( t == -2 ){
								System.out.print("NotURTurn");
							}
							else{ 
								Lcon.clear();
								//Lcon.disable();
								sendHand(h , askedCard);
								System.out.println("RETRYING TO FOLD !");
							}
						}
						playFlag = false;
						resume();
					}
					catch(Exception e){
						Lcon.clear();
						//Lcon.disable();
						sendHand(npc.hand, askedCard);
						return;
					}
				}
				Lcon.clear();
				stateFlag = false;
				//Lcon.disable();
				System.out.print("Hand HAS BEEN SENT");
			}
		};
		Lcon.request();
		h.resetPhoenix();
		return;	
	}
	public void display(){
		
	}
	private void delay(){
		delay(500);
	}
	private void delay(int ms){
		try {
		    Thread.sleep(ms);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	public void destroy(){
		if( myId >= 0 && myId <= 3 && cstate == 0 ){
			String res = excutePost(WEBSERVER+"set.php","tableId="+String.valueOf(tableId)+"&myId="+String.valueOf(myId)+"&function=leaveSeat");
			//String res = excutePost(WEBSERVER+"set.php","tableId="+String.valueOf(tableId)+"&myId="+String.valueOf(myId)+"&function=destroy");
			if( res.contains("00") ){
				System.out.println("Disconected");
				active = false;
			}
		}
		active = false;
	}
	public boolean isMyTurn(){
		return this.playFlag;
	}
	public void pause(){
		this.pause = true;
	}
	public void resume(){
		this.pause = false;
	}
	@Override
	public void run() {
		while( active ){
			gameLoop();
		}
	}
	public int playHand(Vector<CardActor> actors, boolean bombFlag) {
		npc.getHand(actors);
		npc.hand.setCombination(npc.hand.getCombination());
		for( Card a : npc.hand.cards ){
			if( a.id == Card.MAJHONG ){
					return -1;
			}			
		}
		// NEED ASKEDCARD  ? 
		if( this.askedCard > 0 && !bombFlag ){
			// HAVE ASKEDCARD ? 
			boolean flag = false,flag2 = false;
			for( Card a: npc.cards ){
				if( a.weight == this.askedCard ){
					flag2 = true;
				}
			}
			// CAN PLAY ASKED CARD ? 
			////////////////////////// 
			if( flag2 ){
				flag = npc.hand.canPlayAskedCard(npc.cards , currentHand, askedCard);
				if( flag ){
					boolean flag3 = false;
					for( Card a: npc.hand.cards ){
						if( a.weight == this.askedCard ){
							flag3 = true;
						}
					}
					if( !flag3  )return -2;
				}
			}
		} 
		boolean res = npc.hand.isBetter(currentHand);
		if( !res )return 0; 
		while( con.isActive() ){delay(300);}
		// is better hand ?
		this.sendHand(npc.hand , -1);
		return 1;
	}
	public int playHand(Vector<CardActor> actors , int askedCard) {
		npc.getHand(actors);
		npc.hand.setCombination(npc.hand.getCombination());
		if( askedCard == -1 ){
			return -1;
		}
		else{
			// ZITA KARTA !!!!!!!!!!!!!!!!
			//TO-DO
			
		}
				
		if( !npc.hand.isBetter(currentHand) )return 0; 
		while( con.isActive() ){delay(500);}
		// is better hand ?
		this.sendHand(npc.hand, askedCard);
		return 1;
	}
	public boolean isActive() {
		return active;
	}
	
	public static String excutePost(String targetURL, String urlParameters)
	  {
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "application/x-www-form-urlencoded");
	      connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");  
	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);
	      //Send request
	      DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
	      wr.writeBytes (urlParameters);
	      wr.flush ();
	      wr.close ();
	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	      }
	      rd.close();
	      return response.toString();

	    } catch (Exception e) {

	      e.printStackTrace();
	      return null;

	    } finally {

	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	  }
	public void restart(){
		// INIT VALUES
		tradeSetSend = false;switchFlag = false;active = true;pause = false;showHand = false;
		stateFlag = true;
		init(deck);
	}
	
	public void setInterval(float seconds){
		INTERVAL = seconds;
	}
	public void gameLoop(){
		/*
		 * ToDo -> check for error on requests! -> cases -> closedTable -> Wrong Hand -> Sync cards 
		 */
		if( this.pause ){return;}
		if( con.isBusy() )return;
		//if( con.status == "p" )return;
		
		time  += Gdx.graphics.getDeltaTime();
		if( time > INTERVAL ){
			time -= INTERVAL;
		}
		else return;
		if( tableId != -1 && myId != -1 ){ 
			this.stayAlive();
			this.isPlaying();
		}
		if( con.status == "failed" ){
			System.out.println("Failed! Line: " + con.getFirstLine() );
		}
		//if( con.status == "changed" ){System.out.println("CHANGED");}		
		if( tableId == -1 ){
			System.out.println("\nFinding Table!");
			findRandomTable();
		}		 
		else if( stateFlag ){
			//System.out.println("State Changed!");
			if( myId == -1 && cstate == 0 ){
				System.out.println("Getting ID!");
				getId();
			}
			else if( cstate == 1 && npc.cards.size() == 0  ){
				getNames();
				System.out.println("Drawing Cards!");
				drawCards();
			} 
			else if( cstate == 2 ){
				getTradeSet();
			}
			else if( cstate < 0 ){
				System.out.println("DESTROYED! PLAYER " + (-cstate-1) + " DISCONNECTED!");
				active = false;
				stateFlag = false;
				Gdx.app.exit();
				// TODO Announce the fact and solve properly
			}
			else{
				getCurrent();
				return;
			}
		}
		else if( cstate == 1 && npc.cards.size() > 0 && npc.getTradeSet() != null && !tradeSetSend ){
			sendTradeSet();
		}
		else{
			final GamePlayer temp = this;
			//System.out.println("STATECHANGE");
			Gdx.app.postRunnable(new Runnable() {
		         @Override
		         public void run() {
		            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
		        	 parent.game.cstate = temp.cstate;
		        	 parent.game.npc = temp.npc;
		        	 parent.game.currentHand= temp.currentHand;
		        	 parent.game.tableId = temp.tableId;
		        	 parent.game.myId = temp.myId;
		         }
		      });
			stateChanged();
			delay(1000);

		}
		
		//Get Player done -^ implement in getCurrent
		//getPlayer();
		/*/ if me -> me.play()
		if( turn == myId ){
			Hand h = npc.getHand();
			if( h != null ){
				sendHand(h);
			}
		}*/
	}
	private void isPlaying() {
		String res = excutePost(WEBSERVER+"isplaying.php", "tableId="+tableId);
		int a = 1;
		try{
			a = Integer.parseInt(res);
		}catch(Exception e){}
		if( a == 0 ){
			this.playing = 0;
			System.out.println("NOT PLAYING");
		}
	}
	private void getNames(){
		String res = null;
		try{
			res = excutePost(WEBSERVER+"get.php", "tableId="+tableId+"&myId="+myId+"&function=getNames" );
		}
		catch(Exception e){
			System.out.print("CANT CONNECT TO GET NAMES!");
		}
		if( res.contains("e") ){
			System.out.print("ERROR GETING NAMES!");
		}
		else{
			names = res.split("\\|"); 
			System.out.println("GOT NAMES : " + names.length);
		}
	}
	public void stayAlive() {
		excutePost(WEBSERVER+"valid.php", "myId="+myId+"&tableId="+tableId);	
		System.out.println("Alive!");
	}
}


