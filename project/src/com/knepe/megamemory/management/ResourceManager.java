package com.knepe.megamemory.management;

import android.graphics.Color;
import com.knepe.megamemory.GameActivity;
import com.knepe.megamemory.fonts.GradientFont;
import com.knepe.megamemory.fonts.GradientFontFactory;
import com.knepe.megamemory.fonts.GradientStrokeFont;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager
{
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    //SHARED
    private BitmapTextureAtlas mainBackgroundTextureAtlas;
    public Font main_font;
    public Font maint_font_gray;
    public ITextureRegion main_background_region;
    private BitmapTextureAtlas soundToggleTextureAtlas;
    public ITiledTextureRegion sound_toggle_region;
    public Sound click_sound;
    private BitmapTextureAtlas popupButtonTextureAtlas;
    public ITiledTextureRegion popup_button_region;
    //SPLASH SCENE
    public ITextureRegion splash_region;
    private BitmapTextureAtlas splashTextureAtlas;
    //LOADING SCENE
    public ITextureRegion loading_region;
    private BitmapTextureAtlas loadingTextureAtlas;
    //MAIN MENU SCENE
    private BitmapTextureAtlas menuButtonTextureAtlas;
    public ITiledTextureRegion menubutton_region;
    private BitmapTextureAtlas rateUsTextureAtlas;
    public ITextureRegion rate_us_region;
    //THEMES SCENE
    private BitmapTextureAtlas fingerTextureAtlas;
    public ITextureRegion finger_region;
    private BitmapTextureAtlas ladyBugTextureAtlas;
    public ITiledTextureRegion lady_bug_region;
    private BitmapTextureAtlas speechBubbleTextureAtlas;
    public ITextureRegion speech_bubble_region;
    public List<TextureRegion> themes_regions = new ArrayList<TextureRegion>();
    public final int THEMES_MENU_ITEMS = 6;
    //GAME SCENE
    public final int TEXTUREPACK_CARDS = 18;
    private TexturePackTextureRegionLibrary texturePackLibrary;
    private TexturePack texturePack;
    private BitmapTextureAtlas gameBackgroundTextureAtlas;
    private BitmapTextureAtlas popupTextureAtlas;
    private BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlas;
    private BitmapTextureAtlas particleTextureAtlas;
    private BitmapTextureAtlas gameButtonTextureAtlas;
    public TextureRegion score_hud_region;
    public ArrayList<ITiledTextureRegion> card_regions = new ArrayList<ITiledTextureRegion>();
    public TextureRegion particle_region;
    public TextureRegion popup_region;
    public Font game_font;
    public GradientStrokeFont bonus_font;
    public GradientStrokeFont player_turn_font;
    public Font game_font_small;
    public TextureRegion game_background_region;
    public ITiledTextureRegion home_icon_region;
    public ITiledTextureRegion retry_icon_region;
    public Sound turn_card_sound;
    public Sound correct_sound;
    public Sound finished_sound;
    public Sound fireworks_sound;

    private static final ResourceManager INSTANCE = new ResourceManager();

    public Engine engine;
    public GameActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;

    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
        loadThemeMenuGraphics();
        loadLoadingResources();
    }

    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }

    private void loadLoadingResources(){
        loadingTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 500, 800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        loading_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingTextureAtlas, activity, "gfx/loading.png", 0, 0);
        loadingTextureAtlas.load();
    }

    private void loadMenuGraphics()
    {
        mainBackgroundTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        main_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBackgroundTextureAtlas, activity, "gfx/main-bg.jpg", 0, 0);
        mainBackgroundTextureAtlas.load();

        rateUsTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 350, 100, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        rate_us_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(rateUsTextureAtlas, activity, "gfx/btn/rate-us.png", 0, 0);
        rateUsTextureAtlas.load();

        menuButtonTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 395, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menubutton_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuButtonTextureAtlas, activity, "gfx/btn/animbtn2.png", 0, 0, 2, 1);
        menuButtonTextureAtlas.load();

        soundToggleTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 138, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sound_toggle_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(soundToggleTextureAtlas, activity, "gfx/btn/togglesound-btn.png", 0, 0, 2, 1);
        soundToggleTextureAtlas.load();

        popupTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 400,400, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        popup_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(popupTextureAtlas, activity, "gfx/popup-bg.png", 0, 0);
        popupTextureAtlas.load();

        popupButtonTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 300, 60, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        popup_button_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(popupButtonTextureAtlas, activity, "gfx/btn/popup-btn.png", 0, 0, 2, 1);
        popupButtonTextureAtlas.load();
    }

    private void loadMenuFonts(){
        main_font = FontFactory.createFromAsset(activity.getFontManager(), activity.getTextureManager(), 256, 256, activity.getAssets(), "fonts/Cookies.ttf", 26f, true, android.graphics.Color.WHITE);
        main_font.load();
        maint_font_gray = FontFactory.createFromAsset(activity.getFontManager(), activity.getTextureManager(), 256, 256, activity.getAssets(), "fonts/Cookies.ttf", 26f, true, Color.DKGRAY);
        maint_font_gray.load();
    }
    private void loadMenuAudio()
    {
        try {
            click_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "snd/click.ogg");
        } catch (final IOException e) {
            Debug.e("Error", e);
        }
    }

    private void loadThemeMenuGraphics(){
        for (int i = 0; i < THEMES_MENU_ITEMS; i++) {
            BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            ITextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, activity, "gfx/theme-menu/menu"+i+".png", 0, 0);

            mMenuBitmapTextureAtlas.load();
            themes_regions.add((TextureRegion) mMenuTextureRegion);
        }

        fingerTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 103,154, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        finger_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(fingerTextureAtlas, activity, "gfx/swipe-finger.png", 0, 0);
        fingerTextureAtlas.load();

        speechBubbleTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 150,133, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        speech_bubble_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(speechBubbleTextureAtlas, activity, "gfx/speechbubble.png", 0, 0);
        speechBubbleTextureAtlas.load();

        ladyBugTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 605, 215, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        lady_bug_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(ladyBugTextureAtlas, activity, "gfx/animated-ladybug.png", 0, 0, 5, 2);
        ladyBugTextureAtlas.load();
    }

    private void loadGameGraphics()
    {
        activity.initLayout();

        String assetsPath = "gfx/sprites/" + ThemeManager.getThemeString(activity.THEME) + "/" + DifficultyManager.getLayoutString(activity.DIFFICULTY) + "/";
        try
        {
            texturePack = new TexturePackLoader(activity.getTextureManager(), assetsPath).loadFromAsset(activity.getAssets(), "texture.xml");
            texturePack.loadTexture();
            texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
        }
        catch (final TexturePackParseException e)
        {
            Debug.e(e);
        }

        for(int i = 0; i < TEXTUREPACK_CARDS; i++){
            ITiledTextureRegion textureRegion = texturePackLibrary.get(i, 2, 1);
            card_regions.add(textureRegion);
        }

        gameBackgroundTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

        particleTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 20, 21, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        //BACKGROUND
        this.mBuildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
        score_hud_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlas, activity, "gfx/top-hud.png");
        game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "gfx/backgrounds/" + BackgroundManager.getBackground(activity.THEME), 0, 0);

        gameButtonTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 276, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        home_icon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameButtonTextureAtlas, activity, "gfx/btn/home-btn.png", 0, 0, 2, 1);
        retry_icon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameButtonTextureAtlas, activity, "gfx/btn/retry-btn.png", 138, 0, 2, 1);

        //PARTICLE
        particle_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(particleTextureAtlas, activity, "gfx/particleStar.png", 0, 0);

        //LOAD
        gameBackgroundTextureAtlas.load();
        gameButtonTextureAtlas.load();
        particleTextureAtlas.load();

        try {
            this.mBuildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.mBuildableBitmapTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    private void loadGameFonts()
    {
        game_font = FontFactory.createFromAsset(activity.getFontManager(), activity.getTextureManager(), 256, 256, activity.getAssets(),"fonts/Cookies.ttf", 26f, true, android.graphics.Color.WHITE);
        game_font.load();

        game_font_small = FontFactory.createFromAsset(activity.getFontManager(), activity.getTextureManager(), 256, 256, activity.getAssets(),"fonts/Cookies.ttf", 22f, true, android.graphics.Color.WHITE);
        game_font_small.load();


        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        GradientFontFactory.setAssetBasePath("fonts/");
        bonus_font = GradientFontFactory.createStrokeFromAsset(activity.getFontManager(), fontTexture, activity.getAssets(), "Cookies.ttf", 70f, true, new org.andengine.util.color.Color[]{new org.andengine.util.color.Color(0.8588235294117647f, 0.8901960784313725f, 0.1882352941176471f), new org.andengine.util.color.Color(0.7411764705882353f, 0.7764705882352941f, 0f), new org.andengine.util.color.Color(0.8588235294117647f, 0.8901960784313725f, 0.1882352941176471f)}, 1f, Color.DKGRAY);
        bonus_font.load();

        BitmapTextureAtlas player_turn_fontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        GradientFontFactory.setAssetBasePath("fonts/");
        player_turn_font = GradientFontFactory.createStrokeFromAsset(activity.getFontManager(), player_turn_fontTexture, activity.getAssets(), "Cookies.ttf", 40f, true, new org.andengine.util.color.Color[]{new org.andengine.util.color.Color(0.8588235294117647f, 0.8901960784313725f, 0.1882352941176471f), new org.andengine.util.color.Color(0.7411764705882353f, 0.7764705882352941f, 0f), new org.andengine.util.color.Color(0.8588235294117647f, 0.8901960784313725f, 0.1882352941176471f)}, 1f, Color.DKGRAY);
        player_turn_font.load();
    }

    private void loadGameAudio()
    {
        try {
            turn_card_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "snd/turn-card.wav");
            correct_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "snd/correct.wav");
            finished_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "snd/complete.wav");
            fireworks_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "snd/firework-pop.wav");
        } catch (final IOException e) {
            Debug.e("Error", e);
        }
    }

    public void loadSplashScreen()
    {
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 500, 800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "gfx/splash.png", 0, 0);
        splashTextureAtlas.load();
    }

    public void unloadSplashScreen()
    {
        splashTextureAtlas.unload();
        splash_region = null;
    }

    public void unloadGameScene()
    {
        texturePack.unloadTexture();
        texturePackLibrary = null;

        card_regions = new ArrayList<ITiledTextureRegion>();

        gameBackgroundTextureAtlas.unload();
        particleTextureAtlas.unload();

        //BACKGROUND
        mBuildableBitmapTextureAtlas.unload();
        score_hud_region = null;
        game_background_region = null;

        gameButtonTextureAtlas.unload();
        home_icon_region = null;
        retry_icon_region = null;

        //PARTICLE
        particle_region = null;

        game_font.unload();

        turn_card_sound = null;
        correct_sound = null;
        finished_sound = null;
        fireworks_sound = null;
    }

    public void unloadMenuScene()
    {
        if(mainBackgroundTextureAtlas != null)
            mainBackgroundTextureAtlas.unload();
        main_background_region = null;
        if(menuButtonTextureAtlas != null)
            menuButtonTextureAtlas.unload();
        menubutton_region = null;
        if(soundToggleTextureAtlas != null)
            soundToggleTextureAtlas.unload();
        sound_toggle_region = null;

        if(main_font != null)
            main_font.unload();

        themes_regions = new ArrayList<TextureRegion>();
        if(fingerTextureAtlas != null)
            fingerTextureAtlas.unload();
        finger_region = null;
        if(speechBubbleTextureAtlas != null)
            speechBubbleTextureAtlas.unload();
        speech_bubble_region = null;
        if(ladyBugTextureAtlas != null)
            ladyBugTextureAtlas.unload();
        lady_bug_region = null;
    }

    /**
     * @param engine
     * @param activity
     * @param camera
     * @param vbom
     * <br><br>
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    public static void prepareManager(Engine engine, GameActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }

    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------

    public static ResourceManager getInstance()
    {
        return INSTANCE;
    }
}