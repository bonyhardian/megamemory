package com.knepe.megamemory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.GameTimer;
import com.knepe.megamemory.models.accessors.ActorTweenAccessor;
import com.knepe.megamemory.models.entities.Card;
import com.knepe.megamemory.models.helpers.SoundHelper;
import com.knepe.megamemory.models.helpers.ThemeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;

public class GameScreen implements Screen {
    private static Object lock = new Object();
    public Integer cardsLeft = -1;
    private MegaMemory game;
    private final TweenManager tweenManager = new TweenManager();
    private Stage stage;
    public static Integer SelectedId_first = -1;
    public static Integer SelectedId_second = -1;
    private static Boolean mc_isfirst = false;
    public static ArrayList<Card> cards;
    private HashMap<Integer, TextureRegion> cardMappings;
    private List<TextureRegion> cardTextureRegions;
    public boolean isOpponent = false;
    public boolean isMultiplayer = false;
    private int totalBonus = 0;
    private GameTimer timer;
    private int tries = 0;
    private int multiplier = 0;
    private static int mScore = 0;
    private Skin skin = null;
    private Skin hudSkin = null;
    private Skin bonusSkin = null;
    private int multiplier_opponent = 0;
    private int myLock = 0;
    private boolean gameStarted = false;
    private Label hudStatusLabel = null;
    private Label hudScoreLabel = null;
    private boolean isFinished = false;
    private Label bonusLabel = null;
    public ParticleEffect particleEffect = null;

    public GameScreen(MegaMemory game){
        this.game = game;
        this.isMultiplayer = (game.googlePlayInterface.getOpponent() != null);
    }

    public void showOpponentLeftPopup(){
        Dialog dialog = new Dialog("", skin);
        dialog.setSize(537, 327);
        dialog.setPosition((Gdx.graphics.getWidth() / 2) - (dialog.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (dialog.getHeight() / 2));
        dialog.text("Your opponent left! :(");
        TextButton homeButton = new TextButton("OK", skin);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.googlePlayInterface.leaveRoom();
                game.setScreen(new MainScreen(game));
            }
        });
        homeButton.setPosition((dialog.getWidth() / 2) - (homeButton.getWidth() / 2), 25);
        dialog.addActor(homeButton);
        dialog.toFront();
        stage.addActor(dialog);
    }

    public void setOpponentsTurn(){
        isOpponent = true;
    }
    public void setMyTurn(){
        isOpponent = false;
        myLock = 1;
    }
    private void gameStart(){
        SelectedId_first = -1;
        SelectedId_second = -1;
        mc_isfirst = false;
        cards = new ArrayList<Card>();
        cardMappings = new HashMap<Integer, TextureRegion>();
        cardTextureRegions = new ArrayList<TextureRegion>();
        mScore = 0;
        timer = new GameTimer();
        tries = 0;

        loadCardTextureRegions();
        mapCards();
        fillCards();
        addCards();
        cardsLeft = cards.size();
    }

    private void loadCardTextureRegions(){
        FileHandle dirHandle = Gdx.files.internal(game.assetBasePath + ThemeHelper.getPath(game.THEME));
        for(FileHandle entry : dirHandle.list(".png")){
            cardTextureRegions.add(new TextureRegion(new Texture(entry)));
        }
        shuffleCardTextureRegions();
    }

    private void mapCards(){
        int numberOfCards = (game.NUM_COLS * game.NUM_ROWS) / 2;
        for(int i = 0; i < numberOfCards; i++){
            cardMappings.put(i, cardTextureRegions.get(i));
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
        isFinished = true;
        timer.stop();
        if(!isMultiplayer){
            showFinishDialog();
        }
        else{
            sendMyScore();
        }

        game.googlePlayInterface.submitScore(Integer.parseInt(getScore()));
    }

    private void playMove(){
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try{
                    synchronized (lock) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                checkCards();
                            }
                        });
                    }
                }
                catch (Exception e) {

                }
            }
        };

        Timer t = new Timer(false);
        t.schedule(tt, 1200);
    }

    public void showFinishPopupMultiplayer(int opponentScore){
        Dialog dialog = new Dialog("", skin);
        dialog.setSize(537, 327);
        dialog.setPosition((Gdx.graphics.getWidth() / 2) - (dialog.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (dialog.getHeight() / 2));
        Label headerLabel = new Label("Game finished!", skin);
        Label resultLabel = new Label(opponentScore > Integer.parseInt(getScore()) ? "You lose!" : "You win!", skin);
        Label yourScoreLabel = new Label("Your score: " + getScore(), skin);
        Label opponentsScoreLabel = new Label(game.googlePlayInterface.getOpponent().getDisplayName() + "'s score: " + opponentScore, skin);
        headerLabel.setPosition(15, dialog.getHeight() - (headerLabel.getHeight() * 1.7f));
        resultLabel.setPosition(15, headerLabel.getY() - resultLabel.getHeight());
        yourScoreLabel.setPosition(15, resultLabel.getY() - yourScoreLabel.getHeight());
        opponentsScoreLabel.setPosition(15, yourScoreLabel.getY() - opponentsScoreLabel.getHeight());
        dialog.addActor(headerLabel);
        dialog.addActor(resultLabel);
        dialog.addActor(yourScoreLabel);
        dialog.addActor(opponentsScoreLabel);
        Button homeButton = new Button(skin);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game));
            }
        });
        homeButton.add(new Image(new Texture(Gdx.files.internal(game.assetBasePath + "gfx/icons/Home.png"))));
        homeButton.setPosition((dialog.getWidth() / 2) - (homeButton.getWidth() / 2), 25);
        dialog.addActor(homeButton);
        dialog.toFront();
        stage.addActor(dialog);
    };

    public void executeCardCalculation(int id)
    {
        Card card = cards.get(id);
        if(card == null) return;
        card.showFace();
        executeCardCalculation(card);
    }

    public void executeCardCalculation(Card card){
        if(!isOpponent)
            myLock = 0;

        game.soundHelper.playSound(SoundHelper.SoundType.TURNCARD);

        increaseTries();
        //disable all cards
        disableCards();

        mc_isfirst = !mc_isfirst;

        int id = cards.indexOf(card);

        if (mc_isfirst) {
            SelectedId_first = id;

            if(isMultiplayer && !isOpponent)
                sendFirstMove(id);

            enableCards();

        } else {
            SelectedId_second = id;

            if(isMultiplayer && !isOpponent)
                sendSecondMove(id);

            playMove();
        }
    }

    private void showBonusText(){
        int mCurrentMultiplier = isOpponent ? multiplier_opponent : multiplier;

        if(mCurrentMultiplier == 1 || isFinished){
            return;
        }

        String str_txt;

        switch(mCurrentMultiplier){
            case 2:
                str_txt = "Sweet!";
                if(!isOpponent)
                    game.googlePlayInterface.unlockAchievement("CgkI1JqZzYQJEAIQAg");
                increaseTotalBonus(50);
                break;
            case 3:
                str_txt = "Awesome!";
                if(!isOpponent)
                    game.googlePlayInterface.unlockAchievement("CgkI1JqZzYQJEAIQAw");
                increaseTotalBonus(100);
                break;
            case 4:
                str_txt = "Crazy!";
                if(!isOpponent)
                    game.googlePlayInterface.unlockAchievement("CgkI1JqZzYQJEAIQBQ");
                increaseTotalBonus(150);
                break;
            case 5:
                str_txt = "Impossible!";
                if(!isOpponent)
                    game.googlePlayInterface.unlockAchievement("CgkI1JqZzYQJEAIQBg");
                increaseTotalBonus(200);
                break;
            default:
                str_txt = "Sweet!";
                break;
        }

        if(bonusLabel == null){
            bonusLabel = new Label(str_txt, bonusSkin);
            stage.addActor(bonusLabel);
        }
        else{
            bonusLabel.setText(str_txt);
        }

        bonusLabel.setPosition((Gdx.graphics.getWidth() / 2) - (bonusLabel.getWidth() / 2), Gdx.graphics.getHeight() + (bonusLabel.getHeight() * 2));
        Timeline.createSequence()
                .push(Tween.to(bonusLabel, ActorTweenAccessor.POS_XY, 0.3f).target(bonusLabel.getX(), Gdx.graphics.getHeight() / 2).ease(Bounce.OUT))
                .pushPause(1f)
                .push(Tween.to(bonusLabel, ActorTweenAccessor.POS_XY, 0.1f).target(Gdx.graphics.getWidth() + (bonusLabel.getWidth() * 4), Gdx.graphics.getHeight() / 2))
                .start(tweenManager);
    }

    private void checkCards(){
        if(cards.get(SelectedId_first).match(cards.get(SelectedId_second))){
            //correct
            if(cards.get(SelectedId_first).isTurned() && cards.get(SelectedId_second).isTurned()){

                increaseMultiplier();

                game.soundHelper.playSound(SoundHelper.SoundType.CORRECT);

                enableCards();
                cards.get(SelectedId_first).hide(this);
                cards.get(SelectedId_second).hide(this);

                increaseScore(30 * (game.DIFFICULTY + 1));


                if(cardsLeft == 0)
                    finished();

                showBonusText();

                return;
            }
        }

        //incorrect
        if(isOpponent)
            multiplier_opponent = 0;
        else
            multiplier = 0;

        game.soundHelper.playSound(SoundHelper.SoundType.HIDECARD);

        for(int y=0;y < cards.size(); y++){
            cards.get(y).enable();
            if(cards.get(y).isTurned()){
                cards.get(y).showBack();
            }
        }

        if(isMultiplayer){
            if(!isOpponent && myLock == 0){
                sendDone();
                setOpponentsTurn();
            }
        }
    }

    private void fillCards(){
        cards = new ArrayList<Card>();
        TextureRegion back = new TextureRegion(new Texture(Gdx.files.internal(game.assetBasePath + "gfx/card-back.png")));
        for(int i = 0; i < (game.NUM_COLS * game.NUM_ROWS) / 2;i++){
            int cardId = i;

            final Card card1 = new Card(cardId, cardMappings.get(i), back, tweenManager, new ParticleEffect(particleEffect));
            final Card card2 = new Card(cardId, cardMappings.get(i), back, tweenManager, new ParticleEffect(particleEffect));
            card1.setTouchable(Touchable.enabled);
            card2.setTouchable(Touchable.enabled);
            final GameScreen gameScreen = this;
            card1.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if(isOpponent) return;
                    if(card1.click()){
                        gameScreen.executeCardCalculation(card1);
                    }
                }
            });
            card2.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if(isOpponent) return;
                    if(card2.click()){
                        gameScreen.executeCardCalculation(card2);
                    }
                }
            });

            cards.add(card1);
            cards.add(card2);
        }

        shuffleCards();
    }

    private int getSeed(){
        return (game.googlePlayInterface.getOpponent().getId() + game.googlePlayInterface.getMyId()).compareTo(game.googlePlayInterface.getRoomId());
    }

    private void shuffleCards(){
        if(!isMultiplayer){
            Collections.shuffle(cards, new Random(System.nanoTime()));
            return;
        }

        ArrayList<Card> result = new ArrayList<Card>();
        ArrayList<Card> oldList = (ArrayList<Card>) cards.clone();

        int seed = getSeed();

        int i = 1;
        while (oldList.size() > 0)
        {
            int index = getIndex(oldList.size(), i++, seed);
            result.add(oldList.get(index));
            oldList.remove(index);
        }

        cards = result;
    }

    private void shuffleCardTextureRegions(){
        if(!isMultiplayer){
            Collections.shuffle(cardTextureRegions, new Random(System.nanoTime()));
            return;
        }

        ArrayList<TextureRegion> result = new ArrayList<TextureRegion>();
        ArrayList<TextureRegion> oldList = new ArrayList<TextureRegion>(cardTextureRegions);

        int seed = getSeed();

        int i = 1;
        while (oldList.size() > 0)
        {
            int index = getIndex(oldList.size(), i++, seed);
            result.add(oldList.get(index));
            oldList.remove(index);
        }

        cardTextureRegions = result;
    }

    private int getIndex(int max, int iteration, int seed)
    {
        float i = seed*iteration;
        i = i%max;
        return (int) i;
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
        Texture backgroundTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/main-bg.jpg"));
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image background = new Image(backgroundTexture);
        background.setWidth(game.width);
        background.setHeight(game.height);
        background.toBack();
        stage.addActor(background);
    }

    private void showFinishDialog(){
        Dialog dialog = new Dialog("", skin);
        dialog.setSize(537, 327);
        dialog.setPosition((Gdx.graphics.getWidth() / 2) - (dialog.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (dialog.getHeight() / 2));
        Label goodJobLabel = new Label("Good job!", skin);
        Label scoreLabel = new Label("Score: " + getScore(), skin);
        Label triesLabel = new Label("Tries: " + tries, skin);
        Label timeLabel = new Label("Time: " + timer.getStopTime(), skin);
        goodJobLabel.setPosition(15, dialog.getHeight() - (goodJobLabel.getHeight() * 1.7f));
        scoreLabel.setPosition(15, goodJobLabel.getY() - scoreLabel.getHeight());
        triesLabel.setPosition(15, scoreLabel.getY() - triesLabel.getHeight());
        timeLabel.setPosition(15, triesLabel.getY() - timeLabel.getHeight());
        dialog.addActor(goodJobLabel);
        dialog.addActor(scoreLabel);
        dialog.addActor(triesLabel);
        dialog.addActor(timeLabel);
        Button homeButton = new Button(skin);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game));
            }
        });
        homeButton.add(new Image(new Texture(Gdx.files.internal(game.assetBasePath + "gfx/icons/Home.png"))));
        homeButton.setPosition((dialog.getWidth() / 2) - (homeButton.getWidth() / 2), 20);
        dialog.addActor(homeButton);
        dialog.toFront();
        hudScoreLabel.setText("Score: " + getScore());
        stage.addActor(dialog);
    }

    @Override
    public void render(float delta) {
        if(isMultiplayer && !gameStarted && game.googlePlayInterface.getIsOpponentReady()){
            startGame();
        }

        updateHud();

        tweenManager.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        Table.drawDebug(stage);
    }

    private void updateHud(){
        if(isFinished) return;
        if(hudScoreLabel != null)
            hudScoreLabel.setText("Score: " + getScoreRealTime());
        if(hudStatusLabel != null)
            hudStatusLabel.setText(getStatusLabelText());
    }

    @Override
    public void resize(int width, int height) {
        //stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/uiskin.json"));
        hudSkin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/hudskin.json"));
        bonusSkin = new Skin(Gdx.files.internal(game.assetBasePath + "data/skin/bonusskin.json"));
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(game.assetBasePath + "data/particle/star.p"), Gdx.files.internal(game.assetBasePath + "data/particle"));

        stage = new Stage(game.width, game.height, true){
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    if(!isFinished)
                        game.googlePlayInterface.leaveRoom();
                    else
                        game.setScreen(new MainScreen(game));
                }
                return super.keyDown(keyCode);
            }
        };
        Gdx.input.setInputProcessor(stage);

        setBackground();
        createHud();

        if(isMultiplayer){
            sendReadyToPlay();
        }
        else
            startGame();
    }

    private Table createHudTable(){
        Table table = new Table();
        table.setTransform(true);
        Texture hudTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/hud-table.png"));
        table.setBackground(new TextureRegionDrawable(new TextureRegion(hudTexture)));
        table.setWidth(hudTexture.getWidth());
        table.setHeight(hudTexture.getHeight());

        return table;
    }

    private void createHud(){
        Texture hudTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/hud.png"));
        Image hud = new Image(hudTexture);
        hud.setWidth(game.width);
        stage.addActor(hud);

        Table hudScoreTable = createHudTable();
        hudScoreTable.setPosition(5, hud.getHeight() - (hudScoreTable.getHeight() * 1.3f));
        hudScoreLabel = new Label("Score: " + getScoreRealTime(), hudSkin);
        hudScoreLabel.setPosition(10, (hudScoreTable.getHeight() / 2) - (hudScoreLabel.getHeight() / 2));
        hudScoreTable.addActor(hudScoreLabel);
        stage.addActor(hudScoreTable);

        if(isMultiplayer){
            //add status label
            Table hudStatusTable = createHudTable();
            hudStatusTable.setPosition(5, 2);
            hudStatusLabel = new Label(getStatusLabelText(), hudSkin);
            hudStatusLabel.setPosition(10, (hudScoreTable.getHeight() / 2) - (hudStatusLabel.getHeight() / 2));
            hudStatusTable.addActor(hudStatusLabel);
            stage.addActor(hudStatusTable);
        }
    }

    private String getStatusLabelText(){
        if(isMultiplayer){
            if(!gameStarted)
                return "Waiting..";
            if(isOpponent)
                return game.googlePlayInterface.getOpponent().getDisplayName() + "'s turn";
            else
                return "Your turn";
        }
        else
        {
            return null;
        }
    }

    private void startGame(){
        gameStart();
        if(isMultiplayer){
            if (game.googlePlayInterface.getMyId().compareTo(game.googlePlayInterface.getOpponent().getId()) > 0) {
                setMyTurn();
            } else {
                setOpponentsTurn();
            }
        }

        timer.start();
        gameStarted = true;
    }

    private void sendReadyToPlay(){
        String stringMessage = "Ready";
        byte[] message = stringMessage.getBytes();

        game.googlePlayInterface.sendReliableRealTimeMessage(message, game.googlePlayInterface.getRoomId(), game.googlePlayInterface.getOpponent().getId());
    }
    private void sendFirstMove(Integer id){
        String stringMessage = "First:" + id;
        byte[] message = stringMessage.getBytes();

        game.googlePlayInterface.sendReliableRealTimeMessage(message, game.googlePlayInterface.getRoomId(), game.googlePlayInterface.getOpponent().getId());
    }

    private void sendSecondMove(Integer id){
        String stringMessage = "Second:" + id;
        byte[] message = stringMessage.getBytes();

        game.googlePlayInterface.sendReliableRealTimeMessage(message, game.googlePlayInterface.getRoomId(), game.googlePlayInterface.getOpponent().getId());
    }

    public void sendDone(){
        String stringMessage = "Done";
        byte[] message = stringMessage.getBytes();

        game.googlePlayInterface.sendReliableRealTimeMessage(message, game.googlePlayInterface.getRoomId(), game.googlePlayInterface.getOpponent().getId());
    }

    private void sendMyScore(){
        String stringMessage = "Score:" + getScore();
        byte[] message = stringMessage.getBytes();

        game.googlePlayInterface.sendReliableRealTimeMessage(message, game.googlePlayInterface.getRoomId(), game.googlePlayInterface.getOpponent().getId());
    }

    private void increaseTotalBonus(int increment){
        if(!isOpponent || !isMultiplayer)
            totalBonus += increment;

    }

    private void increaseTries(){
        if(!isOpponent || !isMultiplayer)
           tries++;
    }

    private void increaseScore(int increment){
        if(!isOpponent || !isMultiplayer)
            mScore += increment;
    }

    private void increaseMultiplier(){
        if(isOpponent)
            multiplier_opponent++;
        else
            multiplier++;
    }

    private String getScore(){
        double time = timer.getStopSeconds();
        if(isMultiplayer)
            time = 0;
        int score = (int) Math.round(mScore - (time + tries) + totalBonus);

        return "" + (score > 0 ? score : 0);
    }


    private String getScoreRealTime(){
        if(timer == null) return "0";
        double time = timer.getStopSecondsRealTime();
        if(isMultiplayer)
            time = 0;
        int score = (int) Math.round(mScore - (time + tries) + totalBonus);

        return "" + (score > 0 ? score : 0);
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
