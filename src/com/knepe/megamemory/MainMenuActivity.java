package com.knepe.megamemory;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
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
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.opengl.GLES20;

public class MainMenuActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener {
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;

	protected static final int MENU_NEWGAME = 0;
	protected static final int MENU_OPTIONS = MENU_NEWGAME + 1;
	protected static final int MENU_QUIT = MENU_OPTIONS + 1;

	protected Camera mCamera;

	protected Scene mMainScene;

	protected MenuScene mMenuScene;

	private BitmapTextureAtlas mMenuTexture;
	private BitmapTextureAtlas mBgTexture;
	protected ITiledTextureRegion mMenuButtonTextureRegion;
	protected ITextureRegion mMenuOptionsTextureRegion;
	protected ITextureRegion mBgTextureRegion;
	protected ITextureRegion mRedBallonTextureRegion;
	protected ITextureRegion mGreenBallonTextureRegion;
	private BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlas;
	private Font mFont;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}
 
	@Override
	public void onCreateResources() {
		this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		
		this.mBgTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgTexture, this, "gfx/main-bg.jpg", 0, 0);
		this.mBgTexture.load(); 
  
		this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(), 395, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mMenuTexture, this, "gfx/btn/animbtn2.png", 0, 0, 2, 1);
		this.mMenuTexture.load();
		
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),"fonts/doctorsos.ttf", 34f, true, android.graphics.Color.WHITE);
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
		this.createMenuScene();
		
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBgTextureRegion, this.getVertexBufferObjectManager())));

		this.mMainScene.setChildScene(this.mMenuScene, false, false, false);

		return this.mMainScene;
	}
	
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		((AnimatedSpriteMenuItem)pMenuItem).setCurrentTileIndex(1);
		switch(pMenuItem.getID()) {
			case MENU_NEWGAME:
				startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
				return true;
			case MENU_OPTIONS:
				//add options activity
				return true;
			case MENU_QUIT:
				this.finish();
				return true;
			default:
				return false;
		}
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
		
		//final SpriteMenuItem button = new SpriteMenuItem(pId, this.mMenuButtonTextureRegion, this.getVertexBufferObjectManager());
		
		button.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		this.mMenuScene.addMenuItem(button);

		Text btnText = new Text(0,0, mFont, text, this.getVertexBufferObjectManager());
		btnText.setPosition(((button.getWidth() / 2) - btnText.getWidth() / 2), (button.getHeight() / 2) - 20);
		button.attachChild(btnText);
	}
	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.mCamera);

		createButtonWithText(this.getString(R.string.menu_newgame), MENU_NEWGAME);
		createButtonWithText(this.getString(R.string.menu_options), MENU_OPTIONS);
		createButtonWithText(this.getString(R.string.menu_quit), MENU_QUIT);
		
		SlideMenuAnimator slideanim = new SlideMenuAnimator(30);
        
        this.mMenuScene.setMenuAnimator(slideanim);
		this.mMenuScene.buildAnimations();
		this.mMenuScene.setBackgroundEnabled(false);

		this.mMenuScene.setOnMenuItemClickListener(this);
		this.mMenuScene.clearUpdateHandlers();
	}
}