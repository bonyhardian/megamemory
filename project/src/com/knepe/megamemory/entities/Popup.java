package com.knepe.megamemory.entities;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import com.knepe.megamemory.management.ResourceManager;
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
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

public class Popup extends Scene {
	private Font mFont;
	private Sprite mBackgroundSprite;
	private AnimatedSprite mFirstButton;
	private AnimatedSprite mSecondButton;
    private AnimatedSprite mThirdButton;
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
	
	public Popup(float pX, float pY, Scene pParentScene, Font font, Sprite backgroundSprite, AnimatedSprite pFirstButton, AnimatedSprite pSecondButton,AnimatedSprite pThirdButton, VertexBufferObjectManager pVertexBufferObjectManager, TextureRegion pPartTextureRegion, Sound pFireworkPopSound, boolean pSoundEnabled){
		super();
		mFont = font;
		mBackgroundSprite = backgroundSprite;
		mFirstButton = pFirstButton;
		mSecondButton = pSecondButton;
        mThirdButton = pThirdButton;
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
		mBackgroundSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.attachChild(mBackgroundSprite);
		
		mParentScene.setChildScene(this, false, true, true);
		
		IEntityModifierListener listener = new IEntityModifierListener() {
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            	AddToScene(texts);
                if(mPartTextureRegion != null){
                    createRandomParticleEffects();
                }
            }

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {
				// TODO Auto-generated method stub
				
			}};
		
		mBackgroundSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.15f, 1, 1.1f),new ScaleModifier(0.1f,1.1f, 0.9f), new ScaleModifier(0.1f,0.9f, 1, listener)));
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

        AddButtons();
	}

    private void AddButtons(){
        float y = mFirstButton == null ? (mBackgroundSprite.getY() + mBackgroundSprite.getHeight()) : (mBackgroundSprite.getY() + mBackgroundSprite.getHeight()) - (mFirstButton.getHeight() * 2.3f);
        if(mFirstButton != null){
            float x = mFirstButton.getWidth() == ResourceManager.getInstance().popup_button_region.getWidth() ? (mBackgroundSprite.getX() + mFirstButton.getWidth() * 0.25f) : (mBackgroundSprite.getWidth() / 2) - (mFirstButton.getWidth()) + (mFirstButton.getWidth() / 2);

            mFirstButton.setPosition(x, y);

            this.registerTouchArea(mFirstButton);
            this.attachChild(mFirstButton);
        }

        if(mSecondButton != null){
            float x = mFirstButton == null ? 0 : mSecondButton.getWidth() == ResourceManager.getInstance().popup_button_region.getWidth() ? (mFirstButton.getX() + (float) (mFirstButton.getWidth() * 1.2)) : (mFirstButton.getX() + (float) (mFirstButton.getWidth() * 1.5));

            mSecondButton.setPosition(x, y);

            this.registerTouchArea(mSecondButton);
            this.attachChild(mSecondButton);
        }

        if(mThirdButton != null){
            float x = mFirstButton.getX() + 20;
            y += mThirdButton.getHeight() + 20;

            mThirdButton.setPosition(x, y);

            this.registerTouchArea(mThirdButton);
            this.attachChild(mThirdButton);
        }
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
        particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0.3f, 1, 1, 0));
        final Random generator = new Random();

        //Init the particles with random in the angle
        particleSystem.addParticleInitializer(new IParticleInitializer<Sprite>() {
		    @Override
		    public void onInitializeParticle(Particle<Sprite> pParticle) {
		 	   //set random color
		 	   /*int red = (int) (Math.random() * 256);
		 	   int blue = (int) (Math.random() * 256);
		 	   int green = (int) (Math.random() * 256);*/
		 	   
		 	   //pParticle.getEntity().setColor(new Color(red, green, blue));
		        //Create particles between 225 - 315
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

