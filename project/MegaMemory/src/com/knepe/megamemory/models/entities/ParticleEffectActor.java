package com.knepe.megamemory.models.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
    ParticleEffect effect;

    public ParticleEffectActor(ParticleEffect effect){
        this.effect = effect;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        effect.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.update(delta);
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
