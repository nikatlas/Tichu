package com.kiklopas.titchu;

import java.util.Vector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Menu implements ApplicationListener {
	private BitmapFont font;
	private Stage stage;
	private Vector<Button> buttons;
	private float w;
	private float h;
	private AssetManager manager;
	private float debugX=100.0f,debugY=50.0f;
	TextListener username,password;
	private Button connect;
	private Vector<Button> playButtons;
	private Vector<Button> loginButtons;
	Preferences prefs;
	public boolean startRandom = false;
	private Button register;
	
	Menu(AssetManager manager){
		this.manager = manager;		
	}
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
		w = 480;
		h = 640;
		
		// PREFERENCES
		prefs =  Gdx.app.getPreferences("tichuInfo");
		// TEXT INPUT
		username = new TextListener();
		password = new TextListener();
		
		Texture texture = new Texture(Gdx.files.internal("data/font/lucida.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
		
		font = new BitmapFont(Gdx.files.internal("data/font/lucida.fnt"), new TextureRegion(texture), false);
		//font = new BitmapFont();
		font.setScale((float) 0.8);
		font.setColor(new Color(1,1.f,1.f, 1.0f));
		//STAGE
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
        stage.setViewport(w, h,false);
                
		        
		Button temp;
		//*/BACKGROUND
		temp = new Button("background.png",manager);
		temp.selectable = false;
		temp.setBounds(0, 0, 480, 640);
		temp.setZIndex(0);
		stage.addActor(temp);
		//*/
		// BUTTONS ///////////////
		buttons = new Vector<Button>();
		playButtons = new Vector<Button>();
		loginButtons = new Vector<Button>();
		//////////////////////
		temp = new Button("login.png",manager){
			public void onclick(){
				System.out.print("Login\n");
					Gdx.input.getTextInput(password, "Login - password", "Enter password");
					Gdx.input.getTextInput(username, "Login - username", "Enter username");
					hideButtonArray(buttons);
					connect.setVisible(true);
			}
		};
		temp.selectable = true;
		temp.setBounds(w/2-70, h/2-28 +66, 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		loginButtons.add(temp);
		buttons.add(temp);
		///
		temp = new Button("register.png",manager){
			public void onclick(){
				Gdx.input.getTextInput(password, "Login - password", "Enter password");
				Gdx.input.getTextInput(username, "Login - username", "Enter username");
				hideButtonArray(buttons);
				register.setVisible(true);
			}
		};
		temp.setBounds(w/2-70, h/2-28 , 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		loginButtons.add(temp);
		buttons.add(temp);
		///
		temp = new Button("exit.png",manager){
			public void onclick(){
				System.out.print("Exit\n");
				Gdx.app.exit();
			}
		};
		temp.setBounds(w/2-70, h/2-28 - 66 , 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		buttons.add(temp);
		loginButtons.add(temp);
		////
		connect = new Button("connect.png",manager){

			public void onclick(){
				System.out.println("Connect\n"+username.text+"\n");
				String res = GamePlayer.excutePost(GamePlayer.WEBSERVER+"get.php", "username="+username.text+"&password="+password.text+"&function=login");
				if( res == null ){
					System.out.println("NO RESULT");
				}
				else if( res.contains("00") ){
					System.out.println("LOGGED IN!!!");
					prefs.putString("username", username.text);
					prefs.putString("password", password.text);
					prefs.putString("id", res.substring(2));
					prefs.putBoolean("logged", true);
					prefs.flush();
					hideButtonArray(buttons);
					showButtonArray(playButtons);
				}
				else{
					System.out.println("Account doesnt Exists!" + res);
					showButtonArray(loginButtons);
					connect.setVisible(false);
				}
			}
		};
		connect.setBounds(w/2-70, h/2-28 , 140, 56);
		connect.setZIndex(0);
		connect.setVisible(false);
		stage.addActor(connect);
		buttons.add(connect);
		///
		register = new Button("register.png",manager){

			public void onclick(){
				System.out.println("Register\n"+username.text+"\n");
				String res = GamePlayer.excutePost(GamePlayer.WEBSERVER+"set.php", "username="+username.text+"&password="+password.text+"&function=register");
				if( res == null ){
					System.out.println("NO RESULT");
				}
				else if( res.contains("00") ){
					System.out.println("Registered Logged IN!!!");
					prefs.putString("username", username.text);
					prefs.putString("password", password.text);
					prefs.putString("id", res.substring(2));
					prefs.putBoolean("logged", true);
					prefs.flush();
					hideButtonArray(buttons);
					showButtonArray(playButtons);
				}
				else{
					System.out.println("Account doesnt Exists!" + res);
					showButtonArray(loginButtons);
					connect.setVisible(false);
				}
			}
		};
		register.setBounds(w/2-70, h/2-28 , 140, 56);
		register.setZIndex(0);
		register.setVisible(false);
		stage.addActor(register);
		buttons.add(register);
		/////
		temp = new Button("random.png",manager){
			public void onclick(){
				System.out.print("FindRandomTable\n");
				startRandom = true;
			}
		};
		temp.setBounds(w/2-70, h/2-28 + 66 , 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		buttons.add(temp);
		playButtons.add(temp);
		////		
		temp = new Button("logout.png",manager){
			public void onclick(){
				System.out.print("logout\n");
				prefs.putBoolean("logged", false);
				prefs.flush();
				hideButtonArray(buttons);
				showButtonArray(loginButtons);
			}
		};
		temp.setBounds(w/2-70, h/2-28  , 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		buttons.add(temp);
		playButtons.add(temp);
		////////		
		temp = new Button("exit.png",manager){
			public void onclick(){
				System.out.print("Exit\n");
				Gdx.app.exit();
			}
		};
		temp.setBounds(w/2-70, h/2-28 - 66  , 140, 56);
		temp.setZIndex(0);
		stage.addActor(temp);
		buttons.add(temp);
		playButtons.add(temp);
		////////
		hideButtonArray(buttons);
		if( prefs.getBoolean("logged") ){
			showButtonArray(playButtons);		
		}else{showButtonArray(loginButtons);}
		// INPUT
		stage.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//System.out.println("Down: " + x + " " + y);
				
				return true;
			}
        	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        		//System.out.println("up " + x + " " + y);
        		        		
        		Actor actor = stage.hit(x, y, true);
        		if( actor == null )return;
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
        		
				return;
			}
        	public void touchDragged(InputEvent event, float x, float y, int pointer) {
				return;
			}        	
		});
		
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		for( Button b : buttons ){
			b.fireEvents();
		}
		
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
		if( prefs.getBoolean("logged") ){
			stage.getSpriteBatch().begin();
				font.draw(stage.getSpriteBatch(), "Logged in as "+prefs.getString("username") , w/5, h/2+170);
			stage.getSpriteBatch().end();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}
	private void hideButtonArray(Vector<Button> temp){
		for(Button a : temp)a.setVisible(false);
	}
	private void showButtonArray(Vector<Button> temp){
		for(Button a : temp)a.setVisible(true);
	}

}
