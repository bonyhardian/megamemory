package com.knepe.megamemory.models.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by knepe on 2013-08-26.
 */
public class ParticleEffectActor extends Actor {
    ParticleEffect effect;

    public ParticleEffectActor(ParticleEffect effect){
        this.effect = effect;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        effect.draw(batch); //define behavior when stage calls Actor.draw()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.update(delta); //update it
        if(this.effect.isComplete()){
            this.remove();
        }

    }

    public void start(){
        effect.start();
    }
    public void setPosition(float x, float y){
        effect.setPosition(x, y);
    }
}
