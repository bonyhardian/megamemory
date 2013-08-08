package com.knepe.megamemory.management;

import android.content.Intent;
import android.net.Uri;
import android.opengl.GLES20;

import com.appflood.AppFlood;
import com.knepe.megamemory.GameActivity;
import com.knepe.megamemory.R;
import com.knepe.megamemory.scenes.BaseScene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

/**
 * Created by Tobias on 2013-08-08.
 */
public class SelectOpponentScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {

    private final int TOP_PADDING = 50;

    protected static final int MENU_RANDOM = 0;
    protected static final int MENU_INVITE = 1;

    private AnimatedSprite soundToggleSprite;
    private MenuScene mMenuScene;

    protected void createMenuScene() {
        this.mMenuScene = new MenuScene(camera);

        createButtonWithText(activity.getString(R.string.menu_selectplayer_random), MENU_RANDOM);
        createButtonWithText(activity.getString(R.string.menu_selectplayer_invite), MENU_INVITE);

        createSoundToggleButton();

        this.mMenuScene.buildAnimations();
        this.mMenuScene.setBackgroundEnabled(false);
        this.mMenuScene.setY(TOP_PADDING);
        this.mMenuScene.setOnMenuItemClickListener(this);
        this.mMenuScene.clearUpdateHandlers();
        setChildScene(mMenuScene, false, false, false);
    }

    private void createButtonWithText(String text, int pId){
        final AnimatedSpriteMenuItem button = new AnimatedSpriteMenuItem(pId, resourcesManager.menubutton_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
            @Override
            public void onSelected()
            {
                setCurrentTileIndex(1);
                super.onSelected();
            }

            @Override
            public void onUnselected()
            {
                setCurrentTileIndex(0);
                super.onUnselected();
            }
        };

        button.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        this.mMenuScene.addMenuItem(button);

        Text btnText = new Text(0,0, resourcesManager.main_font, text, vbom);
        btnText.setPosition(((button.getWidth() / 2) - btnText.getWidth() / 2), (button.getHeight() / 2) - 10);
        button.attachChild(btnText);
    }

    private void createSoundToggleButton(){
        soundToggleSprite = new AnimatedSprite(camera.getWidth() - (resourcesManager.sound_toggle_region.getWidth() + 5), (5 - TOP_PADDING), resourcesManager.sound_toggle_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if(pSceneTouchEvent.isActionUp()){
                    resourcesManager.activity.sound_enabled = !resourcesManager.activity.sound_enabled;

                    resourcesManager.activity.setSoundPreference();

                    if(resourcesManager.activity.sound_enabled){
                        this.setCurrentTileIndex(0);
                    }
                    else{
                        this.setCurrentTileIndex(1);
                    }
                }

                return true;
            };
        };

        if(resourcesManager.activity.sound_enabled){
            soundToggleSprite.setCurrentTileIndex(0);
        }
        else{
            soundToggleSprite.setCurrentTileIndex(1);
        }
        this.mMenuScene.registerTouchArea(soundToggleSprite);
        this.mMenuScene.attachChild(soundToggleSprite);
    }

    @Override
    public void createScene() {
        SpriteBackground background = new SpriteBackground(new Sprite(0, 0, resourcesManager.main_background_region , vbom));
        background.setColorEnabled(false);
        setBackground(background);
        createMenuScene();
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_SELECTOPPONENT;
    }

    @Override
    public void disposeScene() {

    }

    @Override
    public void refreshSoundToggleButton() {
        if(soundToggleSprite == null) return;

        if(resourcesManager.activity.sound_enabled){
            soundToggleSprite.setCurrentTileIndex(0);
        }
        else{
            soundToggleSprite.setCurrentTileIndex(1);
        }
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        ((AnimatedSpriteMenuItem)pMenuItem).setCurrentTileIndex(1);
        if(resourcesManager.activity.sound_enabled){
            resourcesManager.click_sound.play();
        }
        switch(pMenuItem.getID()) {
            case MENU_RANDOM:
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.startQuickGame();
                    }});
                return true;
            case MENU_INVITE:
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.startActivityForResult(activity.getGamesClient().getSelectPlayersIntent(1, 1), activity.RC_SELECT_PLAYERS);
                    }
                });
                SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_LOADING);
                return true;
            default:
                return false;
        }
    }
}
