package com.knepe.megamemory.models;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.accessors.SpriteTweenAccessor;
import com.knepe.megamemory.models.menus.DifficultyMenu;
import com.knepe.megamemory.models.menus.MainMenu;
import com.knepe.megamemory.models.menus.ThemeMenu;

import java.util.HashMap;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quart;

/**
 * Created by knepe on 2013-08-18.
 */
public class MenuFactory {
    public HashMap<Integer, Table> menus = new HashMap<Integer, Table>();
    public Table currentMenu = null;
    private int currentId = 0;
    private final MegaMemory game;
    private final Stage stage;
    private final TweenManager tweenManager;

    public MenuFactory(final Stage stage, final MegaMemory game, final TweenManager tweenManager){
        this.stage = stage;
        this.game = game;
        this.tweenManager = tweenManager;
        this.menus = getMenus();
    }

    public void setMenu(final int id){
        if(currentMenu != null){
            final float currentMenuX = currentMenu.getX();
            Tween.to(currentMenu, SpriteTweenAccessor.POS_XY, 0.5f).target(-currentMenu.getWidth(), currentMenu.getY()).ease(Quart.IN)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            final Table newMenu = menus.get(id);
                            float originX = newMenu.getX();
                            newMenu.setX(Gdx.graphics.getWidth() + newMenu.getWidth());
                            stage.addActor(newMenu);
                            Tween.to(newMenu, SpriteTweenAccessor.POS_XY, 0.5f).target(originX, newMenu.getY()).ease(Quart.OUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int type, BaseTween<?> source) {
                                            currentMenu.remove();
                                            currentMenu = newMenu;
                                            menus.get(currentId).setX(currentMenuX);
                                            currentId = id;
                                        }
                                    })
                                    .start(tweenManager);
                        }
                    })
                    .start(tweenManager);

        }
        else{
            currentMenu = menus.get(id);
            stage.addActor(currentMenu);
            currentId = id;
        }
    }

    public void back(){
        if(currentId == 0)
            Gdx.app.exit();
        else{
            Log.d("MM", "currentid = " + currentId);
            setMenu((currentId - 1));
        }

    }

    private HashMap<Integer, Table> getMenus(){
        HashMap<Integer, Table> hashMap = new HashMap<Integer, Table>();
        hashMap.put(0, new MainMenu(this));
        hashMap.put(1, new ThemeMenu(this));
        hashMap.put(2, new DifficultyMenu(this));

        return hashMap;
    }
}
