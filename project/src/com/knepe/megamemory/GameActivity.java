package com.knepe.megamemory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.appflood.AppFlood;
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
import com.knepe.megamemory.management.ResourceManager;
import com.knepe.megamemory.management.SceneManager;
import com.knepe.megamemory.scenes.GameScene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends GBaseGameActivity implements RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {
    private static final String TAG = "MegaMemory";
    public final int CAMERA_WIDTH = 500;
    public final int CAMERA_HEIGHT = 800;

    // Request codes for the UIs that we show with startActivityForResult:
    final public static int RC_SELECT_PLAYERS = 10000;
    final public static int RC_INVITATION_INBOX = 10001;
    final public static int RC_WAITING_ROOM = 10002;
    final public static int RC_ACHIEVMENTS = 5002;
    final public static int RC_LEADER_BOARD = 5001;
    public int THEME = -1;
    public int DIFFICULTY = 0;

    public int NUM_ROWS = 4;
    public int NUM_COLS = 4;
    public int myRandom = 0;
    public int opponentsRandom = 0;
    public boolean sound_enabled;
    private ResourceManager resourcesManager;
    private Camera camera;

    //Multiplayer stuff
    // Room ID where the currently active game is taking place; null if we're
    // not playing.

    public String mRoomId = null;

    // My participant ID in the currently active game
    public String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // flag indicating whether we're dismissing the waiting room because the
    // game is starting
    boolean mWaitRoomDismissedFromCode = false;

    // The participants in the currently active game
    public ArrayList<Participant> mParticipants = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];

    void initAppFlood(){
        try{
            AppFlood.initialize(this, "FTkRSXAaB7NFK8hO", "j5ohAa0ea85L518a5d32", AppFlood.AD_ALL);
        }catch(Exception e){}
    }

    @Override
    protected void onCreate(android.os.Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);
        //initAppFlood();
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(SceneManager.getInstance().getCurrentScene() != null)
                SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }
        return false;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions en = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        en.getAudioOptions().setNeedsSound(true);
        return en;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
        ResourceManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
        resourcesManager = ResourceManager.getInstance();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        getSoundPreference();
        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
        }));
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    public void setSoundPreference(){
        SharedPreferences settings = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("soundEnabled", sound_enabled);

        editor.commit();
    }

    public Participant getOpponent(){
        if(this.mParticipants == null || this.mParticipants.size() == 0) return null;
        Participant participant = null;

        for(Participant p : mParticipants){
            if(!p.getParticipantId().equals(mMyId)){
                participant = p;
                break;
            }
        }

        return participant;
    }
    public void getSoundPreference(){
        SharedPreferences settings = getSharedPreferences("preferences", 0);
        boolean soundEnabled = settings.getBoolean("soundEnabled", true);
        sound_enabled = soundEnabled;
    }

    public void initLayout(){
        switch(DIFFICULTY){
            case 0:
                NUM_COLS = 2;
                NUM_ROWS = 2;
                break;
            case 1:
                NUM_COLS = 4;
                NUM_ROWS = 4;
                break;
            case 2:
                NUM_COLS = 6;
                NUM_ROWS = 6;
                break;
            default:
                NUM_COLS = 2;
                NUM_ROWS = 2;
                break;
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.SurfaceViewId;
    }

    @Override
    public void onSignInFailed() {
        // Sign in has failed. So show the user the sign-in button.
        Log.d(TAG, "Sign-in failed.");
    }

    @Override
    public void onSignInSucceeded() {
        Log.d(TAG, "Sign-in succeeded.");

        try
        {
            if(SceneManager.getInstance() != null && SceneManager.getInstance().getCurrentScene() != null && SceneManager.getInstance().getCurrentSceneType() == SceneManager.SceneType.SCENE_MENU){
                resourcesManager.engine.setScene(null);
                SceneManager.getInstance().reloadMenuScene();
            }

            getGamesClient().unlockAchievement(this.getString(R.string.achievement_firstsignin));
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        mIncomingInvitationId = invitation.getInvitationId();
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] buf = realTimeMessage.getMessageData();
        String message = new String(buf);

        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
        if(buf[0] == 'G'){
            // someone else started to play -- so dismiss the waiting room and
            // get right to it!
            Log.d(TAG, "Starting game because we got a start message.");
            dismissWaitingRoom();
            startGame();
        } else{
            if(message.contains("Done")){
                ((GameScene)SceneManager.getInstance().getCurrentScene()).setMyTurn();
                return;
            }
            if(message.contains("First") || message.contains("Second")){
                String[] response = message.split(":");
                Integer id = Integer.parseInt(response[1]);
                ((GameScene)SceneManager.getInstance().getCurrentScene()).executeCardCalculation(id, message.contains("First"));
                return;
            }
            if(message.contains("Score")){
                String[] response = message.split(":");
                Integer score = Integer.parseInt(response[1]);
                ((GameScene)SceneManager.getInstance().getCurrentScene()).showFinishPopupMultiplayer(score);
            }
        }

    }

    void dismissWaitingRoom() {
        mWaitRoomDismissedFromCode = true;
        finishActivity(RC_WAITING_ROOM);
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

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        showAlert(getString(R.string.error), getString(R.string.game_problem));
        SceneManager.getInstance().reloadMenuScene();
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

    private void showWaitingRoom(Room room) {
        mWaitRoomDismissedFromCode = false;

        // minimum number of players required for our game
        final int MIN_PLAYERS = 2;
        Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    public void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_LOADING);
        this.keepScreenOn();
        //resetGameVars();
        this.getGamesClient().createRoom(rtmConfigBuilder.build());
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        showAlert(getString(R.string.error), getString(R.string.game_problem));
        SceneManager.getInstance().reloadMenuScene();
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
        SceneManager.getInstance().reloadMenuScene();
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

    void updateRoom(Room room) {
        mParticipants = room.getParticipants();
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
                if (responseCode == GameActivity.RESULT_OK) {
                    // player wants to start playing
                    Log.d(TAG, "Starting game because user requested via waiting room UI.");
                    //TODO: Boardcast start and start game.
                    // let other players know we're starting.
                    broadcastStart();
                    startGame();
                    // start the game!

                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player actively indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == GameActivity.RESULT_CANCELED) {
                    /* Dialog was cancelled (user pressed back key, for
                     * instance). In our game, this means leaving the room too. In more
                     * elaborate games,this could mean something else (like minimizing the
                     * waiting room UI but continue in the handshake process). */
                    leaveRoom();
                }

                break;
        }

    }

    private void setDefaultQuickplaySettings(){
        DIFFICULTY = 1;
        THEME = 1;
    }

    void startGame() {
        //updateScoreDisplay();
        //broadcastScore(false);
        setDefaultQuickplaySettings();
        SceneManager.getInstance().loadGameScene(this.mEngine);
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
            SceneManager.getInstance().reloadMenuScene();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);
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
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_LOADING);
        keepScreenOn();
        //TODO: Reset game.
        //resetGameVars();
        getGamesClient().createRoom(rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            SceneManager.getInstance().reloadMenuScene();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(GamesClient.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Leave the room.
    public void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        //mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            getGamesClient().leaveRoom (this, mRoomId);
            mRoomId = null;
            SceneManager.getInstance().reloadMenuScene();
        } else {
           SceneManager.getInstance().reloadMenuScene();
        }
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_LOADING);
        keepScreenOn();
        //Reset Game.
        getGamesClient().joinRoom(roomConfigBuilder.build());
    }
}
