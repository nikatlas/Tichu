package com.kiklopas.titchu;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

public class Main {
	private static ActivityManager activity;
	private static LwjglApplication app;

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Titchu";
		cfg.useGL20 = false;
		cfg.width = 240;
		cfg.height = 320;
		
		Texture.setEnforcePotImages(false);
		activity = new ActivityManager();
		app = new LwjglApplication(activity, cfg);
		//new LwjglApplication(new Tichu(), cfg);
	}
	
}
