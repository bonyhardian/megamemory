package com.knepe.megamemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
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
	private Scene scene;
	public static Integer SelectedId_first = -1;
	public static Integer SelectedId_second = -1;
	private static Boolean mc_isfirst = false;
	public static ArrayList<Card> cards;
	public SparseArray<ITiledTextureRegion> cardMappings = new SparseArray<ITiledTextureRegion>();
	private TimerHud timer;
	private static TriesHud triesHud;
	private IUpdateHandler timerUpdateHandler;
	
	//RESOURCES - TEXTURE ATLAS
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mPopupTextureAtlas;
	private BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlas;
	protected ITextureRegion mBgTextureRegion;
	
	//RESOURCES - TEXTUREREGIONS
	private TextureRegion mTopHudTextureRegion;
	private ITiledTextureRegion mBirdTextureRegion;
	private ITiledTextureRegion mCowTextureRegion;
	private ITiledTextureRegion mFishTextureRegion;
	private ITiledTextureRegion mKillerWhaleTextureRegion;
	private ITiledTextureRegion mFoxTextureRegion;
	private ITiledTextureRegion mPenguinTextureRegion;
	private ITiledTextureRegion mRacoonTextureRegion;
	private ITiledTextureRegion mWhaleTextureRegion;
	
	private TextureRegion mPopupBackgroundTextureRegion;
	private Font mFont;
	private TextureRegion mBackgroundTextureRegion;
	
	private BitmapTextureAtlas mButtonTextureRegion;
	protected ITiledTextureRegion mAnimatedButtonTextureRegion;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	} 
	
	@Override
	public void onCreateResources() {
		
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1840,115, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mPopupTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 400,400, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		//CARDS
		this.mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/bird.png", 0, 0, 2, 1);
		this.mCowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/cow.png", 230, 0, 2, 1);
		this.mFishTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fish.png", 460, 0, 2, 1);
		this.mFoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/fox.png", 690, 0, 2, 1);
		this.mKillerWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/killerwhale.png", 920, 0, 2, 1);
		this.mPenguinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/penguin.png", 1150, 0, 2, 1);
		this.mRacoonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/racoon.png", 1380, 0, 2, 1);
		this.mWhaleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "gfx/animals/whale.png", 1610, 0, 2, 1);
		
		//BACKGROUND
		this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		this.mTopHudTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/top-hud.png");
		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTextureAtlas, this, "gfx/main-bg.jpg", 0, 0);
        this.mPopupBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPopupTextureAtlas, this, "gfx/popup-bg.png", 0, 0);
        
        this.mButtonTextureRegion = new BitmapTextureAtlas(this.getTextureManager(), 395, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mAnimatedButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mButtonTextureRegion, this, "gfx/btn/animbtn2.png", 0, 0, 2, 1);
		
		
        this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),"fonts/doctorsos.ttf", 34f, true, android.graphics.Color.WHITE);
		
        //LOAD
        this.mBackgroundTextureAtlas.load();
        this.mPopupTextureAtlas.load();
		this.mBitmapTextureAtlas.load();
		this.mButtonTextureRegion.load();
		this.mFont.load();
		try {
			this.mBuildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onCreateScene() {
		initScene();
		return scene;
	}
	
	private void initScene(){
		scene = new Scene();

		scene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager())));
		
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(checkIfFinished()){
					final IUpdateHandler that = this;
					runOnUpdateThread(new Runnable(){
						@Override
						public void run() {
							scene.unregisterUpdateHandler(that);
							finished();
						}
					});
					
				}
			}
		});
		
		gameStart(scene);
	}
	
	private void gameStart(final Scene scene){
		SelectedId_first = -1;
		SelectedId_second = -1;
		mc_isfirst = false;
		cards = new ArrayList<Card>();
		cardMappings = new SparseArray<ITiledTextureRegion>();
		timer = new TimerHud();
		triesHud = new TriesHud();
		timerUpdateHandler = null;
		
		mapCards();
		fillCards();
		addCardsToScene(scene);
    	initTimer(scene);
	}
	
	private void initTimer(Scene scene){
		scene.attachChild(new Sprite(20, 0, this.mTopHudTextureRegion, this.getVertexBufferObjectManager()));
		
		final Text elapsedText = new Text(CAMERA_WIDTH / 2, 0, this.mFont, "0:00", 300, this.getVertexBufferObjectManager());
		final Text timeText = new Text(CAMERA_WIDTH / 2 - 75, 0, this.mFont, "TIME: ", 6, this.getVertexBufferObjectManager());
		scene.attachChild(elapsedText);
		scene.attachChild(timeText);
		
		
		final Text triesText = new Text(40, 0, this.mFont, "TRIES: ", 7, this.getVertexBufferObjectManager());
		final Text triesCounterText = new Text(120, 0, this.mFont, "0", 300, this.getVertexBufferObjectManager());
		scene.attachChild(triesCounterText);
		scene.attachChild(triesText);
		
		timer = new TimerHud();
		triesHud = new TriesHud();
		
		timerUpdateHandler = new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                    elapsedText.setText(timer.getTime());
                    triesCounterText.setText(triesHud.getTries());
            }
		});
		
		scene.registerUpdateHandler(timerUpdateHandler);
	}
	
	@Override
	public void onGameCreated(){
		if(timer != null){
			timer.start();
		}
	}
	
	private void finished(){
		timer.stop();
		this.mEngine.getScene().unregisterUpdateHandler(this.timerUpdateHandler);
		//show scores, "popup" with "play again" and "back to menu"
		showFinishPopup();
	}
	
	private boolean checkIfFinished(){
		if(cards == null || cards.isEmpty())
		{
			return false;
		}
		
		for(Card card : cards){
			if(card.isVisible())
				return false;
		}
		
		return true;
	}
	
	private void showFinishPopup(){
		final Scene scene = this.mEngine.getScene();
		final float x = CAMERA_WIDTH / 2 - this.mPopupBackgroundTextureRegion.getWidth() / 2;
		final float y = CAMERA_HEIGHT / 2 - this.mPopupBackgroundTextureRegion.getHeight() / 2;
		Sprite popupBg = new Sprite(x, y, this.mPopupBackgroundTextureRegion, this.getVertexBufferObjectManager());
		PopupButton button = new PopupButton(0, 0, this.mAnimatedButtonTextureRegion.getWidth(), this.mAnimatedButtonTextureRegion.getHeight(), this.mAnimatedButtonTextureRegion, this.getVertexBufferObjectManager(), "Play again", this.mFont){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()){
					this.setCurrentTileIndex(1);
				}
				if(pSceneTouchEvent.isActionUp()){
					this.setCurrentTileIndex(0);
					GameActivity.this.reset();
				}
				
				return true;
          };
		};
		
		Popup popup = new Popup(x, y, scene, this.mFont, popupBg, button, this.getVertexBufferObjectManager());
		popup.Add("Nice!", "Your time: " + timer.getStopTime(this));
	}
	
	private void reset(){
	    	mEngine.runOnUpdateThread(new Runnable() 
	    	{
	    		 @Override
	             public void run() 
	    		 {
			    	scene.detachChildren();
			    	scene.reset();
			    	
			    	initScene();
					
			    	GameActivity.this.mEngine.setScene(null);
			    	GameActivity.this.mEngine.setScene(scene);
			    	timer.start();
	    		 }
	    	});
	}
	
	public static void executeCardCalculation(Card card){
		triesHud.increase();
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