package com.knepe.megamemory;

import com.badlogic.gdx.Game;
import com.knepe.megamemory.screens.SplashScreen;

public class MegaMemory extends Game {
    public int height;
    public int width;

    public MegaMemory(int width, int height){
        this.height = height;
        this.width = width;
    }

    @Override
	public void create() {		
        setScreen(new SplashScreen(this));
	}
}
