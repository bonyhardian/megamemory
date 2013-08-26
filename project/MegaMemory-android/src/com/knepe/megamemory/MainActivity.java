package com.knepe.megamemory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.knepe.megamemory.models.googleplay.GooglePlayInterface;
import com.knepe.megamemory.models.googleplay.Opponent;
import com.knepe.megamemory.screens.GameScreen;
import com.knepe.megamemory.screens.MainScreen;
import com.knepe.megamemory.screens.SplashScreen;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GBaseGameActivity implements RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener, GooglePlayInterface {

    private final String TAG = "MM";
    private MegaMemory game;
    final public static int RC_SELECT_PLAYERS = 10000;
    final public static int RC_INVITATION_INBOX = 10001;
    final public static int RC_WAITING_ROOM = 10002;
    final public static int RC_ACHIEVMENTS = 5002;
    final public static int RC_LEADER_BOARD = 5001;
    //Multiplayer stuff
    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    public boolean isOpponentReady = false;
    public String mRoomId = null;
    // My participant ID in the currently active game
    public String mMyId = null;
    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    public String mIncomingInvitationId = null;
    // flag indicating whether we're dismissing the waiting room because the
    // game is starting
    boolean mWaitRoomDismissedFromCode = false;
    // The participants in the currently active game
    public ArrayList<Participant> mParticipants = null;
    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useWakelock = true;
        cfg.useCompass = false;
        cfg.useAccelerometer = false;
        game = new MegaMemory(displayMetrics.widthPixels, displayMetrics.heightPixels, this);
        initialize(game, cfg);
        Gdx.app.log(TAG, "height: " + displayMetrics.heightPixels);
        Gdx.app.log("MM", "game assetpath" + game.assetBasePath);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new SplashScreen(game));
            }
        });
    }

    @Override
    public void onSignInFailed() {
        Gdx.app.log(TAG, "Signin failed");
    }

    @Override
    public void onSignInSucceeded() {
        Gdx.app.log(TAG, "Signin succeeded");

        try
        {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainScreen(game));
                }
            });
            getGamesClient().unlockAchievement(this.getString(R.string.achievement_firstsignin));
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }

        // install invitation listener so we get notified if we receive an
        // invitation to play
        // a game.
        getGamesClient().registerInvitationListener(this);

        //startActivityForResult(getGamesClient().getAchievementsIntent(),5002);

        if (getInvitationId() != null) {
            acceptInviteToRoom(getInvitationId());
        }
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        //Reset Game.
        getGamesClient().joinRoom(roomConfigBuilder.build());
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.d(TAG, "Invitation received");
        mIncomingInvitationId = invitation.getInvitationId();
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        GameScreen screen = null;

        try{
            screen = (GameScreen)game.getScreen();
        }catch(Exception e){
            Gdx.app.log(TAG, e.getMessage());
        }

        final GameScreen gameScreen = screen;

        byte[] buf = realTimeMessage.getMessageData();
        final String message = new String(buf);

        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
        if(buf[0] == 'G'){
            // someone else started to play -- so dismiss the waiting room and
            // get right to it!
            Log.d(TAG, "Starting game because we got a start message.");
            dismissWaitingRoom();
            startGame();
        } else{
            if(message.contains("Ready")){
                this.isOpponentReady = true;
                return;
            }
            if(message.contains("Done")){
                if(gameScreen != null)
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            gameScreen.setMyTurn();
                        }
                    });
                return;
            }
            if(message.contains("First") || message.contains("Second")){
                if(gameScreen != null){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            String[] response = message.split(":");
                            Integer id = Integer.parseInt(response[1]);
                            gameScreen.executeCardCalculation(id);
                        }
                    });
                }

                return;
            }
            if(message.contains("Score")){
                if(gameScreen != null){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            String[] response = message.split(":");
                            Integer score = Integer.parseInt(response[1]);
                            gameScreen.showFinishPopupMultiplayer(score);
                        }
                    });
                }

                return;
            }
            if(message.contains("Left")){
                if(gameScreen != null){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            gameScreen.showOpponentLeftPopup();
                        }
                    });
                }
            }
        }
    }

    void dismissWaitingRoom() {
        mWaitRoomDismissedFromCode = true;
        finishActivity(RC_WAITING_ROOM);
    }

    void startGame() {
        //updateScoreDisplay();
        //broadcastScore(false);
        Gdx.app.log(TAG, "Starting game");
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new GameScreen(game));
            }
        });
    }

    // Broadcast a message indicating that we're starting to play. Everyone else
    // will react
    // by dismissing their waiting room UIs and starting to play too.
    void broadcastStart() {
        mMsgBuf[0] = 'G';
        mMsgBuf[1] = (byte) 0;
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;
            getGamesClient().sendReliableRealTimeMessage(null, mMsgBuf, mRoomId,
                    p.getParticipantId());
        }
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainScreen(game));
                }
            });
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);
        if(invitees == null) return;
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        Gdx.app.log(TAG, "create room invite, auto mathc criteria: " + autoMatchCriteria);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        rtmConfigBuilder.setVariant(getVariant());
        //resetGameVars();
        getGamesClient().createRoom(rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainScreen(game));
                }
            });
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        android.os.Bundle extras = data.getExtras();
        if(extras == null) return;

        Invitation inv = extras.getParcelable(GamesClient.EXTRA_INVITATION);
        if(inv == null) return;
        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    private void sendLeftRoomToOpponent(){
        String stringMessage = "Left";
        byte[] message = stringMessage.getBytes();

        try
        {
            this.getGamesClient().sendReliableRealTimeMessage(null, message, this.mRoomId, this.getOpponent().getId());
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }

    }

    // Leave the room.
    @Override
    public void leaveRoom() {
        this.isOpponentReady = false;
        Log.d(TAG, "Leaving room.");
        //mSecondsLeft = 0;

        if (mRoomId != null) {
            sendLeftRoomToOpponent();
            getGamesClient().leaveRoom(this, mRoomId);
            mRoomId = null;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainScreen(game));
                }
            });
        } else {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainScreen(game));
                }
            });
        }
    }

    @Override
    public Opponent getOpponent(){
        if(this.mParticipants == null || this.mParticipants.size() == 0) return null;
        Participant participant = null;

        for(Participant p : mParticipants){
            if(!p.getParticipantId().equals(mMyId)){
                participant = p;
                break;
            }
        }

        if(participant == null) return null;

        return new Opponent(participant.getParticipantId(), participant.getDisplayName());
    }

    @Override
    public void reset() {
        try
        {
            getGamesClient().leaveRoom(this, mRoomId);
        }
        catch(Exception e){
            Gdx.app.log(TAG, e.getMessage());
        }

        this.mRoomId = null;
        this.mIncomingInvitationId = null;
        this.mParticipants = new ArrayList<Participant>();
    }

    void updateRoom(Room room) {
        mParticipants = room.getParticipants();
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        // get room ID, participants and my ID:
        mRoomId = room.getRoomId();
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(getGamesClient().getCurrentPlayerId());
        int variant = room.getVariant();
        if(variant > 0){
            parseAndSetGameSettings(variant);
        }

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        showAlert(getString(R.string.error), getString(R.string.game_problem));
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MainScreen(game));
            }
        });
    }

    @Override
    public void onPeersConnected(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> strings) {
        Log.e(TAG, "*** Other player disconnected");
        updateRoom(room);
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesClient.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }
    void showGameError() {
        showAlert(getString(R.string.error), getString(R.string.game_problem));
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MainScreen(game));
            }
        });
    }

    private void showWaitingRoom(Room room) {
        mWaitRoomDismissedFromCode = false;

        // minimum number of players required for our game
        final int MIN_PLAYERS = 2;
        Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, MIN_PLAYERS);
        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    private void parseAndSetGameSettings(Integer variant){
        Gdx.app.log(TAG, "variant = " + variant);
        if(variant.toString().length() < 2) return;
        Gdx.app.log(TAG, "parse: 0 = " + variant.toString().charAt(0) + ", 1 = " + variant.toString().charAt(1));
        game.THEME = variant.toString().charAt(0);
        game.DIFFICULTY = variant.toString().charAt(1);
    }
    @Override
    public void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(getAutoMatchCriteria());
        //resetGameVars();
        this.getGamesClient().createRoom(rtmConfigBuilder.build());
    }

    private Bundle getAutoMatchCriteria(){
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, Long.parseLong(String.format("%s%d", game.THEME, game.DIFFICULTY)));

        return autoMatchCriteria;
    }

    private int getVariant(){
        return Integer.parseInt(String.format("%s%d", game.THEME, game.DIFFICULTY));
    }
    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesClient.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {
// we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MainScreen(game));
            }
        });
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesClient.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // ignore result if we dismissed the waiting room from code:
                if (mWaitRoomDismissedFromCode) break;

                // we got the result from the "waiting room" UI.
                if (responseCode == RESULT_OK) {
                    // player wants to start playing
                    Log.d(TAG, "Starting game because user requested via waiting room UI.");
                    // let other players know we're starting.
                    broadcastStart();
                    startGame();
                    // start the game!

                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player actively indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == RESULT_CANCELED) {
                    /* Dialog was cancelled (user pressed back key, for
                     * instance). In our game, this means leaving the room too. In more
                     * elaborate games,this could mean something else (like minimizing the
                     * waiting room UI but continue in the handshake process). */
                    leaveRoom();
                }

                break;
        }

    }

    @Override
    public void login() {
        runOnUiThread(new Runnable() {
            public void run() {

                beginUserInitiatedSignIn();
            }
        });
    }

    @Override
    public void logout() {
        runOnUiThread(new Runnable() {
            public void run() {
                signOut();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MainScreen(game));
                    }
                });
            }
        });
    }

    @Override
    public boolean getSignedIn() {
        return isSignedIn();
    }

    @Override
    public void submitScore(int score) {
        getGamesClient().submitScore(getString(R.string.leaderboard_Main), score);
    }

    @Override
    public void showLeaderboard() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isSignedIn())
                    startActivityForResult(getGamesClient().getLeaderboardIntent(getString(R.string.leaderboard_Main)), RC_LEADER_BOARD);
            }
        });
    }

    @Override
    public int sendReliableRealTimeMessage(byte[] messageData, String roomId, String recipientParticipantId) {
        return getGamesClient().sendReliableRealTimeMessage(null, messageData, mRoomId, getOpponent().getId());
    }

    @Override
    public void unlockAchievement(String id) {
        getGamesClient().unlockAchievement(id);
    }

    @Override
    public void showAchievements() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isSignedIn())
                    startActivityForResult(getGamesClient().getAchievementsIntent(), RC_ACHIEVMENTS);
            }
        });
    }

    @Override
    public void startSelectPlayers(int minPlayers, int maxPlayers) {
        startActivityForResult(getGamesClient().getSelectPlayersIntent(1, 1), RC_SELECT_PLAYERS);
    }

    @Override
    public boolean getIsOpponentReady() {
        return this.isOpponentReady;
    }

    @Override
    public String getRoomId() {
        return this.mRoomId;
    }

    @Override
    public String getMyId() {
        return this.mMyId;
    }
}