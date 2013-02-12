package com.knepe.megamemory;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.IMenuAnimator;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.svg.adt.SVG;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.view.Display;
import android.view.Gravity;
import android.widget.TextView;

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
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1.0f, 1.0f, 0.0f);
	private PhysicsWorld mPhysicsWorld;
	private Font mFont;
	private ArrayList<Balloon> balloons;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}
 
	@Override
	public void onCreateResources() {
		this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		
		this.mBgTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/mainmenu/mainbg.svg", CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mGreenBallonTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/mainmenu/greenballon.svg", 100, 180);
		this.mRedBallonTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/mainmenu/redballon.svg", 100, 180);
		//this.mBgTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		//this.mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgTexture, this, "gfx/mainbg.jpg", 0, 0);
		//this.mBgTexture.load(); 
  
		this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(), 400, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mMenuTexture, this, "gfx/btn/animbtn2.png", 0, 0, 2, 1);
		//this.mMenuButtonTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildableBitmapTextureAtlas, this, "gfx/btn/menubutton.svg", 400, 75, 2, 1);
		this.mMenuTexture.load();
		
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),"fonts/PORKYS.TTF", 28f, true, android.graphics.Color.WHITE);
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
		
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		
	    
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBgTextureRegion, this.getVertexBufferObjectManager())));

		
		
		this.mMainScene.setChildScene(this.mMenuScene, false, false, false);
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);

		this.mMainScene.attachChild(left);
		this.mMainScene.attachChild(right);
		this.mMainScene.attachChild(roof);
		
		balloons = new ArrayList<Balloon>();
		
		addBalloon(this.mRedBallonTextureRegion, CAMERA_WIDTH / 2 + getRandom(0, 50), CAMERA_HEIGHT, this.mMainScene);
		addBalloon(this.mGreenBallonTextureRegion, CAMERA_WIDTH / 2 - getRandom(0, 50), CAMERA_HEIGHT + 50, this.mMainScene);
		addBalloon(this.mGreenBallonTextureRegion, CAMERA_WIDTH / 2 - getRandom(0, 50), CAMERA_HEIGHT + 100, this.mMainScene);
		
		IUpdateHandler updateHandler = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(MainMenuActivity.this.balloons.isEmpty()){
					addBalloon(MainMenuActivity.this.mRedBallonTextureRegion, CAMERA_WIDTH / 2 + getRandom(0, 50), CAMERA_HEIGHT - 50, MainMenuActivity.this.mMainScene);
				}
				ArrayList<Balloon> balloonsToRemove = new ArrayList<Balloon>();
				for(Balloon b : MainMenuActivity.this.balloons){
					if(b.isDisposed()){
						balloonsToRemove.add(b);
						continue;
					}
					if( b.IsPopped()){
						balloonsToRemove.add(b);
						continue;
					}
						
					if(b.IsOutOfBounds()){
						balloonsToRemove.add(b);
						continue;
					}else{
						if(mPhysicsWorld.getBodyCount() > 0){
							Body body = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(b);
							Vector2 center = body.getWorldCenter();
				            Vector2 force = new Vector2(0.0f, (-SensorManager.GRAVITY_EARTH-1.5f) * body.getMass());
				            body.applyForce(force, center);
						}
					}
				}
				
				for(Balloon b : balloonsToRemove){
					balloons.remove(b);
					
					mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(b));
	                if(mPhysicsWorld.getBodyCount() > 0){
	                	Body body = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(b);
	                	if(body != null){
	                		mPhysicsWorld.destroyBody(mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(b));
	                	}
	            	}
	                
					if(!b.isDisposed()){
						MainMenuActivity.this.mMainScene.detachChild(b);
					}
						
					//add new balloon
					addBalloon(MainMenuActivity.this.mRedBallonTextureRegion, CAMERA_WIDTH / 2 + getRandom(0, 50), CAMERA_HEIGHT - 50, MainMenuActivity.this.mMainScene);
				}
			}
			@Override
			public void reset() {
			}
		};
		
		this.mMainScene.registerUpdateHandler(updateHandler);
		
		this.mMainScene.registerUpdateHandler(this.mPhysicsWorld);
		
		
		return this.mMainScene;
	}
	
	private int getRandom(int min, int max){
		Random rand = new Random();
		return rand.nextInt(max-min) + min;
	}
	private void addBalloon(ITextureRegion textureRegion, float x, float y, Scene scene){
		
		final Balloon balloon = new Balloon(x, y, textureRegion, this.getVertexBufferObjectManager(), this.mEngine);
		balloons.add(balloon);
		
		final Body body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, balloon, BodyType.DynamicBody, FIXTURE_DEF);

		balloon.addToScene(this.mMainScene);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(balloon, body, true, true));
		
		balloon.setPhysicsWorld(this.mPhysicsWorld, body);
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