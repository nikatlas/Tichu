package com.kiklopas.titchu;

import java.util.Vector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class ScoreScreen implements ApplicationListener {
	
	public boolean finished = false;
	private AssetManager manager;
	private BitmapFont font;
	private Stage stage;
	public Vector<Button> buttons;
	public int score , score1;
	public ScoreScreen(AssetManager manager, int score, int score1) {
		this.manager = manager;
		this.score = score;
		this.score1 = score1;
	}

	@Override
	public void create() {
		int w = 480;
		int h = 640;
				
		Texture texture = new Texture(Gdx.files.internal("data/font/comic.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
		
		font = new BitmapFont(Gdx.files.internal("data/font/comic.fnt"), new TextureRegion(texture), false);
		//font = new BitmapFont();
		font.setScale((float) 1.8);
		font.setColor(new Color(1,1.f,1.f, 1.0f));
		
	
		//STAGE STUFF
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        stage.setViewport(480, 640,false);

        Button temp;
		//*/BACKGROUND
		temp = new Button("background.png",manager);
		temp.selectable = false;
		temp.setBounds(0, 0, w, h);
		temp.setZIndex(0);
		stage.addActor(temp);
		
		// BUTTON STUFF
		//  GRAND DRAW
		buttons = new Vector<Button>();
		temp = new Button("continue.png",manager){
			public void onclick(){
				finished = true;
			}
		};
		temp.setBounds(480/2-70, 640/2-70, 140, 56);
		//temp.mode = Button.ONE_TIME;
		buttons.add(temp);
		stage.addActor(temp);
		
		//*/////////////////////////////////
		//Tichu.hideButtonArray(buttons);//HIDE ALL BUTTONS
		
		stage.addListener(new InputListener(){
        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {return true;}
        	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {       		
        		//System.out.print("Touchup!\n");
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
        	public void touchDragged(InputEvent event, float x, float y, int pointer) {}        	
		});
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		for( Button a : buttons ){
			a.fireEvents();
		}
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
        stage.getSpriteBatch().begin();
			font.draw(stage.getSpriteBatch(), "Final Score" , 480/2-80, 640/2+140);
			font.draw(stage.getSpriteBatch(), String.valueOf(score)+" - " + String.valueOf(score1) , 480/2-90, 640/2+80);
		stage.getSpriteBatch().end();
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

	}

}
