package com.kiklopas.titchu;

import sun.misc.Regexp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MsgBox extends Sprite{
 
		public static final int LINE_LENGTH = 25;
	
		public String msg;
		public int color;
		public String fileName;
		private boolean visible = true;
		Button b;
		//private BitmapFont font;
		
		public MsgBox(String msg , AssetManager manager, Stage stage ){
			/*Texture texture = new Texture(Gdx.files.internal("data/font/comic.png"), true); // true enables mipmaps
			texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
			
			font = new BitmapFont(Gdx.files.internal("data/font/comic.fnt"), new TextureRegion(texture), false);
			//font = new BitmapFont();
			font.setScale((float) 1.8);
			font.setColor(new Color(1,1.f,1.f, 1.0f));
			*/
			fileName = "msgbox.png";
			// TODO FOR DEBUG
			Texture texture = new Texture(Gdx.files.internal("data/"+fileName));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			TextureRegion region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());

			setMessage(msg);
			final MsgBox self = this;
			b = new Button("msgboxbtn.png", manager){
				public void onclick(){
					System.out.print("OK PRESSED\n");
					self.hideMsg();
				}
			};
			b.selectable = true;
			b.setVisible(true);
			stage.addActor(b);
			
			super.set(new Sprite(region));
		}
		public void setWidth(float size){
			super.setSize(size, size * getHeight() / getWidth());
			return;
		}
		public TextureRegion getRegion(){
			return this;
		}
		public void dispose(){
			return;
		}
		public void setCenterOrigin() {
			setOrigin(getWidth()/2, getHeight()/2);
			return;
		}
		public void setMessage(String msg){
				// parse msg
				String temp[] = msg.split("\\s+");
				String t = "";
				int p = 0;
				for( int i=0;i<temp.length;i++){
					if( p+ temp[i].length() > LINE_LENGTH){
						t += "\n";
						p = 0;
					}
					t+= temp[i] + " ";
					p+= temp[i].length() + 1;
				}
				this.msg = t;
		}
		public void draw(SpriteBatch batch, BitmapFont font){
			if( !this.visible )return;
			
			b.setBounds(this.getX()+142, this.getY()+45, 105, 42);
			b.fireEvents();
			batch.begin();
			super.draw(batch);
			float sx = font.getScaleX();
			float sy = font.getScaleY(); 
			font.setScale(0.8f);
			font.drawMultiLine(batch, this.msg, 60+this.getX(), 260+this.getY());
			//font.draw(batch, "OK", 155+this.getX(), 50+this.getY());
			font.setScale(sx, sy);
			b.draw(batch, 1);

			batch.end();
		}
		public void showMsg(){
			this.visible = true;
			b.setVisible(true);
		}
		public void hideMsg(){
			this.visible = false;
			b.setVisible(false);
		}

}
