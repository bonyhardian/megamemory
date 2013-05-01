package com.knepe.megamemory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.sound.Sound;
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
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

public class Popup extends Scene {
	private Font mFont;
	private Sprite mBackgroundSprite;
	private AnimatedSprite mIconButtonHome;
	private AnimatedSprite mIconButtonAction;
	private VertexBufferObjectManager mVertextBufferObjectManager;
	private float x;
	private float y;
	private Scene mParentScene;
	
	
	 //Number of particles to show
   private int mNumPart = 20;
   //Time to show the particles
   private int mTimePart = 1;
   //Speed of the particles
   private float mSpdInitial = 200.0f;
   private float mSpdParticle = mSpdInitial;
   private float mSpdIncr = 125.0f/mNumPart;
   private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
   private Sound mFireworkPopSound;
   private boolean mSoundEnabled;
   
   private TextureRegion mPartTextureRegion;
	
	public Popup(float pX, float pY, Scene pParentScene, Font font, Sprite backgroundSprite, AnimatedSprite pIconButtonHome, AnimatedSprite pIconButtonAction, VertexBufferObjectManager pVertexBufferObjectManager, TextureRegion pPartTextureRegion, Sound pFireworkPopSound, boolean pSoundEnabled){
		super();
		mFont = font;
		mBackgroundSprite = backgroundSprite;
		mIconButtonHome = pIconButtonHome;
		mIconButtonAction = pIconButtonAction;
		mVertextBufferObjectManager = pVertexBufferObjectManager;
		x = pX;
		y = pY;
		mParentScene = pParentScene;
		mPartTextureRegion = pPartTextureRegion;
		mFireworkPopSound = pFireworkPopSound;
		mSoundEnabled = pSoundEnabled;
	}
	
	public void Add(final Object... texts){
		this.setBackgroundEnabled(false);
		mBackgroundSprite.setPosition(x, y);
		mBackgroundSprite.setScale(0);
		mBackgroundSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.attachChild(mBackgroundSprite);
		
		mParentScene.setChildScene(this, false, true, true);
		
		IEntityModifierListener listener = new IEntityModifierListener() {
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            	AddToScene(texts);
            	createRandomParticleEffects();
            }

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {
				// TODO Auto-generated method stub
				
			}};
		
		mBackgroundSprite.registerEntityModifier(
				new SequenceEntityModifier(new ScaleModifier(0.4f, 0, 1, listener)));
	}
	
	private void AddToScene(Object... texts){
		int index = 0;
		
		for(Object text : texts){
			if(index == 0){
				AddHeader(text.toString());
			}
			else{
				AddText(text.toString(), index);	
			}
			
			index++;
		}
		
		mIconButtonHome.setPosition((mBackgroundSprite.getWidth() / 2) - (mIconButtonHome.getWidth()) + (mIconButtonHome.getWidth() / 2), (mBackgroundSprite.getY() + mBackgroundSprite.getHeight()) - (mIconButtonHome.getHeight() * 2));
		this.registerTouchArea(mIconButtonHome);
		this.attachChild(mIconButtonHome);
		
		mIconButtonAction.setPosition(mIconButtonHome.getX() + (float)(mIconButtonHome.getWidth() * 1.5), (mBackgroundSprite.getY() + mBackgroundSprite.getHeight()) - (mIconButtonHome.getHeight() * 2));
		this.registerTouchArea(mIconButtonAction);
		this.attachChild(mIconButtonAction);
	}
	
	private void AddHeader(String text){
		Text txt = new Text(0,0, mFont, text, mVertextBufferObjectManager);
		txt.setPosition((mBackgroundSprite.getX() + 40), mBackgroundSprite.getY() + 10);
		this.attachChild(txt);
	}
	
	private void AddText(String text, int index){
		Text txt = new Text(0,0, mFont, text, mVertextBufferObjectManager);
		txt.setPosition((mBackgroundSprite.getX() + 40), mBackgroundSprite.getY() + ((txt.getHeight() * index) + txt.getHeight()));
		this.attachChild(txt);
	}
	
	private void createParticleEffect(float posX, float posY) {
        //Init the particle system
        final SpriteParticleSystem  particleSystem = new SpriteParticleSystem(new PointParticleEmitter(posX, posY), (float)60*mNumPart, (float)60*mNumPart, mNumPart, mPartTextureRegion, mVertextBufferObjectManager);
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
			
		this.attachChild(particleSystem);
		this.sortChildren();
        
		if(mSoundEnabled){
			this.mFireworkPopSound.play();	
		}
		
        //Remove the particles from the scene
        this.registerUpdateHandler(new TimerHandler(mTimePart, new ITimerCallback() {
		    @Override
		    public void onTimePassed(final TimerHandler pTimerHandler) {
		   		Popup.this.detachChild(particleSystem);
		 	   	Popup.this.sortChildren();
		 	   	Popup.this.unregisterUpdateHandler(pTimerHandler);
		    }
        }));
	}
	
	private void createRandomParticleEffects(){
		final Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {
			private int counter = 0;
			@Override
			public void run() {
				try{
					int x = (int) (MathUtils.random(20, CAMERA_WIDTH - 20));
					int y = (int) (MathUtils.random(20, CAMERA_HEIGHT - 100));
					createParticleEffect(x, y);
					
					 if(++counter == 7 && t != null) {
			            t.cancel();
					 }
				}
				catch (Exception e) {
				}
			}
		};
		
	  t.schedule(tt, 0, 500);
	}
}

