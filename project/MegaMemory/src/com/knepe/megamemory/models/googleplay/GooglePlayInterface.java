package com.knepe.megamemory.models.googleplay;

/**
 * Created by knepe on 2013-08-20.
 */
public interface GooglePlayInterface {
    public void login();
    public void logout();
    public boolean getSignedIn();
    public void submitScore(int score);
    public void showLeaderboard();
    public int sendReliableRealTimeMessage(byte[] messageData, java.lang.String roomId, java.lang.String recipientParticipantId);
    public void unlockAchievement(java.lang.String id);
    public void showAchievements();
    public void startQuickGame();
    public void startSelectPlayers(int minPlayers, int maxPlayers);
    public boolean getIsOpponentReady();
    public void leaveRoom();
    public String getRoomId();
    public String getMyId();
    public Opponent getOpponent();
    public void reset();
}
