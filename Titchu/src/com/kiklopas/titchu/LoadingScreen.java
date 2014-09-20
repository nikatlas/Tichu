package com.kiklopas.titchu;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LoadingScreen implements ApplicationListener{

	private TextureRegion region;
	private SpriteBatch batch;
	float width,height;
	boolean IC = false;
	private BitmapFont font;
	@Override
	public void create() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		// TODO Auto-generated method stub
		// SETUP SOME FONTS
		Texture texture = new Texture(Gdx.files.internal("data/font/lucida.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
		
		font = new BitmapFont(Gdx.files.internal("data/font/lucida.fnt"), new TextureRegion(texture), false);
		//font = new BitmapFont();
		font.setScale((float) 0.6f);
		font.setColor(new Color(1,1.f,1.f, 1.0f));
		/////// END LOADING FONTS
		
		texture = new Texture(Gdx.files.internal("data/generic_loading.jpg"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		batch = new SpriteBatch();
		String res = null;
		try{
		res = GamePlayer.excutePost(GamePlayer.WEBSERVER + "test.php", "");
		}
		catch(Exception e){
			
		}
		System.out.println("TESTING : " +res);
		try{
			if( Integer.parseInt(res) == 1 ){
				IC = true;
			}
		}
		catch(Exception e){
			
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		batch.begin();
        if( IC ){
        	batch.draw(region, 0, 0, 0, 0, width, height, 1, 1, 1);
        }
        else{
        	if( Gdx.input.isTouched() )Gdx.app.exit();
        	font.drawMultiLine(batch, "There is no internet access!\n  Tap to Exit", 10, height/2 );
        }
        batch.end();
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
