package com.knepe.megamemory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.MenuFactory;
import com.knepe.megamemory.models.accessors.SpriteTweenAccessor;

import java.util.Random;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;

public class MainScreen implements Screen {

    private MegaMemory game;
    private Stage stage;
    private Image mega;
    private Image memory;
    private Image fish;
    private Image fish2;
    private Array<Image> bubbles = new Array<Image>();
    private final TweenManager tweenManager = new TweenManager();
    private MenuFactory menuFactory;

    public MainScreen(MegaMemory game){
        if(game.googlePlayInterface.getSignedIn()){
            game.googlePlayInterface.reset();
        }
        this.game = game;
    }

    @Override
    public void render(float delta) {
        tweenManager.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //stage.setViewport(width, height, false);
    }

    @Override
    public void show() {
        stage = new Stage(game.width, game.height, true){
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    menuFactory.back();
                }
                return super.keyDown(keyCode);
            }
        };
        Gdx.input.setInputProcessor(stage);

        loadTextures();
        createAnimations();
        createMenus();
    }

    private void createMenus(){
        menuFactory = new MenuFactory(stage, tweenManager, game);
        menuFactory.setMenu(0);
    }

    private void loadTextures(){
        Texture megaTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/mega.png"));
        Texture memoryTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/memory.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/main-bg.jpg"));
        Texture fishTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/fish.png"));
        Texture fish2Texture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/fish2.png"));
        Texture bubbleTexture = new Texture(Gdx.files.internal(game.assetBasePath + "gfx/bubble.png"));

        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Image background = new Image(backgroundTexture);
        background.setWidth(game.width);
        background.setHeight(game.height);
        background.toBack();
        stage.addActor(background);
        mega = new Image(megaTexture);
        stage.addActor(mega);
        memory = new Image(memoryTexture);
        stage.addActor(memory);
        fish = new Image(fishTexture);
        stage.addActor(fish);
        fish2 = new Image(fish2Texture);
        stage.addActor(fish2);
        for(int i = 0;i < 15;i++){
            float randomScale = getRandomNumberBetween(0.2f, 0.9f);
            Image bubble = new Image(bubbleTexture);
            float randomX = getRandomNumberBetween(bubble.getWidth(), Gdx.graphics.getWidth());
            bubble.scale(randomScale);
            bubble.setPosition(randomX, -(bubble.getHeight() * 2));
            stage.addActor(bubble);
            bubbles.add(bubble);
        }

    }

    private static float getRandomNumberBetween(float min, float max){
        Random random = new Random();
        return (random.nextFloat() * (max-min)) + min;
    }

    private void createAnimations(){
        mega.setPosition((Gdx.graphics.getWidth() / 2) - mega.getWidth(), Gdx.graphics.getHeight() + (mega.getHeight() * 2));
        memory.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() + (memory.getHeight() * 2));
        fish.setPosition(Gdx.graphics.getWidth() + fish.getWidth(), Gdx.graphics.getHeight() / 2);
        fish2.setPosition(-fish2.getWidth(), Gdx.graphics.getHeight() / 3);

        Timeline.createSequence()
                .pushPause(0.3f)
                .push(Tween.to(mega, SpriteTweenAccessor.POS_XY, 0.5f).target(mega.getX(), Gdx.graphics.getHeight() - mega.getHeight()).ease(Bounce.INOUT))
                .pushPause(0.3f)
                .push(Tween.to(memory, SpriteTweenAccessor.POS_XY, 0.5f).target(memory.getX(), Gdx.graphics.getHeight() - (memory.getHeight() + 50)).ease(Bounce.INOUT))
                .pushPause(0.5f)
                .start(tweenManager);

        Tween.to(fish, SpriteTweenAccessor.POS_XY, 15f).target(0 - fish.getWidth(), fish.getY())
                .repeat(-1, 2f)
                .start(tweenManager);

        for(Image bubble : bubbles){
            float randomSpeed = getRandomNumberBetween(10f, 25f);
            float randomDelay = getRandomNumberBetween(5f, 15f);
            Tween.to(bubble, SpriteTweenAccessor.POS_XY, randomSpeed).target(bubble.getX(), Gdx.graphics.getHeight() + bubble.getHeight())
                    .repeat(-1, randomDelay)
                    .start(tweenManager);
        }

        Tween.to(fish2, SpriteTweenAccessor.POS_XY, 20f).target(Gdx.graphics.getWidth() + fish2.getWidth(), fish2.getY())
                .repeat(-1, 2f)
                .start(tweenManager);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
