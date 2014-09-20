package com.kiklopas.titchu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;

public class Card extends Sprite {
	// TO-DO add color 
	public static final int PHOENIX = 2,
							DRAGON = 1,
							MAJHONG = 0,
							DOGS = 3;
	public static final float SCALE = 0.35f;
	public int weight;
	public int color;
	public String fileName;
	public int id;
	public String num = "";
	
	private TextureRegion createCardByText(char c, int color, BitmapFont font , Pixmap oldtile,Pixmap fontPixmap , Pixmap ff){
		 // load the background into a pixmap

        // load the font
		// get the glypth info
        BitmapFontData data = font.getData();
        Glyph glyph = data.getGlyph(c);
        
        Pixmap tile = new Pixmap (oldtile.getWidth(), oldtile.getHeight(), oldtile.getFormat());
        int w = tile.getWidth();
        int h = tile.getHeight();
        for(int x = 0;x<w;x++)for(int y=0;y<h;y++)tile.drawPixel(x, y,oldtile.getPixel(x, y));
        
        if( c!= '1' ){       
      
        // draw the character onto our base pixmap
        tile.drawPixmap(fontPixmap, 55 - glyph.width/2 , 40-glyph.height/2,
               glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
        tile.drawPixmap(fontPixmap, tile.getWidth()- 60 - glyph.width/2 , 40-glyph.height/2,
                glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
        tile.drawPixmap(ff, 55 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
        tile.drawPixmap(ff, tile.getWidth()- 60 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
        }
        else{
        	
            // draw the character onto our base pixmap
            tile.drawPixmap(fontPixmap, 40 - glyph.width/2 , 40-glyph.height/2,
                   glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
            tile.drawPixmap(fontPixmap, tile.getWidth()- 78 - glyph.width/2 , 40-glyph.height/2,
                    glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
            tile.drawPixmap(ff, 72 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                    fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
            tile.drawPixmap(ff, tile.getWidth()- 43 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                    fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
            
            glyph = data.getGlyph('0');
            
            // draw the character onto our base pixmap
            tile.drawPixmap(fontPixmap, 74 - glyph.width/2 , 40-glyph.height/2,
                   glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
            tile.drawPixmap(fontPixmap, tile.getWidth()- 43 - glyph.width/2 , 40-glyph.height/2,
                    glyph.srcX,  glyph.srcY, glyph.width, glyph.height);
            tile.drawPixmap(ff, 40 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                    fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
            tile.drawPixmap(ff, tile.getWidth()- 78 - glyph.width/2 , tile.getHeight()-40-glyph.height/2,
                    fontPixmap.getWidth()-glyph.srcX-glyph.width,  fontPixmap.getHeight()-glyph.srcY-glyph.height, glyph.width, glyph.height);
        	
        }
        
        // save this as a new texture
		return new TextureRegion(new Texture(tile));
	}
	
	public Card(String file ,int w ,int c , int id, BitmapFont font, Pixmap tile, Pixmap fontPixmap , Pixmap ff1){
		fileName = file;
		TextureRegion region = null;
		Texture texture = null;
		// TODO FOR DEBUG
		switch( file.charAt(0) ){
			case '1':
				region = createCardByText(file.charAt(0),c, font, tile,fontPixmap, ff1);
				break;
			case 'm':
				texture = new Texture(Gdx.files.internal("data/deck/mahlong.png"));
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
				break;
			case 'p':
				texture = new Texture(Gdx.files.internal("data/deck/phoenix.png"));
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
				break;
			case 'd':
				texture = new Texture(Gdx.files.internal("data/deck/dragon.png"));
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
				break;
			case 'o':
				texture = new Texture(Gdx.files.internal("data/deck/dogs.png"));
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
				break;
			default:
				region = createCardByText(file.charAt(0),c,font,tile,fontPixmap, ff1);
				break;
		
		}
		weight = w;
		color = c;
		this.id = id;
		
		super.set(new Sprite(region));
	}
	// 
	public Card(String string) {
		fileName = string;
		Texture texture = new Texture(Gdx.files.internal("data/deck/"+fileName));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
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
	public void draw(SpriteBatch batch){
		super.draw(batch);
	}
}
