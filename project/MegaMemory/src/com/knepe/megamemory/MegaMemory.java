package com.knepe.megamemory;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.knepe.megamemory.models.accessors.ImageTweenAccessor;
import com.knepe.megamemory.models.accessors.SpriteTweenAccessor;
import com.knepe.megamemory.models.accessors.TableTweenAccessor;
import com.knepe.megamemory.screens.SplashScreen;

import aurelienribon.tweenengine.Tween;

public class MegaMemory extends Game {
    public int height;
    public int width;

    public MegaMemory(int width, int height){
        this.height = height;
        this.width = width;
    }

    @Override
	public void create() {
        Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());
        Tween.registerAccessor(Image.class, new ImageTweenAccessor());
        Tween.registerAccessor(Table.class, new TableTweenAccessor());
        Tween.setWaypointsLimit(8);

        setScreen(new SplashScreen(this));
	}
}
