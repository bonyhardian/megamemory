package com.knepe.megamemory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
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
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;


public class GameActivity extends SimpleBaseGameActivity {

	
	//CONSTANTS
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
	private static Object lock = new Object();
	private static int NUM_ROWS = 4;
	private static int NUM_COLS = 4;
	private static int THEME = -1;
	private static int DIFFICULTY = 0;
	private static final int TEXTUREPACK_CARDS = 18;
	private static int mScore = 0;
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
	
	private TexturePackTextureRegionLibrary texturePackLibrary;
	private TexturePack texturePack;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mPopupTextureAtlas;
	private BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlas;
	protected ITextureRegion mBgTextureRegion;
	
	//RESOURCES - TEXTUREREGIONS
	private TextureRegion mTopHudTextureRegion;
	private ArrayList<ITiledTextureRegion> mCardTextureRegions = new ArrayList<ITiledTextureRegion>();
	
	private TextureRegion mPartTextureRegion;
	
	private TextureRegion mPopupBackgroundTextureRegion;
	private Font mFont;
	private TextureRegion mBackgroundTextureRegion;
	
	private BitmapTextureAtlas mButtonTextureRegion;
	private ITiledTextureRegion mHomeIconTextureRegion;
	private ITiledTextureRegion mRetryIconTextureRegion;
	private BitmapTextureAtlas mParticleTextureAtlas;
	
	//SOUNDS
	private static boolean mSoundEnabled;
	private static Sound mTurnCardSound;
	private static Sound mCorrectSound;
	private static Sound mFinishedSound;
	private Sound mFireworkPopSound;
	
	@Override protected void onCreate(android.os.Bundle pSavedInstanceState) 
	{
		super.onCreate(pSavedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	};
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions en = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		en.getAudioOptions().setNeedsSound(true);
		return en;
	} 
	
	private void setSoundPreference(){
		// Restore preferences
       SharedPreferences settings = getSharedPreferences("preferences", 0);
       boolean soundEnabled = settings.getBoolean("soundEnabled", true);
       mSoundEnabled = soundEnabled;
	}
	
	@Override
	public void onCreateResources() {
		setSoundPreference();
		initLayout();
		initTheme();
		loadCardResources();
		
		this.mBackgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mPopupTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 400,400, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mParticleTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 16, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		//BACKGROUND
		this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		this.mTopHudTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/top-hud.png");
		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTextureAtlas, this, "gfx/main-bg.jpg", 0, 0);
        this.mPopupBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPopupTextureAtlas, this, "gfx/popup-bg.png", 0, 0);
        
        this.mButtonTextureRegion = new BitmapTextureAtlas(this.getTextureManager(), 276, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mHomeIconTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mButtonTextureRegion, this, "gfx/btn/home-btn.png", 0, 0, 2, 1);
		this.mRetryIconTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mButtonTextureRegion, this, "gfx/btn/retry-btn.png", 138, 0, 2, 1);
		
        this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),"fonts/doctorsos.ttf", 34f, true, android.graphics.Color.WHITE);
		
        //PARTICLE
        this.mPartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mParticleTextureAtlas, this, "gfx/particle.png", 0, 0);
        
        //LOAD
        this.mBackgroundTextureAtlas.load();
        this.mPopupTextureAtlas.load();
		this.mButtonTextureRegion.load();
		this.mParticleTextureAtlas.load();
		this.mFont.load();
		try {
			this.mBuildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		//load sounds
		try {
			mTurnCardSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "snd/turn-card.wav");
			mCorrectSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "snd/correct.wav");
			mFinishedSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "snd/complete.wav");
			mFireworkPopSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "snd/firework-pop.wav");
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
	}
	
	private void loadCardResources(){
		String assetsPath = "gfx/sprites/" + ThemeManager.getThemeString(THEME) + "/" + DifficultyManager.getLayoutString(DIFFICULTY) + "/";
		try 
	    {
	        texturePack = new TexturePackLoader(getTextureManager(), assetsPath).loadFromAsset(getAssets(), "texture.xml");
	        texturePack.loadTexture();
	        texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
	    } 
	    catch (final TexturePackParseException e) 
	    {
	        Debug.e(e);
	    }

		for(int i = 0; i < TEXTUREPACK_CARDS; i++){
			ITiledTextureRegion textureRegion = texturePackLibrary.get(i, 2, 1);
			mCardTextureRegions.add(textureRegion);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		initScene();
		return scene;
	}
	
	private void initLayout(){
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		DIFFICULTY = settings.getInt("difficulty", 0);
		switch(DIFFICULTY){
	       	case 0:
	       		NUM_COLS = 2;
	       		NUM_ROWS = 2;
	       		break;
	       	case 1:
	       		NUM_COLS = 4;
	       		NUM_ROWS = 4;
	       		break;
	       	case 2:
	       		NUM_COLS = 6;
	       		NUM_ROWS = 6;
	       		break;
	       	default:
	       		NUM_COLS = 2;
	       		NUM_ROWS = 2;
	       		break;
	       }
	}
	
	private void initTheme(){
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		THEME = settings.getInt("theme", 0);
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
		mScore = 0;
		
		mapCards();
		fillCards();
		addCardsToScene(scene);
    	initTimer(scene);
	}
	
	private void initTimer(Scene scene){
		scene.attachChild(new Sprite(40, CAMERA_HEIGHT - this.mTopHudTextureRegion.getHeight(), this.mTopHudTextureRegion, this.getVertexBufferObjectManager()));
		
		final Text triesText = new Text(100, CAMERA_HEIGHT - (this.mTopHudTextureRegion.getHeight() - 5), this.mFont, "TRIES: ", 7, this.getVertexBufferObjectManager());
		final Text triesCounterText = new Text(180, CAMERA_HEIGHT - (this.mTopHudTextureRegion.getHeight() - 5), this.mFont, "0", 300, this.getVertexBufferObjectManager());
		scene.attachChild(triesCounterText);
		scene.attachChild(triesText);
		
		final Text elapsedText = new Text(335, CAMERA_HEIGHT - (this.mTopHudTextureRegion.getHeight() - 5), this.mFont, "0:00", 300, this.getVertexBufferObjectManager());
		final Text timeText = new Text(260, CAMERA_HEIGHT - (this.mTopHudTextureRegion.getHeight() - 5), this.mFont, "TIME: ", 6, this.getVertexBufferObjectManager());
		scene.attachChild(elapsedText);
		scene.attachChild(timeText);
		
		
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
		if(mSoundEnabled){
			mFinishedSound.play();
		}
		
		final Scene scene = this.mEngine.getScene();
		final float x = CAMERA_WIDTH / 2 - this.mPopupBackgroundTextureRegion.getWidth() / 2;
		final float y = CAMERA_HEIGHT / 2 - this.mPopupBackgroundTextureRegion.getHeight() / 2;
		Sprite popupBg = new Sprite(x, y, this.mPopupBackgroundTextureRegion, this.getVertexBufferObjectManager()){
			@Override
                protected void preDraw(GLState pGLState, Camera pCamera) {
		            super.preDraw(pGLState, pCamera);
		            //to prevent "banding" on gradient
		            pGLState.enableDither();
				}
			};
		AnimatedSprite homeButton = new AnimatedSprite(0, 0, this.mHomeIconTextureRegion, this.getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()){
					this.setCurrentTileIndex(1);
				}
				else if(pSceneTouchEvent.isActionUp()){
					this.setCurrentTileIndex(0);
					startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
					GameActivity.this.finish();
				}
				else{
					this.setCurrentTileIndex(0);
				}
				
				return true;
          };
		};
		AnimatedSprite retryButton = new AnimatedSprite(0, 0, this.mRetryIconTextureRegion, this.getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()){
					this.setCurrentTileIndex(1);
				}
				else if(pSceneTouchEvent.isActionUp()){
					this.setCurrentTileIndex(0);
					GameActivity.this.reset();
				}
				else{
					this.setCurrentTileIndex(0);
				}
				
				return true;
          };
		};
		
		Popup popup = new Popup(x, y, scene, this.mFont, popupBg, homeButton, retryButton, this.getVertexBufferObjectManager(), this.mPartTextureRegion, mFireworkPopSound, mSoundEnabled);
		popup.Add("SCORE", "Time: " + timer.getStopTime(this), "Tries: " + triesHud.getTries(), "Score: " + getScore());
		
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	   startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
	}
	
	private String getScore(){
		double time = timer.getStopSeconds();
		double tries = triesHud.getTriesNumber();
		
		int score = (int) Math.round(mScore - (time + tries));
		
		return "" + score;
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
		if(mSoundEnabled){
			mTurnCardSound.play();
		}
		
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
				
				if(mSoundEnabled){
					mCorrectSound.play();
				}
				
				cards.get(SelectedId_first).hide();
				cards.get(SelectedId_second).hide();
				mScore += 30 * (DIFFICULTY + 1);
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
		int startX = DifficultyManager.getLayoutStartX(DIFFICULTY);
		int startY = 265;
		for(Card card : cards){
			if(rowCount == NUM_ROWS)
				break;

			float x = (float) (card.getWidth() * colCount) + startX;
			float y = (float)(card.getHeight() * rowCount) + startY;
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
		Collections.shuffle(mCardTextureRegions, new Random(System.nanoTime()));
		int numberOfCards = (NUM_COLS * NUM_ROWS) / 2;
		for(int i = 0; i < numberOfCards; i++){
			cardMappings.put(i, mCardTextureRegions.get(i));
		}
	}
	
	private void fillCards(){
		cards = new ArrayList<Card>();
		
		for(int i = 0; i < (NUM_COLS * NUM_ROWS) / 2;i++){
			int cardId = i;
			Card card1 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId, mPartTextureRegion, this.mEngine);
			Card card2 = new Card(0, 0, cardMappings.get(i), this.getVertexBufferObjectManager(), cardId, mPartTextureRegion, this.mEngine);
			cards.add(card1);
			cards.add(card2);
		}
		
		Collections.shuffle(cards, new Random(System.nanoTime()));
	}
}