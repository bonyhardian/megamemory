package com.knepe.megamemory;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
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
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.widget.Toast;

public class DifficultyActivity extends LayoutGameActivity implements IOnMenuItemClickListener {
	private final int CAMERA_WIDTH = 500;
	private final int CAMERA_HEIGHT = 800;

	protected static final int MENU_2x2 = 0;
	protected static final int MENU_4x4 = 1;
	protected static final int MENU_6x6 = 2;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	protected MenuScene mMenuScene;
	
	private boolean mSoundEnabled;

	protected ITiledTextureRegion mSoundToggleTextureRegion;
	private BitmapTextureAtlas mSoundToggleTextureAtlas;
	private BitmapTextureAtlas mMenuTexture;
	private BitmapTextureAtlas mBgTexture;
	protected ITiledTextureRegion mMenuButtonTextureRegion;
	protected ITextureRegion mBgTextureRegion;
	private BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlas;
	private Font mFont;
	private Sound mClickSound;
	
	@Override protected void onCreate(android.os.Bundle pSavedInstanceState) 
	{
		super.onCreate(pSavedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	};
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions en = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		en.getAudioOptions().setNeedsSound(true);
		return en;
	}
 
	@Override
	public void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		
		this.mBgTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgTexture, this, "gfx/main-bg.jpg", 0, 0);
		this.mBgTexture.load(); 
  
		this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(), 395, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mMenuTexture, this, "gfx/btn/animbtn2.png", 0, 0, 2, 1);
		this.mMenuTexture.load();
		
		this.mSoundToggleTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 138, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mSoundToggleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mSoundToggleTextureAtlas, this, "gfx/btn/togglesound-btn.png", 0, 0, 2, 1);
		this.mSoundToggleTextureAtlas.load();
		
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),"fonts/PoetsenOne.ttf", 30f, true, android.graphics.Color.WHITE);
		this.mFont.load();
		
		try {
			this.mBuildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		//load sounds
		try {
			this.mClickSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "snd/click.wav");
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	   startActivity(new Intent(DifficultyActivity.this, ThemeActivity.class));
	}

	@Override
	public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		this.createMenuScene();
		
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBgTextureRegion, this.getVertexBufferObjectManager())));

		this.mMainScene.setChildScene(this.mMenuScene, false, false, false);
		
		pOnCreateSceneCallback.onCreateSceneFinished(this.mMainScene);
	}
	
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	 
	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}
	 
	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.SurfaceViewId;
	}
	
	private void setDifficulty(int difficulty){
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("difficulty", difficulty);

		editor.commit();
	}
	
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		((AnimatedSpriteMenuItem)pMenuItem).setCurrentTileIndex(1);
		
		if(this.mSoundEnabled){
			mClickSound.play();
		}
		
		if(pMenuItem.getID() == MENU_6x6){
			runOnUiThread(new Runnable() {
		        @Override
		        public void run() {
		           Toast.makeText(DifficultyActivity.this, "Please buy the full game to unlock this feature!", Toast.LENGTH_SHORT).show();
		        }
		    });
			return false;
		}
		setDifficulty(pMenuItem.getID());
		startActivity(new Intent(DifficultyActivity.this, GameActivity.class));
		this.finish();
		return true;
	}
	
	private void createSoundToggleButton(){
		getSoundPreference();
		
		final AnimatedSprite button = new AnimatedSprite(this.CAMERA_WIDTH - (this.mSoundToggleTextureRegion.getWidth() + 5), 5, this.mSoundToggleTextureRegion, this.getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					DifficultyActivity.this.mSoundEnabled = !DifficultyActivity.this.mSoundEnabled;
					
					setSoundPreference();
					
					if(DifficultyActivity.this.mSoundEnabled){
						this.setCurrentTileIndex(0);
					}
					else{
						this.setCurrentTileIndex(1);
					}
				}
				
				return true;
          };
		};
		
		if(this.mSoundEnabled){
			button.setCurrentTileIndex(0);
		}
		else{
			button.setCurrentTileIndex(1);
		}
		this.mMenuScene.registerTouchArea(button);
		this.mMenuScene.attachChild(button);
	}
	
	private void setSoundPreference(){
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("soundEnabled", mSoundEnabled);

		// Commit the edits!
		editor.commit();
	}
	
	private void getSoundPreference(){
		// Restore preferences
       SharedPreferences settings = getSharedPreferences("preferences", 0);
       boolean soundEnabled = settings.getBoolean("soundEnabled", true);
       this.mSoundEnabled = soundEnabled;
	}
	
	private void createButtonWithText(String text, int pId){
		final AnimatedSpriteMenuItem button = new AnimatedSpriteMenuItem(pId, this.mMenuButtonTextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public void onSelected()
			{
				setCurrentTileIndex(1);
				super.onSelected();
			}

			@Override
			public void onUnselected()
			{
				setCurrentTileIndex(0);
				super.onUnselected();
			}
		};
		
		button.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		this.mMenuScene.addMenuItem(button);

		Text btnText = new Text(0,0, mFont, text, this.getVertexBufferObjectManager());
		btnText.setPosition(((button.getWidth() / 2) - btnText.getWidth() / 2), (button.getHeight() / 2) - 20);
		button.attachChild(btnText);
	}
	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.mCamera);

		createButtonWithText(this.getString(R.string.difficulty_2x2), MENU_2x2);
		createButtonWithText(this.getString(R.string.difficulty_4x4), MENU_4x4);
		createButtonWithText(this.getString(R.string.difficulty_6x6), MENU_6x6);
		createSoundToggleButton();
		
		//SlideMenuAnimator slideanim = new SlideMenuAnimator(30);
        
        //this.mMenuScene.setMenuAnimator(slideanim);
		this.mMenuScene.buildAnimations();
		this.mMenuScene.setBackgroundEnabled(false);
		
		this.mMenuScene.setOnMenuItemClickListener(this);
		this.mMenuScene.clearUpdateHandlers();
	}
}