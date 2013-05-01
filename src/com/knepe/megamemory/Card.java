package com.knepe.megamemory;

import java.util.Random;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.IParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
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
	private TextureRegion mPartReg;
	private VertexBufferObjectManager mVertexBufferObjectManager;
	
	private Engine mEngine;
	 //Number of particles to show
    private int mNumPart = 10;
    //Time to show the particles
    private int mTimePart = 1;
    //Speed of the particles
    private float mSpdInitial = 200.0f;
    private float mSpdParticle = mSpdInitial;
private float mSpdIncr = 125.0f/mNumPart;
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
	    super.preDraw(pGLState, pCamera);
	    //to prevent "banding" on gradient
	    pGLState.enableDither();
	}
	
	public Card(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, final int cardId, TextureRegion pPartReg, Engine pEngine) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		CardId = cardId;
		UuId = UUID.randomUUID();
		mPartReg = pPartReg;
		mEngine = pEngine;
		mVertexBufferObjectManager = pVertexBufferObjectManager;
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

   private void createParticleEffect(float posX, float posY) {
           //Init the particle system
           final SpriteParticleSystem  particleSystem = new SpriteParticleSystem(new PointParticleEmitter(posX, posY), (float)60*mNumPart, (float)60*mNumPart, mNumPart, mPartReg, mVertexBufferObjectManager);
           //Dirt color
           //particleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(0.99f, 0.0f, 0.37f));
           particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA));
           //They will shrink
           particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, mTimePart, 1f, 0f));
           particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0.5f, 1, 1, 0));
           final Random generator = new Random();
           
           //Init the particles with random in the angle
           particleSystem.addParticleInitializer(new IParticleInitializer<Sprite>() {
            
       @Override
       public void onInitializeParticle(Particle<Sprite> pParticle) {
    	   //set random color
    	   int red = (int) (Math.random() * 256);
    	   int blue = (int) (Math.random() * 256);
    	   int green = (int) (Math.random() * 256);
    	   
    	   pParticle.getEntity().setColor(new Color(red, green, blue));
           //Create particles between 225º - 315º
           int ang = generator.nextInt(90) + 225;
           mSpdParticle -= mSpdIncr;
           float fVelocityX = (float) (Math.cos(Math.toRadians(ang)) * mSpdParticle);
           float fVelocityY = (float) (Math.sin(Math.toRadians(ang)) * mSpdParticle);
           
           pParticle.getPhysicsHandler().setVelocity(fVelocityX, fVelocityY);
           // calculate air resistance that acts opposite to particle velocity
           float fVelXopposite = (fVelocityX * (-1));
           float fVelYopposite = (fVelocityY * (-1));
           // x% of deceleration is applied (that is opposite to velocity)
           pParticle.getPhysicsHandler().setAcceleration(fVelXopposite * (50/100.0f), fVelYopposite * (75/100.0f));
       }
   });
   
   mSpdParticle = mSpdInitial;
   			
   			mEngine.getScene().attachChild(particleSystem);
           mEngine.getScene().sortChildren();
           
           //Remove the particles from the scene
           mEngine.registerUpdateHandler(new TimerHandler(mTimePart, new ITimerCallback() {
       @Override
       public void onTimePassed(final TimerHandler pTimerHandler) {
    	   mEngine.getScene().detachChild(particleSystem);
	   		mEngine.getScene().sortChildren();
	   		mEngine.getScene().unregisterUpdateHandler(pTimerHandler);
       }
   }));
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
		final float x = this.mX + (this.getWidth() / 2);
		final float y = this.mY + (this.getHeight() / 2);
		
		createParticleEffect(x, y);
		
		this.setVisible(false);
		/*IEntityModifierListener listener = new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            	createParticleEffect(x, y);
            }
           
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            	pItem.setAlpha(0.3f);
            	((Card)pItem).disable();
            }};

		this.registerEntityModifier(new SequenceEntityModifier(new JumpModifier(0.1f,this.mX, this.mX, this.mY,this.mY, 0, listener)));*/
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
		if(this.IsTurned() || isDisabled || !this.isVisible() || pSceneTouchEvent.isActionMove())
			return true;
		
		showFace();
		
		GameActivity.executeCardCalculation(this);
		
		return true;
	}
}
