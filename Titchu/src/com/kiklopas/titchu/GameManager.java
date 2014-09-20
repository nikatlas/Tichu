package com.kiklopas.titchu;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.AssetManager;

public class GameManager implements ApplicationListener{
	
	private AssetManager manager;
	private ApplicationListener tichu;
	private int type = 0;
	public static final int Random = 0, Table = 1 , HaveID = 2 ;
	public int tableId = -1;
	public int myId = -1;
	public boolean finished = false;
	
	private int score,score1;
	private int state = 0;
	private boolean flag = true;
	private Deck deck;
	
	GameManager(AssetManager manager, int type, Deck deck ){
		this.type = type;
		this.manager = manager;
		this.deck = deck;
	}
	@Override
	public void create() {
		switch( state ){
			case 0: 
				switch(type){
					case Table:case HaveID:
						String res = GamePlayer.excutePost(GamePlayer.WEBSERVER+"get.php", "tableId="+String.valueOf(tableId)+"&function=getScore");
						String r[] = res.split(",");
						int score = Integer.parseInt(r[0]);
						int score1 = Integer.parseInt(r[1]);
						tichu = new Tichu(manager, score, score1, tableId, myId , deck);
						break;
					default:case Random:
						tichu = new Tichu(manager,deck);//, 0, 400, 13, 3);
						break;
				}
				tichu.create();
				break;
			case 1:
				break;
		}
		
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void render() {
		switch( state  ){
		case 0:
			if( ((Tichu)tichu).roundFinished  || ((Tichu)tichu).gameError ){
				score = ((Tichu)tichu).game.teamPoints;
				score1 = ((Tichu)tichu).game.teamPoints2;
				if( ((score >= 1000 || score1 >=1000) && score != score1) || ((Tichu)tichu).gameError ){
					// Show winner!
					state = 1;
					flag = true;
				}
				else{
					tichu.dispose();
					//tichu = null; // no reason for this line BAD BAD REASON tichu is needed down!!!
					tichu = new Tichu(manager, score, score1 , ((Tichu)tichu).game.tableId , ((Tichu)tichu).game.myId , deck);
					tichu.create();
				}
			}	
			break;
		case 1:
			if( flag ){
				tichu.dispose();
				tichu = new ScoreScreen(manager, score, score1);flag =false;
				tichu.create();
			}
			if( ((ScoreScreen)tichu).finished )finished = true;
			break;
		}

		tichu.render();

	}

	@Override
	public void pause() {
		tichu.pause();
	}

	@Override
	public void resume() {
		tichu.resume();
	}

	@Override
	public void dispose() {
		tichu.dispose();
	}

}
