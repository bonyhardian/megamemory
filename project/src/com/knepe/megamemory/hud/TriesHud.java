package com.knepe.megamemory.hud;

public class TriesHud {
	private int tries;
	
	public TriesHud(){
		tries = 0;
	}
	
	public void increase(){
		tries++;
	}
	
	public void reset(){
		tries = 0;
	}
	
	public int getTriesNumber(){
		return tries;
	}
	
	public String getTries(){
		return "" + tries;
	}
}
