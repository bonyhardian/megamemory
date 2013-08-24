package com.knepe.megamemory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.knepe.megamemory.screens.SplashScreen;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "MegaMemory";
		cfg.useGL20 = false;
		cfg.width = 1200;
		cfg.height = 720;
		final MegaMemory game = new MegaMemory(cfg.width, cfg.height, new DesktopGooglePlayInterface());
		new LwjglApplication(game, cfg);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new SplashScreen(game));
            }
        });
	}
}
