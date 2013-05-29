package com.knepe.megamemory.management;

public class DifficultyManager {
	public static String getLayoutString(int id){
		switch(id){
			case 0:
				return "2x2";
			case 1:
				return "4x4";
			case 2:
				return "6x6";
			default:
				return "";
		}
	}
	
	public static int getLayoutStartX(int id){
		switch(id){
		case 0:
			return 125;
		case 1:
			return 10;
		case 2:
			return 35;
		default:
			return 0;
	}
	}
}
