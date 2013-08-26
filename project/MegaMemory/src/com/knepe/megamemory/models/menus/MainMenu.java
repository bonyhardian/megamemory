package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.MenuFactory;

public class MainMenu extends Table {
    public MainMenu(final MenuFactory menuFactory){
        super();

        Skin skin = new Skin(Gdx.files.internal(menuFactory.game.assetBasePath + "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton startButton = new TextButton("Singleplayer", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.game.multiplayerMode = MegaMemory.MultiplayerMode.NONE;
                menuFactory.setMenu(1);
            }
        });

        TextButton signinButton = new TextButton("Sign in", skin);
        signinButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.game.googlePlayInterface.login();
            }
        });

        //multiplayer stuff
        TextButton leaderBoardButton = new TextButton("Leaderboard", skin);
        leaderBoardButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.game.googlePlayInterface.showLeaderboard();
            }
        });
        TextButton achievementsButton = new TextButton("Achievements", skin);
        achievementsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.game.googlePlayInterface.showAchievements();
            }
        });
        TextButton multiPlayerButton = new TextButton("Multiplayer", skin);
        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.setMenu(3);
            }
        });
        TextButton signOutButton = new TextButton("Sign out", skin);
        signOutButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                menuFactory.game.googlePlayInterface.logout();
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        if(!menuFactory.game.googlePlayInterface.getSignedIn())
        {
            add(startButton);
            row();
            add(signinButton);
            row();
            add(quitButton);
        }
        else{
            add(startButton);
            add(multiPlayerButton);
            row();
            add(leaderBoardButton);
            add(achievementsButton);
            row();
            add(signOutButton);
            add(quitButton);
        }
    }
}
