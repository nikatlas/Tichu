package com.kiklopas.titchu;

import java.util.Random;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

public class Deck {
	Vector<Card> cards;
	private Pixmap tile3;
	private BitmapFont font3;
	private Texture texture3;
	private BitmapFont font2;
	private Pixmap tile2;
	private Texture texture2;
	private Pixmap tile1;
	private Texture texture1;
	private Pixmap tile0;
	private BitmapFont font0;
	private BitmapFont font1;
	private Texture texture0;
	private BitmapFontData data;
	private Pixmap fontPixmap0;
	private Pixmap ff0;
	private Pixmap fontPixmap3;
	private Pixmap ff3;
	private Pixmap ff2;
	private Pixmap fontPixmap2;
	private Pixmap ff1;
	private Pixmap fontPixmap1;
	
	public Deck(){
		init();
	}

	private void init() {
		Reader r = new Reader("data/deck.txt");
		cards = new Vector<Card>();
		Card temp = null;
		
		
		// LOAD BACKGROUNDS FOR DRAWING
		texture0 = new Texture(Gdx.files.internal("data/font/blue.png"), true); // true enables mipmaps
		texture0.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image		
		font0 = new BitmapFont(Gdx.files.internal("data/font/blue.fnt"), new TextureRegion(texture0), false);
		tile0 = new Pixmap(Gdx.files.internal("data/deck/blue.png"));
        data = font0.getData();
        fontPixmap0 = new Pixmap(Gdx.files.internal(data.imagePath));
		ff0 = flipPixmap(fontPixmap0); 
		
		texture1 = new Texture(Gdx.files.internal("data/font/red.png"), true); // true enables mipmaps
		texture1.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image		
		font1 = new BitmapFont(Gdx.files.internal("data/font/red.fnt"), new TextureRegion(texture1), false);
		tile1 = new Pixmap(Gdx.files.internal("data/deck/red.png"));
		data = font1.getData();
        fontPixmap1 = new Pixmap(Gdx.files.internal(data.imagePath));
		ff1 = flipPixmap(fontPixmap1); 
		
		texture2 = new Texture(Gdx.files.internal("data/font/black.png"), true); // true enables mipmaps
		texture2.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image		
		font2 = new BitmapFont(Gdx.files.internal("data/font/black.fnt"), new TextureRegion(texture2), false);
		tile2 = new Pixmap(Gdx.files.internal("data/deck/black.png"));
		data = font2.getData();
        fontPixmap2 = new Pixmap(Gdx.files.internal(data.imagePath));
		ff2 = flipPixmap(fontPixmap2); 
		
		texture3 = new Texture(Gdx.files.internal("data/font/green.png"), true); // true enables mipmaps
		texture3.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image		
		font3 = new BitmapFont(Gdx.files.internal("data/font/green.fnt"), new TextureRegion(texture3), false);
		tile3 = new Pixmap(Gdx.files.internal("data/deck/green.png"));
		data = font3.getData();
        fontPixmap3 = new Pixmap(Gdx.files.internal(data.imagePath));
		ff3 = flipPixmap(fontPixmap3); 
		
		
		for(int i=0;i<r.lines.size();i+=3){
			String img = r.lines.get(i);
			int w = Integer.parseInt(r.lines.get(i+1));
			int c = Integer.parseInt(r.lines.get(i+2));
			
			
			switch(c){
			case 0:
				temp = new Card(img,w,c, i/3,font0, tile0, fontPixmap0 , ff0);
				break;
			case 1:
				temp = new Card(img,w,c, i/3,font1, tile1, fontPixmap1 , ff1);
				break;
			case 2:
				temp = new Card(img,w,c, i/3,font2, tile2, fontPixmap2 , ff2);
				break;
			case 3:
				temp = new Card(img,w,c, i/3,font3, tile3, fontPixmap3 , ff3);
				break;
			default:
				temp = new Card(img,w,c, i/3,font3, tile3, fontPixmap3 , ff3);
			}
			cards.add(temp);
		}
		ff0.dispose();
		ff1.dispose();
		ff2.dispose();
		ff3.dispose();
		fontPixmap0.dispose();
		fontPixmap1.dispose();
		fontPixmap2.dispose();
		fontPixmap3.dispose();
		texture0.dispose();
		texture1.dispose();
		texture2.dispose();
		texture3.dispose();
		tile0.dispose();
		tile1.dispose();
		tile2.dispose();
		tile3.dispose();
	}
	
	
	public Card getCard(int id){
		for(int i=0;i<cards.size();i++){
			if( cards.get(i).id == id )return cards.get(i);
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private void shuffle() {
		Random r = new Random();
		for(int i=0;i<cards.size()*100;i++){
			int c = ((int)(r.nextDouble()*1000)+r.nextInt(100))%cards.size();
			System.out.print(c + "!");
			Vector<Card> c1 = new Vector<Card>(cards.subList(0, c));
			Vector<Card> c2 = new Vector<Card>(cards.subList(c, cards.size()));			
			cards = c2;
			cards.addAll(c1);
			//debug();
		}
	}

	@SuppressWarnings("unused")
	private void debug(){
		System.out.print("DECK DEBUG!\n");
		for(int i=0;i<cards.size();i++){
			System.out.print("Card: " + cards.get(i).fileName + ",color :" + cards.get(i).color + ", Weight :" + cards.get(i).weight + "\n" );
		}
	}
	
	public Card getCardByWeight(int val){
		for( Card a  : this.cards){
			if( a.weight == val )return a;
		}
		return null;
	}
	public Card getCardById(int val){
		for( Card a  : this.cards){
			if( a.id == val )return a;
		}
		return null;
	}
	
	public Pixmap flipPixmap(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());
        Pixmap flipped2 = new Pixmap(width, height, src.getFormat());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel(x, height-y-1));
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped2.drawPixel(x, y, flipped.getPixel(width-x-1, y));
            }
        }
        return flipped2;
    }
	
}

