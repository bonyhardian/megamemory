package com.knepe.megamemory.scenes;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.knepe.megamemory.R;
import com.knepe.megamemory.entities.Card;
import com.knepe.megamemory.entities.Popup;
import com.knepe.megamemory.hud.TimerHud;
import com.knepe.megamemory.hud.TriesHud;
import com.knepe.megamemory.management.DifficultyManager;
import com.knepe.megamemory.management.SceneManager;
import com.scoreloop.client.android.core.model.TermsOfService;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;

import java.util.*;

/**
 * Created by knepe on 2013-05-24.
 */
public class GameScene extends BaseScene {
    private static Object lock = new Object();
    private static int mScore = 0;
    public static Integer SelectedId_first = -1;
    public static Integer SelectedId_second = -1;
    private static Boolean mc_isfirst = false;
    public static ArrayList<Card> cards;
    public SparseArray<ITiledTextureRegion> cardMappings = new SparseArray<ITiledTextureRegion>();
    private TimerHud timer;
    private static TriesHud triesHud;
    private IUpdateHandler timerUpdateHandler;


    @Override
    public void createScene() {
        initScene();
    }

    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().loadMenuScene(resourcesManager.engine);
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene() {

    }

    @Override
    public void refreshSoundToggleButton() {
        return;
    }

    private void initScene(){
        setBackground(new SpriteBackground(new Sprite(0, 0, resourcesManager.game_background_region, vbom)));

        registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (checkIfFinished()) {
                    final IUpdateHandler that = this;
                    resourcesManager.activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(that);
                            finished();
                        }
                    });

                }
            }
        });

        gameStart();
    }

    private void gameStart(){
        SelectedId_first = -1;
        SelectedId_second = -1;
        mc_isfirst = false;
        cards = new ArrayList<Card>();
        cardMappings = new SparseArray<ITiledTextureRegion>();
        timer = new TimerHud();
        triesHud = new TriesHud();
        timerUpdateHandler = null;
        mScore = 0;

        mapCards();
        fillCards();
        addCardsToScene();
        initTimer();
    }

    private void initTimer(){
        int hud_y = (int) (resourcesManager.activity.CAMERA_HEIGHT - (resourcesManager.score_hud_region.getHeight() + 50));
        attachChild(new Sprite(40, hud_y, resourcesManager.score_hud_region, vbom){
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        });

        final Text triesText = new Text(95, (hud_y + 8), resourcesManager.game_font, "TRIES: ", 7, vbom);
        final Text triesCounterText = new Text(180, (hud_y + 8), resourcesManager.game_font, "0", 300, vbom);
        attachChild(triesCounterText);
        attachChild(triesText);

        final Text elapsedText = new Text(335, (hud_y + 8), resourcesManager.game_font, "0:00", 300, vbom);
        final Text timeText = new Text(260, (hud_y + 8), resourcesManager.game_font, "TIME: ", 6, vbom);
        attachChild(elapsedText);
        attachChild(timeText);


        timer = new TimerHud();
        triesHud = new TriesHud();
        timer.start();

        timerUpdateHandler = new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                elapsedText.setText(timer.getTime());
                triesCounterText.setText(triesHud.getTries());
            }
        });

        registerUpdateHandler(timerUpdateHandler);
    }

    private void finished(){
        timer.stop();
        unregisterUpdateHandler(timerUpdateHandler);
        showFinishPopup();
    }

    private boolean checkIfFinished(){
        if(cards == null || cards.isEmpty())
        {
            return false;
        }

        for(Card card : cards){
            if(card.isVisible())
                return false;
        }

        return true;
    }

    private void showFinishPopup(){
        if(resourcesManager.activity.sound_enabled){
            resourcesManager.finished_sound.play();
        }

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
        AnimatedSprite homeButton = new AnimatedSprite(0, 0, resourcesManager.home_icon_region, vbom){
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
                    SceneManager.getInstance().loadMenuScene(resourcesManager.engine);
                }
                else{
                    this.setCurrentTileIndex(0);
                }

                return true;
            };
        };
        AnimatedSprite retryButton = new AnimatedSprite(0, 0, resourcesManager.retry_icon_region, vbom){
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
                    GameScene.this.reload();
                }
                else{
                    this.setCurrentTileIndex(0);
                }

                return true;
            };
        };

            AnimatedSprite submitScoreButton = new AnimatedSprite(0, 0, resourcesManager.popup_button_region, vbom){
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
                        resourcesManager.activity.scoreloopActionResolver.submitScore(0, Integer.parseInt(getScore()));

                    }
                    else{
                        this.setCurrentTileIndex(0);
                    }

                    return true;
                };
            };

            Text submitText = new Text(0,0, resourcesManager.game_font_small, "Submit score", vbom);
            submitText.setPosition(((submitScoreButton.getWidth() / 2) - submitText.getWidth() / 2), (submitScoreButton.getHeight() / 2) - 20);
            submitScoreButton.attachChild(submitText);

        Popup popup = new Popup(x, y, scene, resourcesManager.game_font, popupBg, homeButton, retryButton, submitScoreButton, vbom, resourcesManager.particle_region, resourcesManager.fireworks_sound, resourcesManager.activity.sound_enabled);
        popup.Add("SCORE", "Time: " + timer.getStopTime(activity), "Tries: " + triesHud.getTries(), "Score: " + getScore());
    }

    private String getScore(){
        double time = timer.getStopSeconds();
        double tries = triesHud.getTriesNumber();

        int score = (int) Math.round(mScore - (time + tries));

        return "" + score;
    }

    private void reload(){
        final BaseScene scene = SceneManager.getInstance().getCurrentScene();
        resourcesManager.engine.runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                scene.detachChildren();
                scene.reset();

                initScene();

                resourcesManager.engine.setScene(null);
                SceneManager.getInstance().setScene(new GameScene());

                timer.start();
            }
        });
    }

    public void executeCardCalculation(Card card){
        if(resourcesManager.activity.sound_enabled){
            resourcesManager.turn_card_sound.play();
        }

        triesHud.increase();
        //disable all cards
        disableCards();

        mc_isfirst = !mc_isfirst;

        int id = cards.indexOf(card);

        if (mc_isfirst) {
            SelectedId_first = id;

            enableCards();

        } else {
            SelectedId_second = id;

            playMove();
        }
    }


    private void checkCards(){
        if(cards.get(SelectedId_first).Match(cards.get(SelectedId_second))){
            //correct
            if(cards.get(SelectedId_first).IsTurned() && cards.get(SelectedId_second).IsTurned()){

                if(resourcesManager.activity.sound_enabled){
                    resourcesManager.correct_sound.play();
                }

                cards.get(SelectedId_first).hide();
                cards.get(SelectedId_second).hide();
                mScore += 30 * (resourcesManager.activity.DIFFICULTY + 1);
                enableCards();

                return;
            }
        }

        //incorrect
        if(resourcesManager.activity.sound_enabled){
            resourcesManager.turn_card_sound.play();
        }

        for(int y=0;y < cards.size(); y++){
            cards.get(y).enable();
            if(cards.get(y).IsTurned()){
                cards.get(y).showBack();
            }
        }
    }

    private static void disableCards(){
        for(Card c : cards){
            c.disable();
        }
    }

    private static void enableCards(){
        for(Card c : cards){
            c.enable();
        }
    }
    private void playMove(){
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try{
                    synchronized (lock) {
                        checkCards();
                    }
                }
                catch (Exception e) {
                    Log.e("E1", e.getMessage());
                }
            }
        };

        Timer t = new Timer(false);
        t.schedule(tt, 1200);
    }

    private void addCardsToScene(){
        int rowCount = 0;
        int colCount = 0;
        int startX = DifficultyManager.getLayoutStartX(resourcesManager.activity.DIFFICULTY);
        int startY = 50;
        for(Card card : cards){
            if(rowCount == resourcesManager.activity.NUM_ROWS)
                break;

            float x = (float) (card.getWidth() * colCount) + startX;
            float y = (float)(card.getHeight() * rowCount) + startY;
            card.setX(x);
            card.setY(y);
            card.addToScene(this);
            colCount++;

            if(colCount == resourcesManager.activity.NUM_COLS){
                rowCount++;
                colCount = 0;
            }
        }
    }
    private void mapCards(){
        Collections.shuffle(resourcesManager.card_regions, new Random(System.nanoTime()));
        int numberOfCards = (resourcesManager.activity.NUM_COLS * resourcesManager.activity.NUM_ROWS) / 2;
        for(int i = 0; i < numberOfCards; i++){
            cardMappings.put(i, resourcesManager.card_regions.get(i));
        }
    }

    private void fillCards(){
        cards = new ArrayList<Card>();

        for(int i = 0; i < (resourcesManager.activity.NUM_COLS * resourcesManager.activity.NUM_ROWS) / 2;i++){
            int cardId = i;
            Card card1 = new Card(0, 0, cardMappings.get(i), vbom, cardId, resourcesManager.particle_region, resourcesManager.engine);
            Card card2 = new Card(0, 0, cardMappings.get(i), vbom, cardId, resourcesManager.particle_region, resourcesManager.engine);
            cards.add(card1);
            cards.add(card2);
        }

        Collections.shuffle(cards, new Random(System.nanoTime()));
    }
}
