package com.knepe.megamemory;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.knepe.megamemory.models.googleplay.GooglePlayInterface;
import com.knepe.megamemory.models.accessors.ActorTweenAccessor;
import com.knepe.megamemory.models.accessors.ImageTweenAccessor;
import com.knepe.megamemory.models.accessors.SpriteTweenAccessor;
import com.knepe.megamemory.models.accessors.TableTweenAccessor;
import com.knepe.megamemory.models.helpers.SoundHelper;
import com.knepe.megamemory.screens.GameScreen;
import com.knepe.megamemory.screens.SplashScreen;

import aurelienribon.tweenengine.Tween;
import sun.reflect.annotation.ExceptionProxy;

public class MegaMemory extends Game {
    public int height;
    public int width;
    public GooglePlayInterface googlePlayInterface;
    private boolean hd;
    public int THEME = -1;
    public int DIFFICULTY = 0;
    public int NUM_ROWS = 4;
    public int NUM_COLS = 4;
    public String assetBasePath = "";
    public MultiplayerMode multiplayerMode = MultiplayerMode.NONE;
    private boolean soundEnabled = true;
    public SoundHelper soundHelper;

    public enum MultiplayerMode{
        NONE, RANDOM, INVITE
    }

    public MegaMemory(int width, int height, GooglePlayInterface googlePlayInterface){
        this.height = height;
        this.width = width;
        this.googlePlayInterface = googlePlayInterface;
        this.hd = (height >= 720);
        this.assetBasePath = hd ? "hd/" : "sd/";
    }

    @Override
    public void dispose() {
        super.dispose();

        soundHelper.dispose();
    }

    @Override
	public void create() {
        Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());
        Tween.registerAccessor(Image.class, new ImageTweenAccessor());
        Tween.registerAccessor(Table.class, new TableTweenAccessor());
        Tween.registerAccessor(Actor.class, new ActorTweenAccessor());
        Tween.setWaypointsLimit(8);
        Gdx.input.setCatchBackKey(true);
        this.soundHelper = new SoundHelper(this);
	}

    @Override
    public void resize(int width, int height) {
        //super.resize(width, height);
    }

    public void setDifficulty(int difficulty){
        switch(difficulty){
            case 0:
                NUM_COLS = 2;
                NUM_ROWS = 2;
                break;
            case 1:
                NUM_COLS = 4;
                NUM_ROWS = 4;
                break;
            case 2:
                NUM_COLS = 6;
                NUM_ROWS = 6;
                break;
        }

        DIFFICULTY = difficulty;
    }

    public boolean getSoundEnabled(){
        return this.soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled){
        this.soundEnabled = soundEnabled;
    }
}
