package com.knepe.megamemory.scoreloop;

import android.content.Intent;
import android.widget.Toast;
import com.knepe.megamemory.GameActivity;
import com.scoreloop.client.android.core.controller.TermsOfServiceController;
import com.scoreloop.client.android.core.controller.TermsOfServiceControllerObserver;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.TermsOfService;
import com.scoreloop.client.android.ui.EntryScreenActivity;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import org.andengine.ui.activity.BaseGameActivity;

public class ActionResolver implements IActionResolver {

    ScoreloopHandler handler;

    private GameActivity activity;

    public ActionResolver(GameActivity _activity) {
        activity = _activity;
        handler = new ScoreloopHandler(activity);
    }

    public TermsOfService.Status getTOS() {
        TermsOfService tos = Session.getCurrentSession()
                .getUsersTermsOfService();
        return tos.getStatus();

    }

//	@Override
//	public void showScoreloop() {
//		activity.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//
//					activity.startActivity(new Intent(activity,
//							EntryScreenActivity.class));
//
//				} catch (Exception e) {
//				}
//			}
//		});
//	}

    // NEW VERSION OF "showScoreloop", now handles the TOS control (THX fatal!)
    @Override
    public void showScoreloop() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getTOS() == TermsOfService.Status.ACCEPTED) {
                        activity.startActivity(new Intent(activity, EntryScreenActivity.class));
                    } else {
                        TermsOfServiceControllerObserver observer = new TermsOfServiceControllerObserver() {
                            @Override
                            public void termsOfServiceControllerDidFinish(
                                    TermsOfServiceController controller, Boolean accepted) {
                                try {
                                    if (accepted) {
                                        activity.startActivity(new Intent(activity, EntryScreenActivity.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(activity, "Accept Scoreloop to compare your score.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (NullPointerException ex) {
                                    //TODO: No Internet Connection, show error message

                                }
                            }
                        };
                        TermsOfServiceController controller = new TermsOfServiceController(observer);
                        controller.query(activity.getParent());
                    }
                } catch (Exception e) {}
            }
        });
    }

    @Override
    public void submitScore(final int mode, final int score) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getTOS() != TermsOfService.Status.ACCEPTED) {
                        TermsOfServiceControllerObserver observer = new TermsOfServiceControllerObserver() {
                            @Override
                            public void termsOfServiceControllerDidFinish(
                                    TermsOfServiceController controller, Boolean accepted) {
                                try {
                                    if(accepted){
                                        handler.submitScore(score);
                                        handler.getRankingForScore(score);
                                        Toast.makeText(activity, "Score submitted!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(activity, "You must accept Scoreloop Terms of Service to submit scores", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException ex) {
                                    //TODO: No Internet Connection, show error message

                                }
                            }
                        };
                        TermsOfServiceController controller = new TermsOfServiceController(observer);
                        controller.query(activity.getParent());
                    }

                    if(getTOS() == TermsOfService.Status.ACCEPTED){
                        handler.submitScore(score);
                        handler.getRankingForScore(score);
                        Toast.makeText(activity, "Score submitted!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void refreshScores() {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {

                    Toast.makeText(activity, "Refreshing scores",
                            Toast.LENGTH_SHORT).show();
                    handler.getGlobalHighscores();
                    handler.getTodayHighscores();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void bootstrap() {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {

                    handler.getGlobalHighscores();
                    handler.getTodayHighscores();

                    // Upload local scores, if any
                    ScoreloopManagerSingleton.get().submitLocalScores(null);
                } catch (Exception e) {
                }
            }
        });
    }
}
