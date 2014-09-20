package com.kiklopas.titchu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Button extends Actor {
	public boolean selected;
	public boolean fireEvent;
	TextureRegion region;
	static public int ONE_TIME = 0, 
			   CLICK = 1;
	public int mode = CLICK;
	//public boolean visible = true;
	public int info;
	public boolean selectable = true;
	public float opacity = 1;
	
	public Button(String filename, AssetManager manager) {
		Texture texture = manager.get("data/"+filename, Texture.class);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        selected = false;
        fireEvent = false;
        opacity = 1;
	}
	
	public void draw (SpriteBatch batch, float parentAlpha) {
        Color color = getColor();
        if( this.selected && this.selectable ){
        	color.set(1f,0.2f,0.2f,opacity );
        }
        else{
        	color.set(1,1,1,opacity);
        }
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        //batch.disableBlending();
	}
	
	public void fireEvents(){
		if( fireEvent ){
			this.onclick();
			fireEvent = false;
		}		
		return;
	}
	
	public void onclick(){
		return;
	}
	
	public Actor isHit(float x , float y , boolean touchable){
		if( !isVisible() )return null;
    	float width = this.getWidth();
    	float height = this.getHeight();
    	if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < width && y >= 0 && y < height ? this : null;    
    }
}
