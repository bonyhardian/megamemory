package com.knepe.megamemory.models.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.MenuFactory;
import com.knepe.megamemory.models.helpers.SoundHelper;

public class MultiplayerMenu extends Table {
    private MegaMemory game;
    private MenuFactory menuFactory;

    public MultiplayerMenu(final MenuFactory menuFactory, final MegaMemory game){
        super();

        this.menuFactory = menuFactory;
        this.game = game;

        Skin skin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/uiskin.json" ));

        setFillParent(true);
        TextButton inviteFriend = new TextButton("Invite friend", skin);
        inviteFriend.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setMultiplayerMode(MegaMemory.MultiplayerMode.INVITE);
            }
        });

        TextButton random = new TextButton("Random", skin);
        random.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
               setMultiplayerMode(MegaMemory.MultiplayerMode.RANDOM);
            }
        });

        add(inviteFriend);
        add(random);
    }

    private void setMultiplayerMode(MegaMemory.MultiplayerMode multiplayerMode){
        game.soundHelper.playSound(SoundHelper.SoundType.CLICK);
        game.multiplayerMode = multiplayerMode;
        menuFactory.setMenu(1);
    }
}