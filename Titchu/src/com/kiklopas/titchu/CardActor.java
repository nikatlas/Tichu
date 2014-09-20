package com.kiklopas.titchu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class CardActor extends Actor {
    TextureRegion region;
	public int flag;
    public boolean selected;
    public int zIndex;
    public boolean erflag;
	public Card card;
	public boolean isFull;
	public Vector2 pos;
	public CardActor dropped;
	public boolean selectable = true;
	private Texture cardTexture;
	
    public CardActor(Card reg) {
    		card = reg;
            region = new TextureRegion(reg);
            flag = 1;
            selected = false;
            erflag = false;
    }
    
    public CardActor() {
        flag = 1;
        selected = false;
        erflag = false;	
    }
    
	public void setCard(Card reg){
    	card = reg;
    	region = new TextureRegion(reg);
    }
    
    public void setPos(Vector2 position){
    	pos = position;
    }
    public Actor isHit(float x , float y , boolean touchable){
    	float width = this.getWidth();
    	if( !this.isFull )  width = Tichu.CARD_SPACING;
    	float height = this.getHeight();
    	if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < width && y >= 0 && y < height ? this : null;    
    }
    public Actor hit(float x, float y, boolean touchable){
    	int z = this.getZIndex();
    	if( isHit(x,y,touchable) == this ){
    		return this;
    	}
    	else if( z < 13 ){
    		return super.hit(x, y, touchable);
    	}
    	return null;
    }
    public void draw (SpriteBatch batch, float parentAlpha) {
    		if( !isVisible() )return;
            Color color = getColor();
            if( this.selected ){
            	color.set(0.2f,1,0.2f,1);
            }
            else{
            	color.set(1,1,1,1);
            }
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
	public void onTop() {
		this.setZIndex(20);		
	}
}