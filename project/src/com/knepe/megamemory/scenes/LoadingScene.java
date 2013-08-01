package com.knepe.megamemory.scenes;

import com.knepe.megamemory.management.SceneManager;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

public class LoadingScene extends BaseScene {
    @Override
    public void createScene() {
        Sprite loading = new Sprite(0, 0, resourcesManager.loading_region, vbom) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        attachChild(loading);
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene() {

    }

    @Override
    public void refreshSoundToggleButton() {
        return;
    }
}
