package com.knepe.megamemory;

import java.util.UUID;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

public class Card extends AnimatedSprite {
	public int CardId;
	private UUID UuId;
	final SequenceEntityModifier flipFaceModifier;
	final SequenceEntityModifier flipBackModifier;
	final IEntityModifierListener flipFaceListener;
	final IEntityModifierListener flipBackListener;
	public boolean isDisabled = false;
	public boolean isTurned = false;
	
	public Card(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, final int cardId) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		CardId = cardId;
		UuId = UUID.randomUUID();
		this.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
	    
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
                new ScaleModifier(0.2f, 1.0f, 0f, 1.0f, 1.0f),
                new ScaleModifier(0.2f, 0f, 1.0f, 1.0f, 1.0f, flipFaceListener));
		
		flipBackModifier = new SequenceEntityModifier(
                new ScaleModifier(0.2f, 1.0f, 0f, 1.0f, 1.0f), 
                new ScaleModifier(0.2f, 0f, 1.0f, 1.0f, 1.0f, flipBackListener));
		
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
		this.setVisible(false);
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
		if(this.IsTurned() || isDisabled || !this.isVisible())
			return true;
		
		showFace();
		
		GameActivity.executeCardCalculation(this);
		
		return true;
	}
}
