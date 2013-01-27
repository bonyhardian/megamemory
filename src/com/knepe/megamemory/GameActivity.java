package com.knepe.megamemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;

import android.util.Log;
import android.util.SparseArray;


public class GameActivity extends SimpleBaseGameActivity {

	
	//CONSTANTS
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
	private final boolean DEBUG = true;
	private static Object lock = new Object();
	private static final int NUM_ROWS = 4;
	private static final int NUM_COLS = 4;
	
	//PROPERTIES
	public static Integer SelectedId_first = -1;
	public static Integer SelectedId_second = -1;
	private static Boolean mc_isfirst = false;
	public static ArrayList<Card> cards;
	public SparseArray<ITiledTextureRegion> cardMappings = new SparseArray<ITiledTextureRegion>();
	
	//RESOURCES - TEXTURE ATLAS
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mTextTextureAtlas;
	
	//RESOURCES - TEXTUREREGIONS
	private TextureRegion mBackgroundTextureRegion;
	private ITiledTextureRegion mBirdTextureRegion;
	private ITiledTextureRegion mCowTextureRegion;
	private ITiledTextureRegion mFishTextureRegion;
	private ITiledTextureRegion mKillerWhaleTextureRegion;
	private ITiledTextureRegion mFoxTextureRegion;
	private ITiledTextureRegion mPenguinTextureRegion;
	private ITiledTextureRegion mRacoonTextureRegion;
	private ITiledTextureRegion mWhaleTextureRegion;
	private TextureRegion mLetsGoTextureRegion;
	private TextureRegion mGoodJobTextureRegion;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	} 
	
	@Override
	public void onCreateResources() {
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1600,105, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTextTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 630,68, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 800, TextureOptions.DEFAULT);
		
		//CARDS
		this.mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/bird.png", 0, 0, 2, 1);
		this.mCowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/cow.png", 200, 0, 2, 1);
		this.mFishTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fish.png", 400, 0, 2, 1);
		this.mFoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fox.png", 600, 0, 2, 1);
		this.mKillerWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/killerwhale.png", 800, 0, 2, 1);
		this.mPenguinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/penguin.png", 1000, 0, 2, 1);
		this.mRacoonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/racoon.png", 1200, 0, 2, 1);
		this.mWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/whale.png", 1400, 0, 2, 1);
		
		//TEXT
		this.mLetsGoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextTextureAtlas, this, "gfx/txt/letsgo.png", 0, 0);
		this.mGoodJobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextTextureAtlas, this, "gfx/txt/goodjob.png", 315, 0);
		
		//BACKGROUND
        this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTextureAtlas, this, "gfx/bg.jpg", 0, 0);
        
        //LOAD
        this.mBackgroundTextureAtlas.load();
		this.mBitmapTextureAtlas.load();
		this.mTextTextureAtlas.load();  
	}

	@Override
	public Scene onCreateScene() {
		final Scene scene = new Scene();

		SpriteBackground bg = new SpriteBackground(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager()));
        scene.setBackground(bg);
        
		gameStart(scene);
		
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(checkIfFinished()){
				  finished();
				  scene.unregisterUpdateHandler(this);
				}
			}
		});
		
		
		return scene;
	}
	
	private void gameStart(final Scene scene){
		mapCards();
		fillCards();
		
		Sprite letsGoSprite = new Sprite(CAMERA_WIDTH/2 - (mLetsGoTextureRegion.getWidth()/2), CAMERA_HEIGHT/2 - (mLetsGoTextureRegion.getHeight()/2), mLetsGoTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(letsGoSprite);
		
		final IEntityModifier modifier = new SequenceEntityModifier(
				new ScaleModifier(0.5f, 0, 1),
				new DelayModifier(2),
				new ScaleModifier(0.5f, 1, 0, new IEntityModifierListener() {
		            @Override
		            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
		            }
		           
		            @Override
		            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
		            	addCardsToScene(scene);
		            }}));
		modifier.setAutoUnregisterWhenFinished(true);
		letsGoSprite.registerEntityModifier(modifier);
		
		//INIT SCORES, TIME etc
	}
	
	private void finished(){
		Sprite goodJobSprite = new Sprite(CAMERA_WIDTH/2 - (mGoodJobTextureRegion.getWidth()/2), CAMERA_HEIGHT/2 - (mGoodJobTextureRegion.getHeight()/2), mGoodJobTextureRegion, this.getVertexBufferObjectManager());
		
		mEngine.getScene().attachChild(goodJobSprite);
		
		final IEntityModifier modifier = new SequenceEntityModifier(
				new ScaleModifier(0.5f, 0, 1),
				new DelayModifier(2),
				new ScaleModifier(0.5f, 1, 0));
		modifier.setAutoUnregisterWhenFinished(true);
		goodJobSprite.registerEntityModifier(modifier);
		
	}
	
	private boolean checkIfFinished(){
		for(Card card : cards){
			if(card.isVisible())
				return false;
		}
		
		return true;
	}
	
	public static void executeCardCalculation(Card card){
		//disable all cards
		disableCards();
		
		mc_isfirst = !mc_isfirst;
		
		int id = cards.indexOf(card);
		
		if (mc_isfirst) {
			SelectedId_first = id;
			
			enableCards();

		} else {
			SelectedId_second = id;

			playMove();
		}
	}
	
	private static void checkCards(){
		if(cards.get(SelectedId_first).Match(cards.get(SelectedId_second))){
			//correct
			if(cards.get(SelectedId_first).IsTurned() && cards.get(SelectedId_second).IsTurned()){
				cards.get(SelectedId_first).hide();
				cards.get(SelectedId_second).hide();
				
				enableCards();
				
				return;
			}
		}
		
		//incorrect
		for(int y=0;y < cards.size(); y++){
			cards.get(y).enable();
			if(cards.get(y).IsTurned()){
				cards.get(y).showBack();
			}
		}
	}
	
	private static void disableCards(){
		for(Card c : cards){
			c.disable();
		}
	}
	
	private static void enableCards(){
		for(Card c : cards){
			c.enable();
		}
	}
	private static void playMove(){
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				try{
					synchronized (lock) {
						checkCards();
					}
				}
				catch (Exception e) {
					Log.e("E1", e.getMessage());
				}
			}
		};
		
	  Timer t = new Timer(false);
	  t.schedule(tt, 1200);
	}
	
	private void addCardsToScene(Scene scene){
		int rowCount = 0;
		int colCount = 0;
		
		for(Card card : cards){
			if(rowCount == NUM_ROWS)
				break;

			float x = (float) ((75 * colCount) * 1.5) + 15;
			float y = (float)((75 * rowCount) * 1.5) + 100;
			card.setX(x);
			card.setY(y);
			card.addToScene(scene);
			colCount++;
			
			if(colCount == NUM_COLS){
				rowCount++;
				colCount = 0;
			}
		}
	}
	private void mapCards(){
		cardMappings.put(0, mBirdTextureRegion);
		cardMappings.put(1, mCowTextureRegion);
		cardMappings.put(2, mFishTextureRegion);
		cardMappings.put(3, mFoxTextureRegion);
		cardMappings.put(4, mKillerWhaleTextureRegion);
		cardMappings.put(5, mPenguinTextureRegion);
		cardMappings.put(6, mRacoonTextureRegion);
		cardMappings.put(7, mWhaleTextureRegion);
	}
	
	private void fillCards(){
		cards = new ArrayList<Card>();
		
		for(int i = 0; i < (NUM_COLS * NUM_ROWS) / 2;i++){
			int cardId = i;
			Card card1 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId);
			Card card2 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId);
			cards.add(card1);
			cards.add(card2);
		}
		
		if(DEBUG == false){
			Collections.shuffle(cards);
		}
	}
}