package com.knepe.megamemory.scoreloop;

public interface IActionResolver {
    public void bootstrap();
    public void showScoreloop();
    public void submitScore(int mode, int score);
    public void refreshScores();
}
