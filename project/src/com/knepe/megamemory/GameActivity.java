package com.knepe.megamemory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

import com.appflood.AppFlood;
import com.knepe.megamemory.management.ResourceManager;
import com.knepe.megamemory.management.SceneManager;
import com.knepe.megamemory.scenes.MainMenuScene;
import com.knepe.megamemory.scoreloop.ActionResolver;
import com.scoreloop.client.android.core.model.Client;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.LayoutGameActivity;

public class GameActivity extends GBaseGameActivity {
    private static final String TAG = "MegaMemory";
    public final int CAMERA_WIDTH = 500;
    public final int CAMERA_HEIGHT = 800;
    final public static int RC_ACHIEVMENTS = 5002;
    final public static int RC_LEADER_BOARD = 5001;
    public int THEME = -1;
    public int DIFFICULTY = 0;

    public int NUM_ROWS = 4;
    public int NUM_COLS = 4;

    public boolean sound_enabled;
    private ResourceManager resourcesManager;
    private Camera camera;

    public ActionResolver scoreloopActionResolver;
    private static Client client;
    private static String _gameSecret = "CQwKmiS8XpSXt31BCOHxltb7hEpTvVDncdiwBw0kBbfnmvP//9uacg==";

    static void initScoreloop(final Context android_game_context) {
        if (client == null) {
            client = new Client(android_game_context, _gameSecret, null);
        }
    }

    void initAppFlood(){
        try{
            AppFlood.initialize(this, "FTkRSXAaB7NFK8hO", "j5ohAa0ea85L518a5d32", AppFlood.AD_ALL);
        }catch(Exception e){}
    }

    @Override
    protected void onCreate(android.os.Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);

        initScoreloop(this);
        initAppFlood();

        try{
            ScoreloopManagerSingleton.init(this, _gameSecret);
        }catch(Exception e){}

        scoreloopActionResolver = new ActionResolver(GameActivity.this);
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(SceneManager.getInstance().getCurrentScene() != null)
                SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }
        return false;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions en = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        en.getAudioOptions().setNeedsSound(true);
        return en;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
        ResourceManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
        resourcesManager = ResourceManager.getInstance();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        getSoundPreference();
        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
        }));
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    public void setSoundPreference(){
        SharedPreferences settings = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("soundEnabled", sound_enabled);

        editor.commit();
    }

    public void getSoundPreference(){
        SharedPreferences settings = getSharedPreferences("preferences", 0);
        boolean soundEnabled = settings.getBoolean("soundEnabled", true);
        sound_enabled = soundEnabled;
    }

    public void initLayout(){
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

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.SurfaceViewId;
    }

    @Override
    public void onSignInFailed() {
        // Sign in has failed. So show the user the sign-in button.
        Log.d(TAG, "Sign-in failed.");
    }

    @Override
    public void onSignInSucceeded() {
        Log.d(TAG, "Sign-in succeeded.");

        try
        {
            if(SceneManager.getInstance() != null && SceneManager.getInstance().getCurrentScene() != null && SceneManager.getInstance().getCurrentSceneType() == SceneManager.SceneType.SCENE_MENU){
                resourcesManager.engine.setScene(null);
                SceneManager.getInstance().reloadMenuScene();
            }

            getGamesClient().unlockAchievement(this.getString(R.string.achievement_firstsignin));
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }
}
