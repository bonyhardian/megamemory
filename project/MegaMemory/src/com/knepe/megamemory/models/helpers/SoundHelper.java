package com.knepe.megamemory.models.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.knepe.megamemory.MegaMemory;

import java.util.HashMap;

public class SoundHelper {
    private MegaMemory game;
    private HashMap<SoundType, Sound> sounds;

    public enum SoundType{
        CLICK, TURNCARD, HIDECARD, CORRECT
    }

    public SoundHelper(MegaMemory game){
        this.game = game;
        loadSounds();
    }

    private void loadSounds(){
        sounds = new HashMap<SoundType, Sound>();
        try
        {
            sounds.put(SoundType.CLICK, Gdx.audio.newSound(Gdx.files.internal("snd/click.ogg")));
            sounds.put(SoundType.TURNCARD, Gdx.audio.newSound(Gdx.files.internal("snd/turn-card.wav")));
            sounds.put(SoundType.HIDECARD, Gdx.audio.newSound(Gdx.files.internal("snd/turn-card.wav")));
            sounds.put(SoundType.CORRECT, Gdx.audio.newSound(Gdx.files.internal("snd/correct.wav")));
        }
        catch(Exception e){
            Gdx.app.log("MM", e.getMessage());
        }
    }

    public void playSound(SoundType soundType){
        if(game.getSoundEnabled() && sounds.get(soundType) != null)
            sounds.get(soundType).play();
    }
    public void dispose(){
        try{
            sounds.get(SoundType.CLICK).dispose();
            sounds.get(SoundType.TURNCARD).dispose();
            sounds.get(SoundType.HIDECARD).dispose();
            sounds.get(SoundType.CORRECT).dispose();
        }catch(Exception e){
            Gdx.app.log("MM", e.getMessage());
        }
    }
}
