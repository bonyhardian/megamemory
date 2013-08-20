package com.knepe.megamemory.models.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.knepe.megamemory.models.accessors.ImageTweenAccessor;

import java.util.UUID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by knepe on 2013-08-19.
 */
public class Card extends Actor {
    private TextureRegion face;
    private TextureRegion back;
    private final TweenManager tweenManager;
    public int CardId;
    private UUID UuId;
    public boolean isDisabled = false;
    public boolean isTurned = false;
    public State state = State.SHOWBACK;

    public enum State{
        SHOWFACE, SHOWBACK, HIDE
    }
    public Card(final int cardId, TextureRegion face, TextureRegion back, TweenManager tweenManager){
        this.setWidth(back.getRegionWidth());
        this.setHeight(back.getRegionHeight());
        CardId = cardId;
        UuId = UUID.randomUUID();
        this.face = face;
        this.back = back;
        this.tweenManager = tweenManager;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        switch(state){
            case SHOWBACK:
                batch.draw(back, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                break;
            case SHOWFACE:
                batch.draw(face, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                break;
        }
    }

    public boolean match(Card c){
        return this.CardId == c.CardId && this.UuId != c.UuId;
    }

    public void showBack(){
        final Card card = this;
        final float originX = this.getX();
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(this, ImageTweenAccessor.POS_XY, 0.2f).target(this.getX() + (this.getWidth() / 2), this.getY()))
                .push(Tween.to(this, ImageTweenAccessor.SCALE_XY, 0.2f).target(0, 1f)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                card.state = State.SHOWBACK;
                                Timeline.createSequence()
                                        .beginParallel()
                                        .push(Tween.to(card, ImageTweenAccessor.POS_XY, 0.2f).target(originX, card.getY()))
                                        .push(Tween.to(card, ImageTweenAccessor.SCALE_XY, 0.2f).target(1f, 1f))
                                        .end()
                                .start(tweenManager);
                                isTurned = false;
                            }
                        }))
                .end()
        .start(tweenManager);
    }
    public void showFace(){
        isTurned = true;
        final Card card = this;
        final float originX = this.getX();
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(this, ImageTweenAccessor.POS_XY, 0.2f).target(this.getX() + (this.getWidth() / 2), this.getY()))
                .push(Tween.to(this, ImageTweenAccessor.SCALE_XY, 0.2f).target(0, 1f)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                card.state = State.SHOWFACE;
                                Timeline.createSequence()
                                        .beginParallel()
                                        .push(Tween.to(card, ImageTweenAccessor.POS_XY, 0.2f).target(originX, card.getY()))
                                        .push(Tween.to(card, ImageTweenAccessor.SCALE_XY, 0.2f).target(1f, 1f))
                                        .end()
                                        .start(tweenManager);
                            }
                        }))
                .end()
                .start(tweenManager);
    }

    public boolean click(){
        if(this.isTurned() || isDisabled)
            return false;

        showFace();
        return true;
    }

    public boolean isTurned(){
        return isTurned;
    }
    public void disable(){
        isDisabled = true;
    }
    public void enable(){
        isDisabled = false;
    }
    public void hide(){
        state = State.HIDE;
        this.remove();
    }
}
