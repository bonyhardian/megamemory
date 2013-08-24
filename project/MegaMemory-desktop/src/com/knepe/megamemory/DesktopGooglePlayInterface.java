package com.knepe.megamemory;

import com.knepe.megamemory.models.googleplay.GooglePlayInterface;
import com.knepe.megamemory.models.googleplay.Opponent;

public class DesktopGooglePlayInterface implements GooglePlayInterface {
    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean getSignedIn() {
        return false;
    }

    @Override
    public void submitScore(int score) {

    }

    @Override
    public void showLeaderboard() {

    }


    @Override
    public int sendReliableRealTimeMessage(byte[] messageData, String roomId, String recipientParticipantId) {
        return 0;
    }

    @Override
    public void unlockAchievement(String id) {

    }

    @Override
    public void showAchievements() {

    }

    @Override
    public void leaveRoom() {

    }

    @Override
    public String getRoomId() {
        return null;
    }

    @Override
    public String getMyId() {
        return null;
    }

    @Override
    public Opponent getOpponent() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void startQuickGame() {

    }

    @Override
    public void startSelectPlayers(int minPlayers, int maxPlayers) {

    }

    @Override
    public boolean getIsOpponentReady() {
        return false;
    }
}
