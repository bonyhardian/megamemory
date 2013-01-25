package com.knepe.megamemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import android.util.Log;
import android.util.SparseArray;


public class GameActivity extends SimpleBaseGameActivity {

	
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
	public static Integer SelectedId_first = -1;
	public static Integer SelectedId_second = -1;
	private static Object lock = new Object();
	private static Boolean mc_isfirst = false;
	private static final int NUM_ROWS = 4;
	private static final int NUM_COLS = 4;
	public static Scene mScene;
	private static Font mFont;
	private static Text mFinishText;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mEffectsTextureAtlas;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	
	private TextureRegion mBackgroundTextureRegion;
	private ITiledTextureRegion mBirdTextureRegion;
	private ITiledTextureRegion mCowTextureRegion;
	private ITiledTextureRegion mFishTextureRegion;
	private ITiledTextureRegion mKillerWhaleTextureRegion;
	private ITiledTextureRegion mFoxTextureRegion;
	private ITiledTextureRegion mPenguinTextureRegion;
	private ITiledTextureRegion mRacoonTextureRegion;
	private ITiledTextureRegion mWhaleTextureRegion;
	private ITiledTextureRegion mCorrectFxTextureRegion;
	
	public static ArrayList<Card> cards;
	public SparseArray<ITiledTextureRegion> cardMappings = new SparseArray<ITiledTextureRegion>();

	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	@Override
	public void onCreateResources() {
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1600,105, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mEffectsTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800,124, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/bird.png", 0, 0, 2, 1);
		this.mCowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/cow.png", 200, 0, 2, 1);
		this.mFishTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fish.png", 400, 0, 2, 1);
		this.mFoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fox.png", 600, 0, 2, 1);
		this.mKillerWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/killerwhale.png", 800, 0, 2, 1);
		this.mPenguinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/penguin.png", 1000, 0, 2, 1);
		this.mRacoonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/racoon.png", 1200, 0, 2, 1);
		this.mWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/whale.png", 1400, 0, 2, 1);
		
		this.mCorrectFxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mEffectsTextureAtlas, this, "gfx/correctfx.png", 0, 0, 9, 1);
		
		this.mBackgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 768, TextureOptions.DEFAULT);
        this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTextureAtlas, this, "gfx/bg.jpg", 0, 0);
        this.mBackgroundTextureAtlas.load();
		this.mBitmapTextureAtlas.load();
		this.mEffectsTextureAtlas.load();

        GameActivity.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
        	    "fonts/GluttonMan.ttf", 46, true, android.graphics.Color.BLACK);
        GameActivity.mFont.load();
        
        
	}

	@Override
	public Scene onCreateScene() {
		final Scene scene = new Scene();

		SpriteBackground bg = new SpriteBackground(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager()));
        scene.setBackground(bg);
		
        /*final Text elapsedText = new Text(0, 0, this.mFont, "Seconds elapsed:", 1000, this.getVertexBufferObjectManager());
        scene.attachChild(elapsedText);
        
        scene.registerUpdateHandler(new TimerHandler(0.02f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                    elapsedText.setText("Seconds elapsed: " + GameActivity.this.mEngine.getSecondsElapsedTotal());
            }
        }));*/
        
		mapCards();
		fillCards();
		addCardsToScene(scene);
		
		mScene = scene;
		
		mFinishText = new Text(0, 0, mFont, "Good Job!", this.getVertexBufferObjectManager());
		mFinishText.setPosition(CAMERA_WIDTH/2 - (mFinishText.getWidth()/2), CAMERA_HEIGHT/2 - (mFinishText.getHeight()/2));
		mFinishText.setVisible(false);
		scene.attachChild(mFinishText);
        
		
		return scene;
	}
	
	private static boolean checkIfCompleted(){
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
						if(checkIfCompleted()){
						  mFinishText.setVisible(true);
						}
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
			Card card1 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId, new AnimatedSprite(0,0,mCorrectFxTextureRegion, this.getVertexBufferObjectManager()));
			Card card2 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId, new AnimatedSprite(0,0,mCorrectFxTextureRegion, this.getVertexBufferObjectManager()));
			cards.add(card1);
			cards.add(card2);
		}

		Collections.shuffle(cards);
	}
}