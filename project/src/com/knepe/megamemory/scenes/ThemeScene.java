package com.knepe.megamemory.scenes;

import android.widget.Toast;

import com.knepe.megamemory.R;
import com.knepe.megamemory.management.SceneManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.modifier.ease.EaseBackOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by knepe on 2013-05-24.
 */
public class ThemeScene extends BaseScene implements ScrollScene.IOnScrollScenePageListener {
    private Sprite fingerSprite;
    private Text speechBubbleText;
    private AnimatedSprite ladyBugSprite;
    private Sprite speechBubbleSprite;
    protected ScrollScene mScene;
    private AnimatedSprite soundToggleSprite;
    private List<Integer> lockedThemes = Arrays.asList(3, 4, 5);

    @Override
    public void createScene() {
        createScrollScene();
    }

    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_MENU);
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_THEMES;
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
    public void onMoveToPageStarted(int pPageNumber) {
        if(speechBubbleSprite == null) return;
        if(speechBubbleSprite.getScaleX() < 1) return;
        speechBubbleSprite.registerEntityModifier(new ScaleModifier(0.2f, 1, 0));
    }

    @Override
    public void onMoveToPageFinished(int pPageNumber) {
        String txt = "";
        switch (pPageNumber){
            case 0:
                txt = activity.getString(R.string.theme_animals);
                break;
            case 1:
                txt = activity.getString(R.string.theme_fruits);
                break;
            case 2:
                txt = activity.getString(R.string.theme_shapes);
                break;
            case 3:
                txt = activity.getString(R.string.theme_vehicles);
                break;
            case 4:
                txt = activity.getString(R.string.theme_makeup);
                break;
            case 5:
                txt = activity.getString(R.string.theme_numbers);
                break;
        }

        changeSpeechBubbleText(txt);
    }

    private void createScrollScene(){
        this.mScene = new ScrollScene(resourcesManager.activity.CAMERA_WIDTH, resourcesManager.activity.CAMERA_HEIGHT);

        //the offset represents how much the layers overlap
        this.mScene.setOffset(((ITextureRegion)resourcesManager.themes_regions.get(0)).getWidth() / 2);
        this.mScene.setBackgroundEnabled(false);
        SpriteBackground background = new SpriteBackground(new Sprite(0, 0, resourcesManager.main_background_region , vbom));
        background.setColorEnabled(false);
        setBackground(background);

        CreateMenuBoxes();
        createSwipingFingerAnimation();
        createLadyBugAnimation(activity.getString(R.string.str_swipe));
        createSoundToggleButton();

        //change ease function test
        this.mScene.setEaseFunction(EaseBackOut.getInstance());

        this.mScene.registerScrollScenePageListener(this);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        setChildScene(this.mScene);
        sortChildren(true);
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

        this.registerTouchArea(soundToggleSprite);
        this.attachChild(soundToggleSprite);
    }

    private void createSpeechBubble(float x, float y, String text){
        speechBubbleSprite = new Sprite(0,0, resourcesManager.speech_bubble_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        speechBubbleSprite.setPosition(x, y);

        changeSpeechBubbleText(text);

        attachChild(speechBubbleSprite);
    }
    private void createLadyBugAnimation(final String speechBubbleText){
        ladyBugSprite = new AnimatedSprite(-150, (resourcesManager.activity.CAMERA_HEIGHT - resourcesManager.lady_bug_region.getHeight()) - 50, resourcesManager.lady_bug_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };
        ladyBugSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        ladyBugSprite.animate(300);

        final MoveXModifier rightMoveModifier = new MoveXModifier(5f, -150, 30);

        SequenceEntityModifier sequenceEntityModifier = new SequenceEntityModifier(rightMoveModifier) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                try{
                    AnimatedSprite sprite = (AnimatedSprite)pItem;

                    sprite.animate(new long[] { 300,4000}, 8, 9, true);

                    createSpeechBubble(sprite.getX() + (sprite.getWidth() - 20), sprite.getY() - (sprite.getHeight() - 40), speechBubbleText);
                }
                catch(Exception e)
                {

                }
            }
        };

        sequenceEntityModifier.setAutoUnregisterWhenFinished(true);
        ladyBugSprite.registerEntityModifier(sequenceEntityModifier);

        attachChild(ladyBugSprite);

    }
    private void createSwipingFingerAnimation(){
        fingerSprite = new Sprite(resourcesManager.activity.CAMERA_WIDTH - resourcesManager.finger_region.getWidth(), resourcesManager.activity.CAMERA_HEIGHT - 200, resourcesManager.finger_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };
        fingerSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        fingerSprite.setAlpha(0);

        final float minX = resourcesManager.activity.CAMERA_WIDTH - fingerSprite.getWidth();
        final float maxX = (resourcesManager.activity.CAMERA_WIDTH / 2) + 50;

        final float duration = 2f;

        final AlphaModifier alphaModifierHide = new AlphaModifier(0.3f, 1, 0);
        final AlphaModifier alphaModifierShow = new AlphaModifier(0.3f, 0, 1);
        final MoveXModifier leftMoveModifier = new MoveXModifier(duration, minX, maxX);

        SequenceEntityModifier sequenceEntityModifier = new SequenceEntityModifier(alphaModifierShow, leftMoveModifier, alphaModifierHide) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                pItem.setX(minX);
                super.reset();
            }
        };

        sequenceEntityModifier.setAutoUnregisterWhenFinished(false);
        fingerSprite.registerEntityModifier(sequenceEntityModifier);

        attachChild(fingerSprite);
    }

    private void stopFingerAnimation(){
        fingerSprite.clearEntityModifiers();
        fingerSprite.detachSelf();
    }

    private void CreateMenuBoxes() {
        //current item counter
        int iItem = 0;

        for (int x = 0; x < resourcesManager.themes_regions.size(); x++) {

            //On Touch, save the clicked item in case it's a click and not a scroll.
            final int itemToLoad = iItem;
            final ITextureRegion textureRegion = (ITextureRegion)resourcesManager.themes_regions.get(x);

             /* Calculate the coordinates for the face, so its centered on the camera. */
            final float centerX = (this.mScene.getPageWidth() - textureRegion.getWidth()) / 2;
            final float centerY = (this.mScene.getPageHeight() - textureRegion.getHeight()) / 2;

            final Sprite sprite = new Sprite(centerX, centerY, textureRegion, vbom){
                @Override
                protected void preDraw(GLState pGLState, Camera pCamera) {
                    super.preDraw(pGLState, pCamera);
                    pGLState.enableDither();
                }
                @Override
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    if(pSceneTouchEvent.isActionUp() && mScene.mState == ScrollScene.ScrollState.IDLE){
                        loadLevel(itemToLoad);
                    }
                    else{
                        mScene.onSceneTouchEvent(mScene, pSceneTouchEvent);
                        stopFingerAnimation();
                    }

                    return true;
                }
            };

            iItem++;

            Rectangle page = new Rectangle(0, 0, 0, 0, vbom);
            page.attachChild(sprite);

            this.mScene.registerTouchArea(sprite);

            this.mScene.addPage(page);
        }
    }

    private void loadLevel(final int iLevel) {
        if (iLevel != -1) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (resourcesManager.activity.sound_enabled) {
                        resourcesManager.click_sound.play();
                    }

                    if (lockedThemes.contains(iLevel)) {
                        Toast.makeText(activity, activity.getString(R.string.str_buygamemessage), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resourcesManager.activity.THEME = iLevel;
                    SceneManager.getInstance().loadDifficultyScene();
                }
            });
        }
    }

    private void changeSpeechBubbleText(String text){
        if(speechBubbleSprite == null) return;

        if(speechBubbleText == null || speechBubbleText.getText() == null){
            speechBubbleText = new Text(20, 35, resourcesManager.maint_font_gray, text, 100, vbom);
            speechBubbleSprite.attachChild(speechBubbleText);

            speechBubbleSprite.setScale(1);
            speechBubbleSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.15f, 1, 1.1f), new ScaleModifier(0.1f, 1.1f, 0.9f), new ScaleModifier(0.1f, 0.9f, 1)));
        }
        else{
            if(speechBubbleText.getText() != text){
                speechBubbleSprite.setScale(1);
                speechBubbleSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.15f, 1, 1.1f), new ScaleModifier(0.1f, 1.1f, 0.9f), new ScaleModifier(0.1f, 0.9f, 1)));
                speechBubbleText.setText(text);
            }
        }
    }
}
