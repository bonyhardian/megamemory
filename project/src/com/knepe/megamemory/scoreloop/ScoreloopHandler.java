package com.knepe.megamemory.scoreloop;

import android.app.Activity;
import android.util.Log;
import com.knepe.megamemory.GameActivity;
import com.scoreloop.client.android.core.controller.*;
import com.scoreloop.client.android.core.model.Ranking;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.SearchList;
import com.scoreloop.client.android.core.model.Session;
import org.andengine.ui.activity.BaseGameActivity;

import java.util.ArrayList;
import java.util.List;

public class ScoreloopHandler {

    private GameActivity activity;

    public ScoreloopHandler(GameActivity _activity) {
        activity = _activity;
    }

    public void submitScore(int scoreValue) {
        Log.d("Scoreloop", "submitting score");
        final Score score = new Score((double) scoreValue, null);
        score.setLevel(1);
        final ScoreController myScoreController = new ScoreController(
                new ScoreSubmitObserver());
        myScoreController.submitScore(score);
    }

    public void getRankingForScore(int scoreValue) {
        Score score = new Score((double) scoreValue, null);
        RankingController controller = new RankingController(
                new RankingRequestObserver());
        controller.loadRankingForScore(score);
    }

    public void getGlobalHighscores() {
        ScoresController myScoresController = new ScoresController(
                new GlobalRankObserver());
        myScoresController.setSearchList(SearchList.getGlobalScoreSearchList());
        myScoresController.setRangeLength(15);
        myScoresController.loadRangeForUser(Session.getCurrentSession()
                .getUser());
    }

    public void getTodayHighscores() {
        ScoresController myScoresController = new ScoresController(new DailyRankObserver());
        myScoresController.setSearchList(SearchList.getTwentyFourHourScoreSearchList());
        myScoresController.setRangeLength(15);
        myScoresController.loadRangeForUser(Session.getCurrentSession().getUser());
    }

    public void getLocalHighscores() {
        ScoresController myScoresController = new ScoresController(new DailyRankObserver());
        myScoresController.setSearchList(SearchList.getLocalScoreSearchList());
        myScoresController.setMode(0);
        myScoresController.setRangeLength(10);
        myScoresController.loadRangeAtRank(1);
    }

    private class RankingRequestObserver implements RequestControllerObserver {

        @Override
        public void requestControllerDidFail(RequestController arg0,
                                             Exception arg1) {

        }

        @Override
        public void requestControllerDidReceiveResponse(
                RequestController controller) {
            Ranking ranking = ((RankingController) controller).getRanking();
            final int rank = ranking.getRank();
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    private class ScoreSubmitObserver implements RequestControllerObserver {

        @Override
        public void requestControllerDidFail(
                final RequestController requestController,
                final Exception exception) {
            Log.d("Scoreloop",
                    "score submitted exception " + exception.getMessage());
        }

        @Override
        public void requestControllerDidReceiveResponse(
                final RequestController requestController) {
            Log.d("Scoreloop", "score submitted successsfully");

        }
    }

    private class DailyRankObserver implements RequestControllerObserver {

        @Override
        public void requestControllerDidFail(RequestController controller,
                                             Exception arg1) {
        }

        @Override
        public void requestControllerDidReceiveResponse(
                RequestController controller) {
            final List<Score> retrievedScores = ((ScoresController) controller)
                    .getScores();

            final List<String> scoreList = new ArrayList<String>();
            int i = 0;
            for (final Score score : retrievedScores) {
                scoreList.add(++i + ". " + String.valueOf(score.getResult()));
            }

            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    // Submit the scoreList to your game...

                }
            });
        }
    }

    private class GlobalRankObserver implements RequestControllerObserver {

        @Override
        public void requestControllerDidFail(RequestController arg0,
                                             Exception arg1) {

        }

        @Override
        public void requestControllerDidReceiveResponse(
                RequestController controller) {
            final List<Score> retrievedScores = ((ScoresController) controller)
                    .getScores();
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    // Submit the scoreList to your game...
                }
            });
        }

    }
}
