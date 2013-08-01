package com.knepe.megamemory.management;

public class BackgroundManager {
	public static String getBackground(int id){
		switch(id){
			case 0:
				return "animals-bg1.jpg";
			case 1:
				return "animals-bg2.jpg";
			case 2: 
				return "fish-bg.jpg";
			default:
				return "default.jpg";
		}
	}
}
