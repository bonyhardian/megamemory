package com.knepe.megamemory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.accessors.SpriteTweenAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class SplashScreen implements Screen {
    private Stage stage;
    private MegaMemory game;
    private Sprite knepe;
    private Sprite inc;
    private final SpriteBatch batch = new SpriteBatch();
    private final TweenManager tweenManager = new TweenManager();
    private TweenCallback callback;

    public SplashScreen(MegaMemory game)
    {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        tweenManager.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        knepe.draw(batch);
        inc.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        Texture.setEnforcePotImages(false);
        stage = new Stage();
        this.callback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(new MainScreen(game));
            }
        };

        loadTextures();
        createAnimations();
    }

    private void loadTextures(){
        Texture splash1Texture = new Texture(Gdx.files.internal("gfx/splash-1.png"));
        Texture splash2Texture = new Texture(Gdx.files.internal("gfx/splash-2.png"));

        knepe = new Sprite(splash1Texture);
        inc = new Sprite(splash2Texture);
    }


    private void createAnimations(){
        knepe.setPosition(-knepe.getWidth(), Gdx.graphics.getHeight() / 2);
        inc.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        Timeline.createSequence()
                .push(Tween.set(inc, SpriteTweenAccessor.OPACITY).target(0))
                .pushPause(0.8f)
                .push(Tween.to(knepe, SpriteTweenAccessor.POS_XY, 0.5f).target((Gdx.graphics.getWidth() / 2) - knepe.getWidth(), knepe.getY()).ease(Quart.OUT))
                .pushPause(-0.3f)
                .beginParallel()
                .push(Tween.set(inc, SpriteTweenAccessor.OPACITY).target(1))
                .push(Tween.to(inc, SpriteTweenAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.OUT))
                .end()
                .pushPause(0.2f)
                .push(Tween.to(inc, SpriteTweenAccessor.SCALE_XY, 0.3f).waypoint(1.6f, 0.4f).target(1f, 1f).ease(Cubic.OUT))
                .pushPause(0.3f)
                .push(Tween.to(inc, SpriteTweenAccessor.ROTATION, 1f).target(360 * 5).ease(Quad.OUT))
                .beginParallel()
                .push(Tween.to(knepe, SpriteTweenAccessor.POS_XY, 0.5f).target(Gdx.graphics.getWidth() + knepe.getWidth(), knepe.getY()).ease(Back.IN))
                .push(Tween.to(inc, SpriteTweenAccessor.POS_XY, 0.5f).target(Gdx.graphics.getWidth() + inc.getWidth(), inc.getY()).ease(Back.IN))
                .end()
                .pushPause(0.8f)
                .setCallback(callback)
                .start(tweenManager);
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
