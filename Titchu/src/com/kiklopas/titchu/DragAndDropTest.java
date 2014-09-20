package com.kiklopas.titchu;

import java.util.Vector;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;


public class DragAndDropTest  {
	private static final int START = 34;
	Stage stage;
	public DragAndDrop dragAndDrop;
	
	public void create (Stage st, Vector<CardActor> trades) {
		stage = st;

		Card card = new Card("back.png");
		CardActor temp = new CardActor(card);
		temp.setBounds(START, 380, 133, 200);
		temp.isFull = true;
		trades.add(temp);
		stage.addActor(temp);
		temp = new CardActor(card);
		temp.setBounds(START + 140, 380, 133, 200);
		temp.isFull = true;
		trades.add(temp);
		stage.addActor(temp);
		temp = new CardActor(card);
		temp.setBounds(START + 280, 380, 133, 200);
		temp.isFull = true;
		trades.add(temp);
		stage.addActor(temp);
		
		for( CardActor actor : trades ){
			actor.setVisible(false);
			actor.setZIndex(1000);
		}
		dragAndDrop = new DragAndDrop();
		
	}
}