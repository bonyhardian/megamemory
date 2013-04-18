package com.knepe.megamemory;

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
	
	public String getTries(){
		return "" + tries;
	}
}
