package com.kiklopas.titchu;

import java.util.Vector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Tichu implements ApplicationListener {
	public static final int CARD_SPACING = 70;
	private static final int ASK_CARD_WIDTH = 32;
	private static final int ASK_CARD_HEIGHT = 52;
	private static final float FONT_SCALE = 1.f;
	
	private Stage stage;
	private Vector<CardActor> actors;
	private Vector<CardActor> currentHand;
	
	Vector<Button> buttons;
	Vector<Button> mainButtons;
	private boolean tradeFlag = true;

	public GamePlayer game;
	private boolean showCardsFlag = true;
	private DragAndDropTest a;
	private Vector<CardActor> trades;
	private int gamestate = 0;
	private Button send;
	private Button recieve;
	private BitmapFont font;
	int w,h;
	private Vector<Vector<CardActor>> opponents;
	private boolean playFlag = false;
	public Thread thread;
	private Vector<Button> askCardButtons;
	private Vector<Button> dragonButtons;
	private boolean asking = false;
	private boolean dragon = false;
	@SuppressWarnings("unused")
	private float debugX,debugY;
	public boolean loading = true;
	private Vector<Button> grandButtons;
	private boolean grand = true;
	private boolean grandCalled = false;
	private boolean tichuCalled = false;
    private Button tichu;
	private boolean restartFlag = false;
	private AssetManager manager;
	public boolean roundFinished = false;
	private int score;
	private int score1;
	private int tableId = -1;
	private int myId = -1;
	private Vector<Button> finishButtons;
	private Button bomb;
	private boolean bombFlag;
	private boolean tichuShownFlag = true;
	private Vector<Vector<Button>> tichusVec;
	private boolean drawFlag = false;
	private MsgBox msgbox;
	private Deck deck ;
	public boolean gameError = false;
	private Timer timer;
	public Tichu(AssetManager manager, Deck deck){
		this.manager = manager;
		this.deck = deck;
	}
	
	public Tichu(AssetManager manager, int score, int score1, int tableId, int myId, Deck deck) {
		this.manager = manager;
		this.score = score;
		this.score1 = score1;
		this.tableId = tableId;
		this.myId  = myId;
		this.deck = deck;
	}

	@Override
	public void create() {	
		// TODO 
		//		DRAGON SEND LEFT RIGHT 	  // Fixed but need recheck! maybe it appear twice
		// 	 	END OF Tichu Display FIX  // Bypassed that for the moment
		// 		CHECK END at 1000
		
		// TODO FOR HTML VERSION
		// 		REMOVE THREADS 		
		//		ADD game.run();
		
		//w = Gdx.graphics.getWidth();
		//h = Gdx.graphics.getHeight();

		w = 480;
		h = 640;
				
		Texture texture = new Texture(Gdx.files.internal("data/font/lucida.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
		
		font = new BitmapFont(Gdx.files.internal("data/font/lucida.fnt"), new TextureRegion(texture), false);
		//font = new BitmapFont();
		font.setScale((float)FONT_SCALE);
		font.setColor(new Color(1,1.f,1.f, 1.0f));
		
	
		//NEW
		game = new GamePlayer(this,deck); // Extends Runnable
		game.teamPoints = score;
		game.teamPoints2 = score1;
		game.tableId = this.tableId;
		game.myId = this.myId;
		thread = new Thread((Runnable)game , "NETWORK");
		thread.setDaemon(false);
		thread.start();
		
		
		System.out.println("\n\nCurrentThread:"+Thread.currentThread().getName());
		//System.out.print("PRIORITY ="+t.getPriority()+"\n");
		//END NEW
		
		opponents = new Vector<Vector<CardActor>>();
		for(int i=1;i<=4;i++){
			Vector<CardActor> temp = new Vector<CardActor>();
			opponents.add(temp);
		}
		
		// CARDS PL
		actors = new Vector<CardActor>();
		
		//STAGE STUFF
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        stage.setViewport(480, 640,false);
        currentHand = new Vector<CardActor>();


		
		
        /*/* LOADING SCREEN
        Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	       stage.getSpriteBatch().begin();
				font.draw(stage.getSpriteBatch(), "LOADING...", 10, 100);
	       stage.getSpriteBatch().end();
         */
    
        Button temp;
		//*/BACKGROUND
		temp = new Button("background.png",manager);
		temp.selectable = false;
		temp.setBounds(0, 0, 480, 640);
		temp.setZIndex(0);
		stage.addActor(temp);
		//*/
		trades = new Vector<CardActor>();
        a = new DragAndDropTest();
		a.create(stage ,trades);
		
		// BUTTON STUFF
		//  GRAND DRAW
		buttons = new Vector<Button>();
		grandButtons = new Vector<Button>();
		temp = new Button("grand.png",manager){
			public void onclick(){
				System.out.print("GRAND\n");
				game.callTichu(2);
				showCardsFlag = true;
				grandCalled = true;// show a grand sign
				grand = false;
				showArray(trades);
				drawFlag = true;
				hideButtonArray(grandButtons);
			}
		};
		temp.setBounds(20, 270, 140, 56);
		//temp.mode = Button.ONE_TIME;
		buttons.add(temp);
		grandButtons.add(temp);
		stage.addActor(temp);
		//DRAW
		temp = new Button("play.png",manager){
			public void onclick(){
				System.out.print("Draw\n");
				showCardsFlag = true;
				grand = false;
				showArray(trades);
				drawFlag = true;
				hideButtonArray(grandButtons);
			}
		};
		temp.setBounds(170, 270, 140, 56);
		buttons.add(temp);
		grandButtons.add(temp);
		stage.addActor(temp);
		// MAIN BUTTONS
		mainButtons = new Vector<Button>();
		// TICHU BUTTON
		tichu = new Button("tichu.png",manager){
			public void onclick(){
				System.out.print("TICHU CALLED\n");
				game.callTichu(1);
				tichuCalled = true;// show a tichu sign
				tichu.setVisible(false);
			}
		};
		tichu.setBounds(20, 336, 140, 56);
		tichu.setVisible(false);
		//tichu.mode = Button.ONE_TIME;
		//buttons.add(tichu);
		//mainButtons.add(tichu);
		stage.addActor(tichu);
		// OTHER MAIN BUTTONS
		//PLAY
		temp = new Button("play.png",manager){
			public void onclick(){
				System.out.print("PLAY\n");
				if( bombFlag ){
					game.npc.getHand(actors);
					if( game.npc.hand.getCombination() != Hand.BOMB ){
						//TODO !!!! ^ check this if statement
						msgbox.setMessage("You must play your Bomb!");msgbox.showMsg();
						return;
					}
				}
				for( CardActor a: actors ){
					if( a.selected )
					System.out.print(a.card.weight + " , " ) ; 
				}
				System.out.print("\n");
				int flag = game.playHand(actors, bombFlag);
				if( flag == 1 ){
					removeFromHand(actors);
					tichu.setVisible(false);
					bombFlag = false;
				}
				else if( flag == -1 )requestCard();
				else if( flag == -2 ){
					msgbox.setMessage("You must play the asked card: " + game.askedCard + " !");msgbox.showMsg();
					return;
				}
			}
		};
		temp.setBounds(170, 270, 140, 56);
		buttons.add(temp);
		mainButtons.add(temp);
		stage.addActor(temp);
		//PASO
		temp = new Button("fold.png",manager){
			public void onclick(){
				if ( game.currentHand.combination == 0 || game.currentHand.combination == -1 ){
					msgbox.setMessage("You must play something!");msgbox.showMsg();
					System.out.println("CannotFold");return;
				}
				if( game.askedCard > 0 ){
					if( game.npc.hand.canPlayAskedCard(game.npc.cards , game.currentHand, game.askedCard) ){
						msgbox.setMessage("You must play the asked card!");msgbox.showMsg();
						return;
					}
				}
				System.out.print("FOLD\n");
				game.fold();
			}
		};
		temp.setBounds(320, 270, 140, 56);
		buttons.add(temp);
		mainButtons.add(temp);
		stage.addActor(temp);
		//BOMB
		temp = new Button("bomb.png",manager){

			public void onclick(){
				System.out.println("BOMB!!!");
				if( game.currentHand.combination == Hand.NOTHING || game.currentHand.combination == Hand.DOGS )return;
				//*
				if( !isBombBetter() ){
					msgbox.setMessage("You must select a bomb better than the table combination!");msgbox.showMsg();
					return;
				}//*/
				String res = GamePlayer.excutePost(GamePlayer.WEBSERVER+"set.php", "tableId="+String.valueOf(game.tableId)+"&myId="+String.valueOf(game.myId)+"&function=bomb");
				if( res.contains("00") ){
					System.out.println("Bomb deployed!");
					bomb.setVisible(false);
					bombFlag = true;
				}
				
				return;
			}

			private boolean isBombBetter() {
				game.npc.getHand(actors);
				return game.npc.hand.isBetter(game.currentHand);
				//Hand temp = game.npc.getBestBomb();
				//return Hand.isBombBetter(game.npc.hand, game.currentHand);
			}
		};
		temp.setBounds(20, 270, 140, 56);
		bomb = temp;
		buttons.add(temp);
		mainButtons.add(temp);
		stage.addActor(temp);
		//Send Trades
		send = new Button("trade.png",manager){

			public void onclick(){
				//System.out.println("Send Pressed! ");
				send.setVisible(false);
				tradeFlag = false;
				game.npc.setTradeSet(trades);
				game.resume();
			}		
		};
		send.setBounds(170, 270, 140, 56);
		buttons.add(send);
		stage.addActor(send);
		//Recieve Trades <-- <-- <-- 
		recieve = new Button("tichu.png",manager){
			public void onclick(){
				//System.out.println("Recieve Pressed! ");
				recieve.setVisible(false);
				game.npc.addTradeSetToCards();
				fixCards();
			}	
		};
		recieve.setBounds(170, 270, 140, 40);
		buttons.add(recieve);
		stage.addActor(recieve);
		
		
		// BUTTON STUFF
		// DRAGON SEND
		dragonButtons = new Vector<Button>();
		// RIGHT +3 // CAUSE OF anapoda ta turn
		temp = new Button("right.png",manager){
			public void onclick(){
				System.out.print("RIGHT\n");
				boolean flag = game.sendDragon(3);	
				if( !flag )return;
				hideButtonArray(buttons);
				showButtonArray(mainButtons);
				showHand();
			}
		};
		temp.setBounds(330, 370, 40, 140);
		buttons.add(temp);
		dragonButtons.add(temp);
		stage.addActor(temp);
		// LEFT +1
		temp = new Button("left.png",manager){
			public void onclick(){
				System.out.print("LEFT\n");
				boolean flag = game.sendDragon(1);
				if( !flag )return;
				hideButtonArray(buttons);
				showButtonArray(mainButtons);
				showHand();
			}
		};
		temp.setBounds(100, 370, 40, 140);
		buttons.add(temp);
		dragonButtons.add(temp);
		stage.addActor(temp);
		
		//*//////////////////////////////////
		//  T / G shown
		/////////////////////////////////
		tichusVec = new Vector<Vector<Button>>();
		for(int i=0;i<4;i++)tichusVec.add(new Vector<Button>());
		Vector2 c = new Vector2(w, h);
		Color col = new Color();col.set(1.f, 0, 0, 1.f);
		
		temp = new Button("tichu.png",manager);
		temp.selectable = false;
		temp.setBounds(20, 336, 70, 28);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(0).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		temp = new Button("t.png",manager);
		temp.selectable = false;
		temp.setBounds( c.x*0.02f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(1).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		temp = new Button("t.png",manager);
		temp.selectable = false;
		temp.setBounds(c.x*0.30f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(2).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		temp = new Button("t.png",manager);
		temp.selectable = false;
		temp.setBounds(c.x*0.87f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(3).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		col.set(1.f, 0, 0, 1.f); // grand color
		////////////////////////////////
		temp = new Button("grand.png",manager);
		temp.selectable = false;
		temp.setBounds(20, 336, 70, 28);
		temp.setColor(col);
		temp.setZIndex(11);
		tichusVec.get(0).add(temp);
		stage.addActor(temp);
		//////////////////////////////
		temp = new Button("g.png",manager);
		temp.selectable = false;
		temp.setBounds(c.x*0.02f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(1).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		temp = new Button("g.png",manager);
		temp.selectable = false;
		temp.setBounds(c.x*0.30f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(2).add(temp);
		stage.addActor(temp);
		/////////////////////////////////
		temp = new Button("g.png",manager);
		temp.selectable = false;
		temp.setBounds(c.x*0.87f, c.y*0.7f+110, 25, 35);
		temp.setColor(col);
		temp.setZIndex(10);
		tichusVec.get(3).add(temp);
		stage.addActor(temp);
		//*/
		
		askCardButtons = new Vector<Button>();
		//Majong Card Ask! 
		temp = new Button("askedCards/1.gif",manager){
			public void onclick(){
				for ( Button a : askCardButtons ){
					a.selected = false;
				}
				this.selected = true;
			}
		};
		temp.info = 0;
		temp.setBounds(10 + 1, 350, ASK_CARD_WIDTH, ASK_CARD_HEIGHT );
		askCardButtons.add(temp);
		buttons.add(temp);
		stage.addActor(temp);
		for( int i = 2 ;i <= 14 ; i ++ ){
			temp = new Button("askedCards/0.gif",manager){
			//temp = new Button("askedCards/askedCard.png"){
				public void onclick(){
					for ( Button a : askCardButtons ){
						a.selected = false;
					}
					this.selected = true;
				}
			};
			temp.info = i;
			temp.setBounds(10 + (i-1)*ASK_CARD_WIDTH + i , 350, ASK_CARD_WIDTH, ASK_CARD_HEIGHT );
			askCardButtons.add(temp);
			buttons.add(temp);
			stage.addActor(temp);
		}
		temp = new Button("play.png",manager){
			public void onclick(){
				hideButtonArray(askCardButtons);
				asking = false;
				int askedCard = -1;
				for( Button a : askCardButtons ){
					if( a.selected ){askedCard = a.info ;break;} 
				}
				System.out.print("PLAY AFTER ASKING FOR MAJHONG! \n");
				int flag = game.playHand(actors , askedCard );
				if( flag == 1 ){
					removeFromHand(actors);
					tichu.setVisible(false);
				}
				else if( flag == -1 )requestCard();
				else{
					return;
				}
			}
		};
		temp.mode = Button.CLICK;
		temp.setBounds(170 , 270, 140 , 56);
		askCardButtons.add(temp);
		buttons.add(temp);
		stage.addActor(temp);
		/////////////// END MAJON ///
		// CONTINUE LEVEL //
		finishButtons = new Vector<Button>();
		Button but = new Button("opacity.png",manager);
		but.selectable = false;
		//but.opacity = 0.55f;
		but.setBounds(0 ,0 , w, h);
		buttons.add(but);
		finishButtons.add(but);
		stage.addActor(but);		
		but = new Button("panel.png",manager);
		but.selectable = false;
		but.setBounds(w/2 - 200/2, h/2 - 300/2, 200, 300);
		buttons.add(but);
		finishButtons.add(but);
		stage.addActor(but);
		but = new Button("continue.png",manager){
			@Override
			public void onclick(){
				roundFinished = true;
			}
		};
		but.setBounds(w/2 - 140/2, h/2 - 300/2 + 56, 140, 56);
		buttons.add(but);
		finishButtons.add(but);
		stage.addActor(but);
		////////////////////////////////////
		
		
		////////////////////////////////////
		for( Vector<Button> a : tichusVec ){
			hideButtonArray(a);
		}
		//*/////////////////////////////////
		hideButtonArray(buttons);//HIDE ALL BUTTONS
		//showButtonArray(askCardButtons);
		//send.setVisible(true);
		//showButtonArray(finishButtons);
		
		//LISTENER
		stage.addListener(new InputListener(){
			private Vector2 actorDv;
			private Actor selectedActor;
        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//System.out.println("Down: " + x + " "+ y);
				//game.pause();
				Actor actor = stage.hit(x, y, true);
        		if( actor == null )return true;
        		
        		//game.pause();
        		if( gamestate == 1 ){
        			try{
	        			CardActor cardactor = (CardActor)actor;
	        			if( actors != null ){
		        			if( !actors.contains(cardactor) ){
		        				return true;
		        			}
	        			}else{return true;}
	        			for( CardActor a: trades)
	        				if( a.dropped == cardactor ){
	        					a.dropped = null;
	        				}	        			
	        			cardactor.zIndex = cardactor.getZIndex();
	        			actorDv = cardactor.stageToLocalCoordinates(new Vector2(x,y));
						cardactor.setZIndex(30);
						cardactor.isFull = true;
						selectedActor = actor;
					}
					catch(ClassCastException e){
						//System.out.print("Cannot Cast To Actor!-!\n");
					}
        			
        		}
        		else if( gamestate > 2 ){     		
						try{
	        			CardActor cardactor = (CardActor)actor;
	        			if( cardactor.selectable )
	        				cardactor.selected = !cardactor.selected;
						}
						catch(ClassCastException e){
							//System.out.print("Cannot Cast To Actor!\n");
						}
        		}
				//this.touchDragged(event, x, y, pointer);
				return true;
			}
        	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        		//System.out.println("up " + x + " " + y);
        		//game.resume();
        		if( gamestate == 1 && selectedActor!=null && tradeFlag && drawFlag){
        			try{
	    				CardActor card = (CardActor)selectedActor;
	    				card.setZIndex(card.zIndex);
	    				
	    				MoveToAction action = new MoveToAction();
	    				
	    				card.setZIndex(0);
	    				boolean flag = false;
	    				Actor temp = stage.hit(x, y, false);
	    				if( trades.contains(temp) ){
	    						action.setPosition(temp.getX(),temp.getY());
	    						trades.get(trades.indexOf(temp)).dropped = card;
	    						flag = true;
	    				}				
	    				boolean aflag = true;
						for( CardActor a:trades ){
							if( a.dropped == null ){aflag = false;break;}
						}
						send.setVisible(aflag);
						
	    				card.setZIndex(card.zIndex);
	    				
	    				if( !flag )action.setPosition(card.pos.x , card.pos.y);   				
	    				action.setDuration(0.5f);
	    				card.addAction(action);
	    			}
	    			catch(ClassCastException e){
	    				//System.out.print("Cannot Cast To Actor!TouchUP.\n");
	    			}
        			
        		}
        		Actor actor = stage.hit(x, y, true);
        		if( actor == null )return;
        		
        		if( y > 255 ){
        			try{
	        			Button but = (Button)actor;
	        			if( but.mode == Button.CLICK ){
	        				but.selected = false;
	        				but.fireEvent = true;
	        			}
	        			else if( but.mode == Button.ONE_TIME ){
	        				but.selected = true;
	        			}
        			}
        			catch(ClassCastException e){
						//System.out.print("Cannot Cast To Button2!\n");
					}
        		}
				
        		selectedActor = null;
				return;
			}
        	public void touchDragged(InputEvent event, float x, float y, int pointer) {
        		debugX = x;
        		debugY = y;
        		Actor actor = stage.hit(x, y, true);
        		if( actor == null )return;
        		if(gamestate == 1 && selectedActor != null && tradeFlag && drawFlag ){
	    			try{
	    				CardActor card = (CardActor) selectedActor;
	    				card.clearActions();
	    				MoveToAction action = new MoveToAction();
	    				action.setPosition(x-actorDv.x , y-actorDv.y);
	    				action.setDuration(0.05f);
	    				card.addAction(action);
	    			}
	    			catch(ClassCastException e){
	    				//System.out.print("Cannot Cast To Button3!\n");
	    			}
				}
        		
        		if( y > 255 ){       		
        			try{
        				Button but = (Button)actor;
        				for(int i=0;i<buttons.size();i++){
                			if( !buttons.get(i).isVisible() )continue;
                			if( buttons.get(i).mode == Button.ONE_TIME )continue;
                			buttons.get(i).selected = false;
                		}
        				if( but.mode == Button.CLICK ){
        					but.selected = true;
        				}
        			}
        			catch(ClassCastException e){
						//System.out.print("Cannot Cast To Button3!\n");
					}	
        		}
        		
        		
        		
				return;
			}        	
		});
		//InputListener a;
		
		/// SETUP MSGBOX

		msgbox = new MsgBox("this is a new game with the ability to fuck things up" , manager , stage);
		msgbox.setX(50);
		msgbox.setY(200);
		msgbox.hideMsg();
		
		/*
		CardActor custom = new CardActor(new Card("10" ,1 ,2 , -1));
		custom.setBounds(10, 10, 133, 200);
		stage.addActor(custom);
		*/
		
		loading = false;
		game.setInterval(GamePlayer.SLOW_INTERVAL);
		game.resume();
	}

	protected boolean canPlayAskedCard() {
		boolean flag = false;
		for( Card a: game.npc.cards ){
			if( a.weight == game.askedCard ){
				flag = true;
			}
		}
		if( flag ){
			if( game.currentHand.combination == Hand.STRAIGHT ){
				Vector<Card> vec = new Vector<Card>();
				for( Card a: game.npc.cards ){ vec.add(a); }
				removeDuplicatesByWeight(vec);
				Hand.sortCards(vec);
				boolean hasPhoenix = false;
				for(Card a: vec){
					if ( a.weight == Card.PHOENIX ){
						hasPhoenix = true;break;
					}
				}
				boolean usedPhoenix = false;
				int i = 1;
				int w = vec.get(0).weight;
				if( w == Card.PHOENIX ){w=vec.get(1).weight;i++;}
				Vector<Integer> straight = new Vector<Integer>();
				straight.add(w);
				for(;i<vec.size();i++){
					if( Math.abs(vec.get(i).weight - w) == 1 ){
						w = vec.get(i).weight;
						straight.add(w);
					}
					else if( Math.abs(vec.get(i).weight - w) == 2 && !usedPhoenix){
						w = vec.get(i).weight;
						straight.add(w);
						usedPhoenix = true;
					}
					else{
						int siz = straight.size();
						if(hasPhoenix){siz++;}
						if( siz >= game.currentHand.cards.size() ){
							for( Integer car: straight ){
								if(car==game.askedCard){
									return true;
								}
							}
						}
						w = vec.get(i).weight;
						usedPhoenix = false;
						straight.clear();
						straight.add(w);
					}
				}
			}
			else{
				if( game.currentHand.cards.get(0).weight > game.askedCard )return false;
				return true;
			}
		}
		return false;
	}
	public void removeDuplicatesByWeight(Vector<Card> v)
	{ 
	     for(int i=0;i<v.size();i++){
	             for(int j=0;j<v.size();j++)
	                 {
	                     if(i!=j)
	                         {
	                             if(v.elementAt(i).weight == v.elementAt(j).weight )
	                                 {
	                                     v.removeElementAt(j);
	                                 }
	                         }
	                 }
	     }
	}
	
	public void requestCard() {
		// ASK CARD FROM MAJHONG
		asking = true;
		hideButtonArray(buttons);
		showButtonArray(askCardButtons);	
	}

	@Override
	public void dispose() {
		System.out.print("DISPOSED");
		game.destroy();
		stage.dispose();
	}

	@Override
	public void render() {	
		//game.run();
		if( Gdx.input.isKeyPressed(Keys.A) ){
			//showButtonArray(finishButtons);
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// CHECK Disconections
		if( game.playing == 0 ){
			this.gameError = true;
		}
		
		for( Button b : buttons ){
			b.fireEvents();
		}
		tichu.fireEvents(); // SPECIFIC
		 
		int templ = game.cstate;
		if( gamestate !=  templ){
			gamestate = templ;
			this.stateChange(gamestate);
		}
		if( gamestate == 1 && restartFlag ){
			hideButtonArray(buttons);
			game.pause();
			// TODO maybe hideArray(actors) instead of setting Z-index
			for( Button a: finishButtons){
				a.setZIndex(100);
			}
			showButtonArray(finishButtons);
			roundFinished = true;
			restartFlag = false;			
		}
		else if( gamestate == 1 && game.npc.cards.size() > 0 && showCardsFlag ){
				System.out.println("CARDS!!!" + game.npc.cards.size());
				showCards(game.npc.cards);
		}
		else if( gamestate == 2 && game.switchFlag ){
			grand= false;
			// TODO recieve not needed!
			recieve.setVisible(false);
			switchTrades(game.npc.tradeSet, game.npc.oldTradeSet);
		}
		else if( gamestate == 3 ){
			//* NO REASON FOR THIS Its JUST FOR CONSISTENCY!
			//  ^^ REASON TO SHOW TICHU
			showOpponents();
			if( !grandCalled && tichuShownFlag ){
				tichu.setVisible(true);
				tichuShownFlag = false;
			}
			//////////////////////
			grand= false;
			stateChange(gamestate);
			if( playFlag != game.isMyTurn() ){
				playFlag = game.isMyTurn();
			}
			if( !areCardsSynced(game.npc.cards) ){
				cleanCards();
				showCardsAll(game.npc.cards);
			}
			if( game.showHand )this.showHand();//*/
		}
		else{
			if( playFlag != game.isMyTurn() ){
				playFlag = game.isMyTurn();
				if( playFlag ){
					game.setInterval(GamePlayer.SLOW_INTERVAL);
				}
				else{
					game.setInterval(GamePlayer.FAST_INTERVAL);
				}
				stateChange(gamestate);
			}
			if( !areCardsSynced(game.npc.cards) ){
				cleanCards();
				showCardsAll(game.npc.cards);
			}
				
			if( game.showHand )this.showHand();
		}
		
		
        
        
        if( Gdx.input.isKeyPressed(Input.Keys.A) ){
        	Hand hand = new Hand();
        	hand.getHand(actors);
        }
        
        
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        showOpponentCards();
        showTichus();
        
        stage.getSpriteBatch().begin();
			font.draw(stage.getSpriteBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 100);
			font.draw(stage.getSpriteBatch(), "A.C.: " + game.askedCard, 10, 130);
			//font.draw(stage.getSpriteBatch(), "+ X:"+String.valueOf(debugX) + " \n  Y:"+String.valueOf(debugY), debugX, debugY);

			//Vector2 temp = stage.screenToStageCoordinates(new Vector2(w/5,h/20));
			float w = stage.getWidth();
			float h = stage.getHeight();
			Vector2 temp = new Vector2(w/5,h/19);
			
			switch( gamestate ){
				case -1:
					font.draw(stage.getSpriteBatch(), "Waiting For Table", temp.x, h/2 );
					break;
				case 0:
					font.drawMultiLine(stage.getSpriteBatch(), "Waiting For Players\n\tConnected: "+game.players, temp.x*1/2, h/2 );
					break;
				case 1:
					font.draw(stage.getSpriteBatch(), "Make Trades", temp.x, temp.y );
					if( !restartFlag && drawFlag ){
						showArray(trades);
					}
					break;
				case 2:
					font.draw(stage.getSpriteBatch(), "Get Trades", temp.x, temp.y );
					hideArray(trades);
					break;
				default:
					if( playFlag  ) font.draw(stage.getSpriteBatch(), "MyTurn", temp.x, temp.y );
					else font.draw(stage.getSpriteBatch(), "Playing", temp.x, temp.y );
					break;
			}
			if( asking ){
				drawAskFont();
			}
		font.setScale(0.8f);
		font.draw(stage.getSpriteBatch(), "Score: "+String.valueOf(game.teamPoints)+"-"+String.valueOf(game.teamPoints2), 2, h-20);
		
		//font.draw(stage.getSpriteBatch(), String.valueOf(game.teamPoints2), w-80, h-22);
		
		if( gamestate >= 1 ){
			font.draw(stage.getSpriteBatch() , game.names[(game.myId + 1)%4] , 2 , h-220);
			font.draw(stage.getSpriteBatch() , game.names[(game.myId + 2)%4] , w/2 , h - 90);
			font.draw(stage.getSpriteBatch() , game.names[(game.myId + 3)%4] , w-100 , h - 220);
		}
		font.setScale(FONT_SCALE);
		
		game.npc.getHand(actors);
		font.draw(stage.getSpriteBatch(), (game.npc.hand.getCombination()==0)? "" : String.valueOf(game.npc.hand.getCombination()), w/2, h-100);	
		if( game.currentHand != null ){
			if( game.currentHand.combination != Hand.NOTHING ){
				if( game.currentHand.by == game.myId )font.draw(stage.getSpriteBatch(), "Played by me", w/2 - 110, h-100);
				else font.draw(stage.getSpriteBatch(), "Played by "+game.names[game.currentHand.by] , w/2 - 130, h-100);
			}
		}
		if( game.askedCard > 0 ){
			char a[] = {'J','Q','K','A'};
			char asked = (char) ((game.askedCard < 11)?game.askedCard:a[game.askedCard-11]);
			font.draw(stage.getSpriteBatch(), "Asked Card "+asked , w/2 - 100, h-125);
		}
		stage.getSpriteBatch().end();
		
		
		msgbox.draw(stage.getSpriteBatch(),font);
		
	}
	private void showTichus() {
		if ( game.myId < 0 || game.myId > 3 )return;
		for( int i=0; i< 4;i++){
			int a = game.tichus[(game.myId+i)%4];
			if( a == 1 ){
				this.tichusVec.get(i).get(0).setVisible(true);
			}
			else if( a == 2 ){
				this.tichusVec.get(i).get(1).setVisible(true);
			}
		}
	}

	private void cleanCards() {
		for(CardActor a: actors){
			stage.getRoot().removeActor(a);
		}
		actors.clear();
	}

	private boolean areCardsSynced(Vector<Card> cards) {
		if( gamestate < 3 )return true;
		if( cards.size() != actors.size() )return false;
		boolean flag;
		try{
		for( Card a: cards ){
			flag = true;
			for(CardActor b: actors){
				if( b.card.equals(a) ){
					flag = false;break;
				}
			}
			if( flag ){
				return false;
			}
		}
		}
		catch(Exception e){
		}
		return true;
	}

	private void drawAskFont() {
		//font.setScale(2.f);
		String a[] = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
		for( int i = 2 ;i <= 14 ; i ++ ){
			if( i!=10 && i!=12){
				font.draw(stage.getSpriteBatch(), a[i-2], 14 + (i-1)*ASK_CARD_WIDTH + i , 395);
			}else{
				font.setScale(0.8f);
				font.draw(stage.getSpriteBatch(), a[i-2], ((i==12)?13:11) + (i-1)*ASK_CARD_WIDTH + i , 393);
				font.setScale(1f);
			}
		}		
		//font.setScale(1.8f);
	}

	private void showOpponentCards() {
		if(true)return;
		@SuppressWarnings("unused")
		Vector2 c = new Vector2(w, h);
		Vector2 relVec[] = { new Vector2(c.x*0.02f,c.y*0.7f+20), new Vector2( c.x/2-90 , c.y*0.98f ) , new Vector2(c.x*0.98f-100,c.y*0.7f+20)};	
		for(int j=1;j<4;j++){
			int num = (j+game.myId)%4;
			stage.getSpriteBatch().begin();
				String test = String.valueOf(game.numOfCards[num]);
				font.draw(this.stage.getSpriteBatch(), test, relVec[j-1].x, relVec[j-1].y);
			stage.getSpriteBatch().end();
		}		
	}

	private void showHand(){
		if( game.dragon == 1 && game.currentHand.combination == Hand.NOTHING ){
			return;
		}
		if( game.currentHand != null ){
			if( game.currentHand.combination == Hand.NOTHING ){
				int relpos = game.pointsTo - game.myId;
				if( relpos < 0 )relpos += 4;
				System.out.println("RELPOS"+relpos);
				for(final CardActor a:currentHand){
					a.setOrigin(a.getWidth()/2, a.getHeight()/2);
					OnEndMoveToAction action = new OnEndMoveToAction();
					action.setDuration(0.5f);
					float ax =a.getX() + ((relpos == 3)?(150):(0)) -((relpos == 1)? (150):(0)) ;
					float ay = a.getY() + ((relpos == 2)? (150):(0)) - ((relpos == 0)? (150):(0));
					System.out.println("ax:"+ax+" a.getX:"+a.getX());
					System.out.println("ay:"+ay+" a.getY:"+a.getY());
					action.setPosition( ax , ay );
					action.onFinish = new Runnable(){
						public void run(){
							a.remove();
							System.out.println("FINISHED");
						}
					};
					ScaleToAction saction = new ScaleToAction();
					saction.setDuration(0.2f);
					saction.setScale(0.3f);
					
					a.addAction(Actions.parallel(action,saction));
				}
			}
			else{
				for(CardActor a:currentHand){
					a.remove();
				}
			}
			currentHand.clear();
			
			int i=0;
			for( Card a: game.currentHand.cards ){
				CardActor act = new CardActor(a);
				//CHECK X!
				float x = (w/2);
				float y = 340;
				act.setBounds( x - (((game.currentHand.cards.size()<7)?game.currentHand.cards.size():6)*CARD_SPACING+80)/2  + CARD_SPACING*(i%6) + 20 , ((game.currentHand.cards.size()<7)? y:y+CARD_SPACING) -CARD_SPACING*(int)(i/6), (int)(140/1.5), (int)(207/1.5));
				//act.setPos(new Vector2(CARD_SPACING*2/3*(i%7),  (w/2-game.currentHand.cards.size()*CARD_SPACING*2/3)));
				act.setZIndex(i);
				act.setOrigin(0, 0);
				act.setVisible(true);
				currentHand.add(act);
				stage.addActor(act);
				i++;
			}
			
		}
		game.showHand = false;
	}
	private void stateChange(int state) {
		updateOpponentsCards();

		if( state >= 3 ){
			restartFlag  = true;
			if( playFlag ){
				if( game.currentHand.by == game.myId && game.dragon == 1){
					hideButtonArray(buttons);
					showButtonArray(dragonButtons);
				}
				else if( !asking ){
					hideButtonArray(buttons);
					showButtonArray(mainButtons);
				}
				if( game.dragon == 1 ){
					game.resume();
				}
				else if( !dragon ){
					//game.pause();
				}
			}
			else{
				hideButtonArray(buttons);
				playFlag = false;
				game.resume();
			}
			if( recieve.isVisible() ){
				recieve.setVisible(false);
				game.npc.addTradeSetToCards();
			}
			if( state < 5 ){
				fixCards();
			}
		}
		else{
			switch( state ){
				case 0:
					
					break;
				case 1:
					//showButtonArray(grandButtons);
					break;
				case 2:
					hideArray(trades);
					break;
			}
		}
		if( game.npc.hasBomb() && !bombFlag ){
			bomb.setVisible(true);
		}
		else{
			bomb.setVisible(false);
		}
		
		//showCards(game.npc.cards);
	}
	private void updateOpponentsCards(){
		for(int j=1;j<4;j++){
			int num = (j+game.myId)%4;
			if ( game.numOfCards[num] < opponents.get(num).size() ){
				for(int i=opponents.get(num).size()-1;i>=game.numOfCards[num];i--){
					opponents.get(num).get(i).setVisible(false);
				}
			}
			else{
				continue;
			}
		}
	}
	private void showOpponents() {
		//Vector2 c = stage.screenToStageCoordinates(new Vector2(w, 0));
		Vector2 c = new Vector2(w, h);
		Vector2 relVec[] = { new Vector2(c.x*0.02f,c.y*0.75f), new Vector2( c.x*0.45f , c.y*0.85f ) , new Vector2(c.x*0.98f-50,c.y*0.75f)};
				
		for(int j=1;j<4;j++){
			int num = (j+game.myId)%4;
			if ( game.numOfCards[num] != opponents.get(num).size() ){
				opponents.get(num).clear();
			}
			else{
				continue;
			}
			for(int i=0;i<game.numOfCards[num];i++){
				CardActor actor = new CardActor( new Card( "back.png" ) );
				Vector2 posVec;
				if( j!=2 )
					posVec = new Vector2((i%2)*5,(int)(i)*(-5) );
				else posVec = new Vector2((int)(i)*(5),(i%2)*5 ); 
				posVec = posVec.add( relVec[j-1] );
				actor.setPosition( posVec.x , posVec.y );
				actor.setSize( (int)133/3 , (int)200/3 );
				actor.selectable = false;
				opponents.get(num).add( actor );
				stage.addActor( actor );
			}
			this.stage.getSpriteBatch().begin();
				String test = String.valueOf(game.numOfCards[num]);
				this.font.draw(this.stage.getSpriteBatch(), test, relVec[j-1].x, relVec[j-1].y+40);
			this.stage.getSpriteBatch().end();
		}
	}

	public static  void showArray(Vector<CardActor> temp){
		for(CardActor a : temp)a.setVisible(true);
	}
	public static  void showButtonArray(Vector<Button> temp){
		for(Button a : temp)a.setVisible(true);
	}
	public static  void hideArray(Vector<CardActor> temp){
		for(CardActor a : temp)a.setVisible(false);
	}
	public static void hideButtonArray(Vector<Button> temp){
		for(Button a : temp)a.setVisible(false);
	}
	
	private void fixCards() {
		CardActor temp;
		int s = actors.size();
		for(int i=1;i<s;i++)
			for(int j=s-1;j>=i;j--){
				if( actors.get(j).card.weight < actors.get(j-1).card.weight ){
					temp = actors.get(j-1);
					actors.remove(j-1);
					actors.add(j, temp);
				}
				//else if same weight
			}
		
		for( int i=0;i<actors.size();i++){
			MoveToAction action = new MoveToAction();
			Vector2 tvec = new Vector2(CARD_SPACING*(i%7), 60 - ((int)(i/7))*120);
			action.setPosition(tvec.x,tvec.y);
			action.setDuration(0.5f);
			actors.get(i).setZIndex(50+i);
			actors.get(i).pos = tvec;
			actors.get(i).addAction(action);
		}
	}	
	
	@Override
	public void resize(int width, int height) {
        //Gdx.gl.glViewport(0, 0, 480, 640);
		//stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
		game.pause();
	}

	@Override
	public void resume() {
		game.resume();
	}
	
	public void switchTrades(Vector<Card> newVec , Vector<Card> oldVec){
		System.out.println("[*] Switching ------ N:" + newVec.size() +" | O:" + oldVec.size());
		while(newVec.size()!=3){
			delay();
		}
		if( newVec.size() != 3 || oldVec.size() != 3 )return;
		CardActor j = null;
		for(int i=0;i<3;i++){
			for(CardActor a:actors)if( oldVec.contains(a.card) )j = a; 
			if( j == null ){
				System.out.print("ERROR SWITCHING SETS ARE WRONG!");
				return;
			}
			actors.get(actors.indexOf(j)).setCard(newVec.get(i));
			j = null;
		}
		System.out.println("[!] Switched Trades!" );
		game.switchFlag = false;
	}
	

	public void showCards(Vector<Card> cards){  
		if( gamestate == 1 && grand ){
			showButtonArray(grandButtons);
		}
		if( cards.size() <= 0 )return;
		showCardsFlag = false;
		
		// ACTOR STUFF
		CardActor a;
		int from = (grand )? 0:8;
		int limit = (grand )? 8: cards.size();
		for( int i=from;i<limit ;i++){
			a = new CardActor(cards.get(i%cards.size()));
			a.setBounds(CARD_SPACING*(i%7), 60 - ((int)(i/7))*120, 133, 200);
			a.setPos(new Vector2(CARD_SPACING*(i%7), 60 - ((int)(i/7))*120));
			a.setZIndex(i);
			a.setOrigin(0, 0);
			actors.add(a);
			stage.addActor(a);
		}
		//game.pause();
		fixCards();
	}
	public void showCardsAll(Vector<Card> cards){  
		if( cards.size() <= 0 )return;
		showCardsFlag = false;
		// ACTOR STUFF
		CardActor a;
		for( int i=0;i<cards.size();i++){
			a = new CardActor(cards.get(i%cards.size()));
			a.setBounds(CARD_SPACING*(i%7), 60 - ((int)(i/7))*120, 133, 200);
			a.setPos(new Vector2(CARD_SPACING*(i%7), 60 - ((int)(i/7))*120));
			a.setZIndex(i);
			a.setOrigin(0, 0);
			actors.add(a);
			stage.addActor(a);
		}
		//game.pause();
		fixCards();//Instant
		
	}
	public void removeFromHand(Vector<CardActor> actors) {
		for( CardActor a: actors){
			if( a.selected ){
				a.selected = false;
				a.remove();
				game.npc.cards.remove(a.card);
			}
		}
		fixCards();
	}
	private void delay() {
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
}