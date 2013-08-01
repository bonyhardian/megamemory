package com.knepe.megamemory.transitions;

import com.knepe.megamemory.scenes.BaseScene;

public interface ITransitionListener {

    public void onTransitionFinished(BaseScene pOutScene, BaseScene pInScene, AbstractTransition pTransition);

}
