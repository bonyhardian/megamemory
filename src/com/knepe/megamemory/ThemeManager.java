package com.knepe.megamemory;

public class ThemeManager {
	
	public static String getThemeString(int id){
		switch(id){
			case 0:
				return "animals";
			case 1:
				return "fruits";
			case 2:
				return "shapes";
			case 3:
				return "vehicles";
			case 4:
				return "makeup";
			case 5:
				return "numbers";
			default:
				return "";
		}
	}
}
