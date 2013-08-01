package com.knepe.megamemory.transitions;


import com.knepe.megamemory.scenes.BaseScene;

import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

/**
 *
 * @author rkpost
 */
public abstract class AbstractTransition {

    protected IEaseFunction mEaseFunction;
    protected float mDuration;

    public AbstractTransition(float pDuration) {
        this(pDuration, EaseLinear.getInstance());
    }

    public AbstractTransition(float mDuration, IEaseFunction pEaseFunction) {
        this.mDuration = mDuration;
        this.mEaseFunction = pEaseFunction;
    }

    public abstract void execute(final BaseScene pOutScene, final BaseScene pInScene, final ITransitionListener pTransitionListener);

    public abstract AbstractTransition getBackTransition();

    protected void onTransitionFinish(final BaseScene pOutScene, final BaseScene pInScene, final ITransitionListener pTransitionListener) {

        pOutScene.setPosition(0, 0);
        pOutScene.setRotation(0);
        pOutScene.setAlpha(1);
        pOutScene.setScale(1);
        pOutScene.setVisible(true);

        pInScene.setPosition(0, 0);
        pInScene.setRotation(0);
        pInScene.setAlpha(1);
        pInScene.setScale(1);
        pInScene.setVisible(true);

        pTransitionListener.onTransitionFinished(pOutScene, pInScene, this);

    }
}