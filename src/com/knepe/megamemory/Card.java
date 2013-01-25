package com.knepe.megamemory;

import java.util.UUID;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.SequenceModifier;

public class Card extends AnimatedSprite {
	public int CardId;
	private UUID UuId;
	final SequenceEntityModifier flipFaceModifier;
	final SequenceEntityModifier flipBackModifier;
	final IEntityModifierListener flipFaceListener;
	final IEntityModifier fadeOutModifier;
	final IEntityModifierListener flipBackListener;
	final AnimatedSprite correctFxSprite;
	public boolean isDisabled = false;
	public boolean isTurned = false;
	
	public Card(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, final int cardId, final AnimatedSprite correctFxSprite) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		CardId = cardId;
		UuId = UUID.randomUUID();
		this.correctFxSprite = correctFxSprite;
		this.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		fadeOutModifier = new AlphaModifier(0.4f, 0, 255);
	    fadeOutModifier.setAutoUnregisterWhenFinished(true);
	    
	    flipFaceListener = new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            	Card card = ((Card) pItem);
            	card.setCurrentTileIndex(1);
            }
           
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            }};
            
            flipBackListener = new IEntityModifierListener() {
                @Override
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                	Card card = ((Card) pItem);
                	card.setCurrentTileIndex(0);
                }
               
                @Override
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                }};
	    
		flipFaceModifier = new SequenceEntityModifier(
                new ScaleModifier(0.3f, 1.0f, 0f, 1.0f, 1.0f),
                new ScaleModifier(0.3f, 0f, 1.0f, 1.0f, 1.0f, flipFaceListener));
		
		flipBackModifier = new SequenceEntityModifier(
                new ScaleModifier(0.3f, 1.0f, 0f, 1.0f, 1.0f), 
                new ScaleModifier(0.3f, 0f, 1.0f, 1.0f, 1.0f, flipBackListener));
		
		flipFaceModifier.setAutoUnregisterWhenFinished(true);
		flipBackModifier.setAutoUnregisterWhenFinished(true);
	}

	public void addToScene(Scene scene) {
		scene.attachChild(this);
		scene.registerTouchArea(this);
	}
	
	public void disable(){
		isDisabled = true;
	}
	public void enable(){
		isDisabled = false;
	}
	
	public void hide(){
		IEntityModifierListener listener = new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            	
            	float[] cords = pItem.convertSceneToLocalCoordinates(pItem.getX(), pItem.getY());
            	correctFxSprite.setPosition(cords[0], cords[1] - 80);
            	correctFxSprite.animate(100);
            	pItem.attachChild(correctFxSprite);
            }
           
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            	pItem.setVisible(false);
            }};

		this.registerEntityModifier(new SequenceEntityModifier(new JumpModifier(0.6f,this.mX, this.mX, this.mY,this.mY, 20, listener)));
	}
	
	public boolean Match(Card c){
		return this.CardId == c.CardId && this.UuId != c.UuId;
	}
	public void showBack(){
		this.registerEntityModifier(flipBackModifier);
		flipBackModifier.reset();
		isTurned = false;
	}
	public void showFace(){
		this.registerEntityModifier(flipFaceModifier);
		flipFaceModifier.reset();
		isTurned = true;
	}
	
	public boolean IsTurned(){
		return (isTurned && this.isVisible());
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(this.IsTurned() || isDisabled)
			return true;
		
		showFace();
		
		GameActivity.executeCardCalculation(this);
		
		return true;
	}
}
