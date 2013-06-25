package com.knepe.megamemory.transitions;

import com.knepe.megamemory.management.SceneManager;
import com.knepe.megamemory.scenes.BaseScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.IEaseFunction;

/**
 * Created by knepe on 2013-06-09.
 */
public class RightPushInTransition extends AbstractTransition {

    public RightPushInTransition(float pDuration) {
        super(pDuration);
    }

    public RightPushInTransition(float mDuration, IEaseFunction pEaseFunction) {
        super(mDuration, pEaseFunction);
    }

    @Override
    public void execute(final BaseScene pOutScene, final BaseScene pInScene, final ITransitionListener pTransitionListener) {

        float width = SceneManager.getInstance().getSurfaceWidth();

        MoveXModifier outModifier = new MoveXModifier(1, 0, -width, mEaseFunction);
        MoveXModifier inModifier = new MoveXModifier(1, width, 0, mEaseFunction);

        pInScene.registerEntityModifier(inModifier);
        pOutScene.registerEntityModifier(outModifier);

        inModifier.addModifierListener(new IModifier.IModifierListener<IEntity>() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                onTransitionFinish(pOutScene, pInScene, pTransitionListener);
            }
        });

    }

    @Override
    public AbstractTransition getBackTransition() {
        return this;
    }
}
