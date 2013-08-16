package com.knepe.megamemory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.knepe.megamemory.MegaMemory;
import com.knepe.megamemory.models.SpriteTweenAccessor;

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
    private static final int PX_PER_METER = 400;
    private final OrthographicCamera camera = new OrthographicCamera();
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
        GLCommon gl = Gdx.gl;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        knepe.draw(batch);
        inc.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        this.callback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(new MainMenuScreen(game));
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
        Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());
        Tween.setWaypointsLimit(8);
        float wpw = 1f;
        float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        camera.viewportWidth = wpw;
        camera.viewportHeight = wph;
        camera.update();

        Sprite[] sprites = new Sprite[] {knepe, inc};
        for (Sprite sp : sprites) {
            sp.setSize(sp.getWidth()/PX_PER_METER, sp.getHeight()/PX_PER_METER);
            sp.setOrigin(sp.getWidth()/2, sp.getHeight()/2);
        }

        knepe.setPosition(-0.320f, -0.166f);
        inc.setPosition(-0.238f, -0.300f);

        Timeline.createSequence()
                .push(Tween.set(knepe, SpriteTweenAccessor.POS_XY).targetRelative(-1, 0))
                .push(Tween.set(inc, SpriteTweenAccessor.SCALE_XY).target(7, 7))
                .push(Tween.set(inc, SpriteTweenAccessor.OPACITY).target(0))
                .pushPause(0.8f)
                .push(Tween.to(knepe, SpriteTweenAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))
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
                .push(Tween.to(knepe, SpriteTweenAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
                .push(Tween.to(inc, SpriteTweenAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
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
