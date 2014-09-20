package com.kiklopas.titchu;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class ActivityManager implements ApplicationListener{
	ApplicationListener stage;
	Preferences prefs;
	private AssetManager manager;
	public static final short Loading = 11,
							  StartMenu = 0,
							  Game = 1
						;
	public int activityState = Loading;
	private boolean activityFlag = true;
	private int tableId;
	private int myId;
	private boolean IC = false;
	private Deck deck;
	@Override
	public void create() {
		// TODO Auto-generated method stub
		prefs = Gdx.app.getPreferences("tichuInfo");
		manager = new AssetManager();

		manager.load("data/login.png", Texture.class);
		manager.load("data/register.png", Texture.class);
		manager.load("data/exit.png", Texture.class);
		manager.load("data/connect.png", Texture.class);
		manager.load("data/random.png", Texture.class);
		manager.load("data/logout.png", Texture.class);
		
		manager.load("data/opacity.png", Texture.class);


		manager.load("data/font/comic.png", Texture.class);
		manager.load("data/font/comic.fnt", BitmapFont.class);
		manager.load("data/background.png", Texture.class);
		manager.load("data/tichu.png", Texture.class);
		manager.load("data/grand.png", Texture.class);		
		manager.load("data/left.png", Texture.class);
		manager.load("data/right.png", Texture.class);

		manager.load("data/fold.png", Texture.class);
		manager.load("data/play.png", Texture.class);
		manager.load("data/bomb.png", Texture.class);
		manager.load("data/trade.png", Texture.class);
		manager.load("data/t.png", Texture.class);
		manager.load("data/g.png", Texture.class);
		manager.load("data/panel.png", Texture.class);
		manager.load("data/continue.png", Texture.class);
		manager.load("data/askedCards/1.gif", Texture.class);
		manager.load("data/askedCards/0.gif", Texture.class);
		manager.load("data/msgboxbtn.png", Texture.class);
		deck = new Deck();
		stage = new LoadingScreen();
		stage.create();
		IC = ((LoadingScreen)stage).IC;
		if( !IC ){
			System.out.println("NO INTERNET CONNECTION");
		}
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		stage.resize(width, height);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		if( !IC ){
			
		}
		else{
			if( manager.update() && activityFlag ){
				switch(activityState){
					case Loading:
						activityState = StartMenu;
					case StartMenu:
						stage.dispose();
						stage = new Menu(manager);
						stage.create();
						break;
					case Game:
						stage.dispose();
						stage = new GameManager(manager, GameManager.Random, deck);
						((GameManager)stage).tableId = tableId;
						((GameManager)stage).myId = myId;
						stage.create();
						break;
				}
				activityFlag = false;
			}
			checkForChanges();
		}
		stage.render();
	}

	private void checkForChanges() {
		if( !manager.update() )return;
		switch(activityState){
			case StartMenu:
				Menu temp = (Menu)stage;
				if( temp.startRandom ){
					activityState = Game;
					tableId = -1;
					myId = -1;
					temp.startRandom = false;
					activityFlag = true;
				}
				break;
			case Game:
				GameManager temp1 = (GameManager)stage;
				if( temp1.finished ){
					activityFlag = true;
					activityState = StartMenu;
				}
				break;
		}
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		stage.pause();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		stage.resume();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}
	
}
