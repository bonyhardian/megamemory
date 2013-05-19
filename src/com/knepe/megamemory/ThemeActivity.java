package com.knepe.megamemory;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.util.modifier.ease.EaseBackOut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
   
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.texture.region.TextureRegion;

import com.knepe.megamemory.ScrollScene.IOnScrollScenePageListener;
import com.knepe.megamemory.ScrollScene.ScrollState;

public class ThemeActivity extends LayoutGameActivity implements IOnScrollScenePageListener {
		private final int CAMERA_WIDTH = 500;
		private final int CAMERA_HEIGHT = 800;
	
		protected static final int MENU_ANIMALS = 0;
		protected static final int MENU_FRUITS = 1;
		protected static final int MENU_SHAPES = 2;
		protected static final int MENU_VEHICLES = 3;
		protected static final int MENU_MAKEUP = 4;
		protected static final int MENU_NUMBERS = 5;
		
		protected static final int TOP_PADDING = 190;
		protected Camera mCamera;
	
		protected ScrollScene mScene;
	
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
 
        protected static int FONT_SIZE = 24;
        protected static int PADDING = 50;
        
        protected static int MENUITEMS = 6;
         
        private List<TextureRegion> columns = new ArrayList<TextureRegion>();

        // ===========================================================
    // Constructors
    // ===========================================================
 
        // ===========================================================
    // Getter & Setter
    // ===========================================================
 
        // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
 
        @Override protected void onCreate(android.os.Bundle pSavedInstanceState) 
    	{
    		super.onCreate(pSavedInstanceState);
    		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    	};
    	
        @Override
		public void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
                
        	this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
    		
    		this.mBgTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    		this.mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgTexture, this, "gfx/main-bg.jpg", 0, 0);
		this.mBgTexture.load(); 
  
		//Images for the menu
        for (int i = 0; i < MENUITEMS; i++) {				
        	BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    		ITextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, this, "gfx/theme-menu/menu"+i+".png", 0, 0);
        	
        	this.mEngine.getTextureManager().loadTexture(mMenuBitmapTextureAtlas);
        	columns.add((TextureRegion) mMenuTextureRegion);
        	
        	
        }
        
        this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(), 128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mEngine.getTextureManager().loadTexture(mMenuTexture); 
		
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
		public EngineOptions onCreateEngineOptions() {
                this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
                final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
    			//engineOptions.getTouchOptions().setNeedsMultiTouch(true);
    			engineOptions.getAudioOptions().setNeedsSound(true);
    			return engineOptions;
        }
 
        @Override
		public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        	this.mScene = new ScrollScene(CAMERA_WIDTH, CAMERA_HEIGHT);
    		//the offset represents how much the layers overlap
        	this.mScene.setOffset(((ITextureRegion)columns.get(0)).getWidth() / 2);
			this.mScene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBgTextureRegion, this.getVertexBufferObjectManager())));
	
			CreateMenuBoxes();
			createSoundToggleButton();
			
			//change ease function test
    		this.mScene.setEaseFunction(EaseBackOut.getInstance());

    		this.mScene.registerScrollScenePageListener(this);
    		this.mScene.setTouchAreaBindingOnActionDownEnabled(true);
    		
    		pOnCreateSceneCallback.onCreateSceneFinished(this.mScene);
 
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
 
        // ===========================================================
    // Methods
    // ===========================================================
    
    private void CreateMenuBoxes() {
    	 //current item counter
         int iItem = 0;

    	 for (int x = 0; x < columns.size(); x++) {
    		 
    		 //On Touch, save the clicked item in case it's a click and not a scroll.
             final int itemToLoad = iItem;
    		 final ITextureRegion textureRegion = (ITextureRegion)columns.get(x);
    		 
             /* Calculate the coordinates for the face, so its centered on the camera. */
      		final float centerX = (this.mScene.getPageWidth() - textureRegion.getWidth()) / 2;
      		final float centerY = (this.mScene.getPageHeight() - textureRegion.getHeight()) / 2;
      		
      		final Sprite sprite = new Sprite(centerX, centerY, textureRegion, this.getVertexBufferObjectManager()){
      			@Override
      	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
      				if(pSceneTouchEvent.isActionUp() && mScene.mState == ScrollState.IDLE){
      					loadLevel(itemToLoad);
      				}
      				else{
  						mScene.onSceneTouchEvent(mScene, pSceneTouchEvent);
      				}
      				
      				return true;
                }
    		};
      		
    		 iItem++;
    		
    		 Rectangle page = new Rectangle(0, 0, 0, 0, this.getVertexBufferObjectManager());
    		 page.attachChild(sprite);     
    		 
    		 this.mScene.registerTouchArea(sprite); 
    		 
    		 this.mScene.addPage(page);
		}
    }
    
    
    private void createSoundToggleButton(){
		getSoundPreference();
		
		final AnimatedSprite button = new AnimatedSprite(this.CAMERA_WIDTH - (this.mSoundToggleTextureRegion.getWidth() + 5), (5 - TOP_PADDING), this.mSoundToggleTextureRegion, this.getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					ThemeActivity.this.mSoundEnabled = !ThemeActivity.this.mSoundEnabled;
					
					setSoundPreference();
					
					if(ThemeActivity.this.mSoundEnabled){
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
		this.mScene.registerTouchArea(button);
		this.mScene.attachChild(button);
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
        
 
        //Here is where you call the item load.
    private void loadLevel(final int iLevel) {
            if (iLevel != -1) {
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            			
                            	if(ThemeActivity.this.mSoundEnabled){
                        			mClickSound.play();
                        		}
                        		
                        		if(iLevel != MENU_ANIMALS){
                        			Toast.makeText(ThemeActivity.this, "Please buy the full game to unlock this theme!", Toast.LENGTH_SHORT).show();
            						return;
                        		}
                        		
                        		ThemeActivity.this.setThemePref(iLevel);
                        		startActivity(new Intent(ThemeActivity.this, DifficultyActivity.class));
                        		finish();
                            }
                    });
            }
    }
    
    @Override
	public void onBackPressed() {
		this.finish();
	   startActivity(new Intent(ThemeActivity.this, MainMenuActivity.class));
	}
    
    private void setThemePref(int theme){
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("theme", theme);

		editor.commit();
	}

	@Override
	public void onMoveToPageStarted(int pPageNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMoveToPageFinished(int pPageNumber) {
		// TODO Auto-generated method stub
		
	}
}