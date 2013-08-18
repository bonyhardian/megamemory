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
public class MainMenu extends Table {
    public MainMenu(final MenuFactory menuFactory){
        super();

        Skin skin = new Skin(Gdx.files.internal( "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton startButton = new TextButton("Singleplayer", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.setMenu(1);
            }
        });

        TextButton signinButton = new TextButton("Sign in", skin);
        signinButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                //sign in to google games
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        add(startButton);
        row();
        add(signinButton);
        row();
        add(quitButton);
    }
}
