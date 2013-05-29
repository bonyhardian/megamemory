package com.knepe.megamemory.management;

import android.widget.ViewFlipper;
import com.knepe.megamemory.GameActivity;
import com.knepe.megamemory.R;
import com.knepe.megamemory.scenes.*;
import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    private BaseScene currentScene;

    private Engine engine = ResourceManager.getInstance().engine;

    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
        SCENE_THEMES,
        SCENE_DIFFICULTY
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
        scene.refreshSoundToggleButton();
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
        ResourceManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene();
        themesScene = new ThemeScene();
        difficultyScene = new DifficultyScene();
        loadingScene = new LoadingScene();
        setScene(menuScene);
        disposeSplashScene();
    }

    public void reloadMenuScene(){
        menuScene = new MainMenuScene();
        setScene(menuScene);
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
