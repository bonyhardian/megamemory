package com.knepe.megamemory;

import com.badlogic.gdx.Game;
import com.knepe.megamemory.screens.SplashScreen;

public class MegaMemory extends Game {
	@Override
	public void create() {		
        setScreen(new SplashScreen(this));
	}
}
