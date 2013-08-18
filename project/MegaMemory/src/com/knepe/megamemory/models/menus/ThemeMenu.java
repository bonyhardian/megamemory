package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.models.MenuFactory;

/**
 * Created by knepe on 2013-08-18.
 */
public class ThemeMenu extends Table {
    public ThemeMenu(final MenuFactory menuFactory){
        super();

        Skin skin = new Skin(Gdx.files.internal( "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton animals = new TextButton("Animals", skin);
        animals.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.setMenu(2);
            }
        });

        TextButton fruits = new TextButton("Fruits", skin);
        fruits.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.setMenu(0);
            }
        });

        TextButton shapes = new TextButton("Shapes", skin);
        shapes.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });

        TextButton vehicles = new TextButton("Vehicles", skin);
        vehicles.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });
        TextButton makeup = new TextButton("Makeup", skin);
        makeup.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });
        TextButton numbers = new TextButton("Numbers", skin);
        numbers.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });


        add(animals);
        add(fruits);
        row();
        add(shapes);
        add(vehicles);
        row();
        add(makeup);
        add(numbers);
    }
}
