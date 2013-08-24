package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.MenuFactory;
import com.knepe.megamemory.screens.GameScreen;

public class DifficultyMenu extends Table {
    private MegaMemory game;
    private MenuFactory menuFactory;

    public DifficultyMenu(final MenuFactory menuFactory, final MegaMemory game){
        super();

        this.menuFactory = menuFactory;
        this.game = game;

        Skin skin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton easy = new TextButton("Easy", skin);
        easy.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setDifficultyPref(0);
            }
        });

        TextButton normal = new TextButton("Normal", skin);
        normal.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setDifficultyPref(1);
            }
        });

        TextButton hard = new TextButton("Hard", skin);
        hard.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setDifficultyPref(2);
            }
        });

        add(easy);
        row();
        add(normal);
        row();
        add(hard);

    }

    private void setDifficultyPref(int difficulty){
        game.setDifficulty(difficulty);
        game.setScreen(new GameScreen(game));
    }
}
