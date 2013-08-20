package com.knepe.megamemory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.entities.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import aurelienribon.tweenengine.TweenManager;

public class GameScreen implements Screen {
    private static Object lock = new Object();
    private MegaMemory game;
    private final TweenManager tweenManager = new TweenManager();
    private Stage stage;
    public static Integer SelectedId_first = -1;
    public static Integer SelectedId_second = -1;
    private static Boolean mc_isfirst = false;
    public static ArrayList<Card> cards;
    private HashMap<Integer, TextureRegion> cardMappings;
    public boolean isOpponent = true;

    private int totalBonus = 0;
    private int multiplier = 0;
    private static int mScore = 0;

    private int multiplier_opponent = 0;
    private int myLock = 0;
    private boolean gameStarted = false;

    public GameScreen(MegaMemory game){
        this.game = game;
    }

    private void gameStart(){
        SelectedId_first = -1;
        SelectedId_second = -1;
        mc_isfirst = false;
        cards = new ArrayList<Card>();
        cardMappings = new HashMap<Integer, TextureRegion>();
        mScore = 0;

        mapCards();
        fillCards();
        addCards();
        gameStarted = true;
    }

    private void mapCards(){
        int numberOfCards = (game.NUM_COLS * game.NUM_ROWS) / 2;
        for(int i = 0; i < numberOfCards; i++){
            cardMappings.put(i, new TextureRegion(new Texture(Gdx.files.internal("gfx/animals/" + i + ".png"))));
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

    private void finished(){
        /*timer.stop();
        unregisterUpdateHandler(timerUpdateHandler);
        if(activity.mMyId == null){
            showFinishPopup();
        }
        else{
            sendMyScore();
        }*/
        game.setScreen(new MainScreen(game));
    }

    private boolean checkIfFinished(){
        for(Card c : cards){
            if(c.state != Card.State.HIDE)
                return false;
        }

        return true;
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

                }
            }
        };

        Timer t = new Timer(false);
        t.schedule(tt, 1200);
    }

    public void executeCardCalculation(Card card){
        if(!isOpponent)
            myLock = 0;
        /*if(resourcesManager.activity.sound_enabled){
            resourcesManager.turn_card_sound.play();
        }*/


        //increaseTries();
        //disable all cards
        disableCards();

        mc_isfirst = !mc_isfirst;

        int id = cards.indexOf(card);

        if (mc_isfirst) {
            SelectedId_first = id;

            /*if(!isOpponent)
                sendFirstMove(id);*/

            enableCards();

        } else {
            SelectedId_second = id;

            /*if(!isOpponent)
                sendSecondMove(id);*/

            playMove();
        }
    }

    private void checkCards(){
        if(cards.get(SelectedId_first).match(cards.get(SelectedId_second))){
            //correct
            if(cards.get(SelectedId_first).isTurned() && cards.get(SelectedId_second).isTurned()){

                //increaseMultiplier();

                /*if(resourcesManager.activity.sound_enabled){
                    resourcesManager.correct_sound.play();
                }*/

                cards.get(SelectedId_first).hide();
                cards.get(SelectedId_second).hide();
                //showBonusText();

                //increaseScore(30 * (resourcesManager.activity.DIFFICULTY + 1));
                enableCards();

                return;
            }
        }

        //incorrect
        if(isOpponent)
            multiplier_opponent = 0;
        else
            multiplier = 0;

        /*if(resourcesManager.activity.sound_enabled){
            resourcesManager.turn_card_sound.play();
        }*/

        for(int y=0;y < cards.size(); y++){
            cards.get(y).enable();
            if(cards.get(y).isTurned()){
                cards.get(y).showBack();
            }
        }

        /*if(!isOpponent && myLock == 0){
            sendDone();
            setOpponentsTurn();
        }*/
    }

    private void fillCards(){
        cards = new ArrayList<Card>();
        TextureRegion back = new TextureRegion(new Texture(Gdx.files.internal("gfx/card-back.png")));
        for(int i = 0; i < (game.NUM_COLS * game.NUM_ROWS) / 2;i++){
            int cardId = i;
            final Card card1 = new Card(cardId, cardMappings.get(i), back, tweenManager);
            final Card card2 = new Card(cardId, cardMappings.get(i), back, tweenManager);
            card1.setTouchable(Touchable.enabled);
            card2.setTouchable(Touchable.enabled);
            final GameScreen gameScreen = this;
            card1.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if(card1.click()){
                        gameScreen.executeCardCalculation(card1);
                    }
                }
            });
            card2.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if(card2.click()){
                        gameScreen.executeCardCalculation(card2);
                    }
                }
            });

            cards.add(card1);
            cards.add(card2);
        }
    }

    private void addCards(){
        Table table = new Table();
        table.setFillParent(true);
        int rowCount = 0;
        int colCount = 0;

        for(Card c : cards){
            if(rowCount == game.NUM_ROWS)
                break;

            table.add(c);
            colCount++;

            if(colCount == game.NUM_COLS){
                table.row();
                rowCount++;
                colCount = 0;
            }
        }

        table.setTouchable(Touchable.childrenOnly);
        stage.addActor(table);
    }

    private void setBackground(){
        Texture backgroundTexture = new Texture(Gdx.files.internal("gfx/main-bg.jpg"));
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image background = new Image(backgroundTexture);
        background.toBack();
        stage.addActor(background);
    }

    @Override
    public void render(float delta) {
        if(checkIfFinished()){
            finished();
        }
        tweenManager.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        Table.drawDebug(stage);

    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        setBackground();
        gameStart();
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
