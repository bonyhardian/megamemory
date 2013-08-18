package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.models.MenuFactory;

/**
 * Created by knepe on 2013-08-19.
 */
public class DifficultyMenu extends Table {
    public DifficultyMenu(final MenuFactory menuFactory){
        super();

        Skin skin = new Skin(Gdx.files.internal( "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton easy = new TextButton("Easy", skin);
        easy.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.setMenu(1);
            }
        });

        TextButton normal = new TextButton("Normal", skin);
        normal.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });

        TextButton hard = new TextButton("Hard", skin);
        hard.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {

            }
        });

        add(easy);
        row();
        add(normal);
        row();
        add(hard);

    }
}
