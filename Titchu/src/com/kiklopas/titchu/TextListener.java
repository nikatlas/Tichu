package com.kiklopas.titchu;

import com.badlogic.gdx.Input.TextInputListener;

public class TextListener implements TextInputListener {
	String text;
	@Override
	public void input(String text) {
		// TODO Auto-generated method stub
		this.text = text;
	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub
		text = "";
	}

}
