package com.knepe.megamemory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "MegaMemory";
		cfg.useGL20 = false;
		cfg.width = 1200;
		cfg.height = 720;
		
		new LwjglApplication(new MegaMemory(cfg.width, cfg.height), cfg);
	}
}
