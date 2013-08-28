package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.MenuFactory;
import com.knepe.megamemory.models.helpers.SoundHelper;

public class ThemeMenu extends Table {
    private MegaMemory game;
    private MenuFactory menuFactory;

    public ThemeMenu(final MenuFactory menuFactory, final MegaMemory game){
        super();

        this.menuFactory = menuFactory;
        this.game = game;

        Skin skin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton animals = new TextButton("Animals", skin);
        animals.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setThemePref(1);
            }
        });

        TextButton fruits = new TextButton("Fruits", skin);
        fruits.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setThemePref(2);
            }
        });

        TextButton shapes = new TextButton("Shapes", skin);
        shapes.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setThemePref(3);
            }
        });

        TextButton vehicles = new TextButton("Vehicles", skin);
        vehicles.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setThemePref(4);
            }
        });
        TextButton numbers = new TextButton("Numbers", skin);
        numbers.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setThemePref(5);
            }
        });


        add(animals);
        add(fruits);
        row();
        add(shapes);
        add(vehicles);
        row();
        add(numbers);
    }

    private void setThemePref(int id){
        game.soundHelper.playSound(SoundHelper.SoundType.CLICK);
        game.THEME = id;
        menuFactory.setMenu(2);
    }
}
