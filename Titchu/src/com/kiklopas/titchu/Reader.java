package com.kiklopas.titchu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import com.badlogic.gdx.Gdx;

public class Reader {
	String fileName = null;
	public Vector<String> lines; 
	
	Reader( String file ){
		fileName = file;
		lines = new Vector<String>();
		System.out.print(file);
		
		read();
	}
	private void read() {
			
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(Gdx.files.internal(fileName).reader()); 
			//reader = new BufferedReader(new FileReader(filename));// FOR DEBUG
			String line = null;
			while ((line = reader.readLine()) != null) {
			    lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
