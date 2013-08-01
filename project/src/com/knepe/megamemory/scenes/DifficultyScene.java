package com.knepe.megamemory.scenes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.widget.Toast;
import com.knepe.megamemory.GameActivity;
import com.knepe.megamemory.R;
import com.knepe.megamemory.management.SceneManager;
import org.andengine.engine.camera.Camera;
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

/**
 * Created by knepe on 2013-05-24.
 */
public class DifficultyScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {
    private MenuScene mMenuScene;
    protected static final int MENU_2x2 = 0;
    protected static final int MENU_4x4 = 1;
    protected static final int MENU_6x6 = 2;
    private AnimatedSprite soundToggleSprite;
    @Override
    public void createScene() {
        SpriteBackground background = new SpriteBackground(new Sprite(0, 0, resourcesManager.main_background_region , vbom));
        background.setColorEnabled(false);
        setBackground(background);
        createMenuScene();
    }

    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_THEMES);
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_DIFFICULTY;
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

    protected void createMenuScene() {
        this.mMenuScene = new MenuScene(camera);

        createButtonWithText(activity.getString(R.string.difficulty_2x2), MENU_2x2);
        createButtonWithText(activity.getString(R.string.difficulty_4x4), MENU_4x4);
        createButtonWithText(activity.getString(R.string.difficulty_6x6), MENU_6x6);
        createSoundToggleButton();

        this.mMenuScene.buildAnimations();
        this.mMenuScene.setBackgroundEnabled(false);
        this.mMenuScene.setOnMenuItemClickListener(this);
        this.mMenuScene.clearUpdateHandlers();
        setChildScene(mMenuScene, false, false, false);
    }

    private void createSoundToggleButton(){
        soundToggleSprite = new AnimatedSprite(camera.getWidth() - (resourcesManager.sound_toggle_region.getWidth() + 5), 5, resourcesManager.sound_toggle_region, vbom){
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

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        ((AnimatedSpriteMenuItem)pMenuItem).setCurrentTileIndex(1);

        if(resourcesManager.activity.sound_enabled){
            resourcesManager.click_sound.play();
        }

        if(pMenuItem.getID() == MENU_6x6){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, activity.getString(R.string.str_unlockfeaturemessage), Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        resourcesManager.activity.DIFFICULTY = pMenuItem.getID();
        SceneManager.getInstance().loadGameScene(resourcesManager.engine);
        return true;
    }
}
