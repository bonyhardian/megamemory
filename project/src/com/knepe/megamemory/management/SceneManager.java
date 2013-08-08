package com.knepe.megamemory.management;

import android.util.DisplayMetrics;
import android.widget.ViewFlipper;
import com.knepe.megamemory.GameActivity;
import com.knepe.megamemory.R;
import com.knepe.megamemory.scenes.*;
import com.knepe.megamemory.transitions.AbstractTransition;
import com.knepe.megamemory.transitions.ITransitionListener;
import com.knepe.megamemory.transitions.LeftPushInTransition;
import com.knepe.megamemory.transitions.RightPushInTransition;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.IGameInterface;

public class SceneManager
{
    //---------------------------------------------
    // SCENES
    //---------------------------------------------

    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    private BaseScene themesScene;
    private BaseScene difficultyScene;
    private BaseScene selectOpponentScene;

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    private BaseScene currentScene;

    private Engine engine = ResourceManager.getInstance().engine;

    private GameActivity mActivity;
    private DisplayMetrics mDisplayMetrics;
    private BaseScene mTransitionScene = new BaseScene() {
        @Override
        public void createScene() {

        }

        @Override
        public void onBackKeyPressed() {

        }

        @Override
        public SceneType getSceneType() {
            return null;
        }

        @Override
        public void disposeScene() {

        }

        @Override
        public void refreshSoundToggleButton() {

        }
    };

    public float getDisplayHeight() {
        return mDisplayMetrics.heightPixels * mDisplayMetrics.scaledDensity;
    }

    public float getDisplayWidth() {
        return mDisplayMetrics.widthPixels * mDisplayMetrics.scaledDensity;
    }

    public float getSurfaceHeight() {
        return engine.getCamera().getHeight();
    }

    public float getSurfaceWidth() {
        return engine.getCamera().getWidth();
    }

    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
        SCENE_THEMES,
        SCENE_DIFFICULTY,
        SCENE_SELECTOPPONENT
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void setScene(final BaseScene scene)
    {
        engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if(scene == null) return;
                engine.setScene(scene);
                currentScene = scene;
                currentSceneType = scene.getSceneType();
                scene.refreshSoundToggleButton();
            }
        });
    }

    public void setScene(BaseScene pInScene, AbstractTransition pTransition) {

        this.setScene(mTransitionScene);
        mTransitionScene.attachChild(currentScene);
        mTransitionScene.attachChild(pInScene);

        pTransition.execute(this.getCurrentScene(), pInScene, new ITransitionListener() {

            @Override
            public void onTransitionFinished(BaseScene pOutScene, BaseScene pInScene, AbstractTransition pTransition) {
                engine.runOnUpdateThread(new Runnable() {

                    @Override
                    public void run() {
                        mTransitionScene.detachChildren();
                    }
                });
                SceneManager.this.setScene(pInScene);
            }
        });

    }

    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            case SCENE_THEMES:
                setScene(themesScene);
                break;
            case SCENE_DIFFICULTY:
                setScene(difficultyScene);
                break;
            case SCENE_SELECTOPPONENT:
                setScene(selectOpponentScene);
                break;
            default:
                break;
        }
    }

    public void createSplashScene(IGameInterface.OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourceManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene();
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    private void disposeSplashScene()
    {
        ResourceManager.getInstance().unloadSplashScreen();
        splashScene.disposeScene();
        splashScene = null;
    }

    public void loadSelectOpponentScene(){
        selectOpponentScene = new SelectOpponentScene();
        setScene(selectOpponentScene);
    }

    public void loadMenuScene(final Engine mEngine)
    {
        setScene(loadingScene);
        disposeGameScene();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadMenuResources();
                menuScene = new MainMenuScene();
                themesScene = new ThemeScene();
                difficultyScene = new DifficultyScene();
                loadingScene = new LoadingScene();
                setScene(menuScene);
            }
        }));
    }

    public void createMenuScene(){
        boolean disposeSplashScene = getCurrentSceneType() == SceneType.SCENE_SPLASH;
        ResourceManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene();
        themesScene = new ThemeScene();
        difficultyScene = new DifficultyScene();
        loadingScene = new LoadingScene();
        setScene(menuScene);

        if(disposeSplashScene)
            disposeSplashScene();

        ((MainMenuScene)menuScene).showSignInPopup();
    }

    public void reloadMenuScene(){
        if(getCurrentSceneType() == SceneType.SCENE_GAME){
            setScene(loadingScene);
            disposeGameScene();
            createMenuScene();
        }
        else{
            menuScene = new MainMenuScene();
            setScene(menuScene);
        }
    }

    private void unloadMenuScene(){
        ResourceManager.getInstance().unloadMenuScene();
    }

    public void loadThemeScene(){
        setScene(themesScene);
    }

    public void loadDifficultyScene(){
        setScene(difficultyScene);
    }

    public void disposeGameScene(){
        ResourceManager.getInstance().unloadGameScene();
    }

    public void loadGameScene(final Engine mEngine)
    {
        setScene(loadingScene);
        unloadMenuScene();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadGameResources();
                gameScene = new GameScene();
                setScene(gameScene);
            }
        }));
    }

    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------

    public static SceneManager getInstance()
    {
        return INSTANCE;
    }

    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }

    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
}
