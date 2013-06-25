package com.knepe.megamemory.scenes;

import android.content.Intent;
import android.net.Uri;
import android.opengl.GLES20;
import android.widget.ViewFlipper;

import com.appflood.AppFlood;
import com.knepe.megamemory.R;
import com.knepe.megamemory.entities.Popup;
import com.knepe.megamemory.management.ResourceManager;
import com.knepe.megamemory.management.SceneManager;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

public class MainMenuScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {
    private final int TOP_PADDING = 50;
    protected static final int MENU_NEWGAME = 0;
    protected static final int MENU_QUIT = 1;
    protected static final int MENU_BUYFULLGAME = 2;
    protected static final int MENU_HIGHSCORE = 3;
    protected static final int MENU_MOREGAMES = 4;
    private AnimatedSprite soundToggleSprite;
    private MenuScene mMenuScene;

    @Override
    public void createScene() {
        SpriteBackground background = new SpriteBackground(new Sprite(0, 0, resourcesManager.main_background_region , vbom));
        background.setColorEnabled(false);
        setBackground(background);
        createMenuScene();
    }

    @Override
    public void onBackKeyPressed() {
        showExitConfirmation();
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_MENU;
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

    private void showExitConfirmation(){
        final Scene scene = SceneManager.getInstance().getCurrentScene();
        final float x = resourcesManager.activity.CAMERA_WIDTH / 2 - resourcesManager.popup_region.getWidth() / 2;
        final float y = resourcesManager.activity.CAMERA_HEIGHT / 2 - resourcesManager.popup_region.getHeight() / 2;
        Sprite popupBg = new Sprite(x, y, resourcesManager.popup_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                //to prevent "banding" on gradient
                pGLState.enableDither();
            }
        };
        AnimatedSprite yesButton = new AnimatedSprite(0, 0, resourcesManager.popup_button_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if(pSceneTouchEvent.isActionDown()){
                    this.setCurrentTileIndex(1);
                }
                else if(pSceneTouchEvent.isActionUp()){
                    this.setCurrentTileIndex(0);
                    System.exit(0);
                }
                else{
                    this.setCurrentTileIndex(0);
                }

                return true;
            };
        };
        AnimatedSprite noButton = new AnimatedSprite(0, 0, resourcesManager.popup_button_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if(pSceneTouchEvent.isActionDown()){
                    this.setCurrentTileIndex(1);
                }
                else if(pSceneTouchEvent.isActionUp()){
                    this.setCurrentTileIndex(0);
                    resourcesManager.engine.setScene(null);
                    SceneManager.getInstance().reloadMenuScene();
                }
                else{
                    this.setCurrentTileIndex(0);
                }

                return true;
            };
        };

        Text yesText = new Text(0,0, resourcesManager.main_font, activity.getString(R.string.str_yes), vbom);
        yesText.setPosition(((yesButton.getWidth() / 2) - yesText.getWidth() / 2), (yesButton.getHeight() / 2) - 20);

        Text noText = new Text(0,0, resourcesManager.main_font, activity.getString(R.string.str_no), vbom);
        noText.setPosition(((noButton.getWidth() / 2) - noText.getWidth() / 2), (noButton.getHeight() / 2) - 20);

        yesButton.attachChild(yesText);
        noButton.attachChild(noText);

        Popup popup = new Popup(x, y, scene, resourcesManager.main_font, popupBg, yesButton, noButton, null, vbom, null, null, false);
        popup.Add(activity.getString(R.string.str_quit_popup_header), activity.getString(R.string.str_quit_popup_text));
    }
    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        ((AnimatedSpriteMenuItem)pMenuItem).setCurrentTileIndex(1);
        if(resourcesManager.activity.sound_enabled){
            resourcesManager.click_sound.play();
        }
        switch(pMenuItem.getID()) {
            case MENU_NEWGAME:
                SceneManager.getInstance().loadThemeScene();
                return true;
            case MENU_QUIT:
                showExitConfirmation();
                return true;
            case MENU_BUYFULLGAME:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(activity.getString(R.string.full_game_link)));
                activity.startActivity(intent);
                return true;
            case MENU_HIGHSCORE:
                resourcesManager.activity.scoreloopActionResolver.showScoreloop();
                return true;
            case MENU_MOREGAMES:
                AppFlood.showPanel(activity, AppFlood.PANEL_PORTRAIT);
                return true;
            default:
                return false;
        }
    }

    protected void createMenuScene() {
        this.mMenuScene = new MenuScene(camera);

        createButtonWithText(activity.getString(R.string.menu_newgame), MENU_NEWGAME);
        createButtonWithText(activity.getString(R.string.menu_buyfullgame), MENU_BUYFULLGAME);
        createButtonWithText(activity.getString(R.string.menu_highscore), MENU_HIGHSCORE);
        createButtonWithText(activity.getString(R.string.menu_moregames), MENU_MOREGAMES);
        createButtonWithText(activity.getString(R.string.menu_quit), MENU_QUIT);
        createSoundToggleButton();
        createRateUsButton();

        this.mMenuScene.buildAnimations();
        this.mMenuScene.setBackgroundEnabled(false);
        this.mMenuScene.setY(TOP_PADDING);
        this.mMenuScene.setOnMenuItemClickListener(this);
        this.mMenuScene.clearUpdateHandlers();
        setChildScene(mMenuScene, false, false, false);
    }

    private void createRateUsButton(){
        Sprite rateUsSprite = new Sprite((camera.getWidth() / 2) - (resourcesManager.rate_us_region.getWidth() / 2), camera.getHeight() - 200, resourcesManager.rate_us_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if(pSceneTouchEvent.isActionUp()){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(activity.getString(R.string.free_game_link)));
                    activity.startActivity(intent);
                }

                return true;
            };
        };

        this.mMenuScene.registerTouchArea(rateUsSprite);
        this.mMenuScene.attachChild(rateUsSprite);

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
        btnText.setPosition(((button.getWidth() / 2) - btnText.getWidth() / 2), (button.getHeight() / 2) - 20);
        button.attachChild(btnText);
    }
}
