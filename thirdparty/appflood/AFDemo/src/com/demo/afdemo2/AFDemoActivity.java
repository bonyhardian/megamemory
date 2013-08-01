package com.demo.afdemo2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appflood.AFBannerView;
import com.appflood.AppFlood;
import com.appflood.AppFlood.AFRequestDelegate;

public class AFDemoActivity extends Activity implements OnClickListener {

	private AFBannerView afBannerView = null;
	private Button fullButton = null;
	private Button downButton = null;
	private Button topButton = null;
	private Button conButton = null;
	private Button desButton = null;
	private Button baMButton = null;
	private Button baSButton = null;
	private Button queryButton = null;
	private Button consumeButton = null;
	private Button preloadButton = null;
	private Button getTypeButton = null;
	private TextView landTextView = null;
	private TextView porTextView = null;
	private Spinner initSpinner = null;
	private AFRequestDelegate requestDelegate = null;
	private String LOGTAG = "Demo";
	private RelativeLayout relativeLayout = null;
	private ArrayList<String> appKeyList, secretList;
//	private Spinner selectAppSpinner;
	private String appid = "T24NcUADLYVbSsMv";
	private String secretkey = "tQcJMhgb308L50d40936";
//	776£º//	App Key: T24NcUADLYVbSsMv //	Secret Key: tQcJMhgb308L50d40936
//	1261£º//	App Key: sYhsmlXhBHGNZvJm //	Secret Key: jkWN1GXS4edL512dc4c5
//	1254£º//	App Key: sl9LsCP0gAftxYZm //	Secret Key: J77LWkTe4e6L512db7ca


	public static JSONArray jsonArray = new JSONArray();
	String appArray[] = //{"VtuJbs75kc1vSGDy","72LAizPc2dfL50cda5b1","ARtb7jncmaJ1Naw2","ndFoTny82e3L50ce7f1c","WQovhufmOuzKy0KL" ,"kCXNPC0h2e4L50ce83cb","9KZtTlMflBLABpWj","0NX6TUZK302L50d2e1d2","SSa4JuIbVXDKNbwb", "vDU0X0be338L50e7b61a"};
	{ "XP6euqU8soFlaDL9", "KabhOssf27cL50d3f015", "Z1Lmowg50RnTd8qM", "b5bvnmfN259L50c596b4", "rvRncALZgIciy6cm",
			"XyW89RYO258L50c59655", "l0yQsXCuSlNBUead", "bm1CzfmZ24cL50c54f99", "KNqTCryZTDKNhDJE", "X4V54V5d2b4L50da7235",
			"6ncAofVGzydbUxTi", "Sxd5Q0Px25fL50c5b109", "JT1oUy4GGZxM5bqz", "TA0WzBi41f4L5099f882" }; // test2

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_land);

		landTextView = (TextView) findViewById(R.id.showLand);
		porTextView = (TextView) findViewById(R.id.showport);
		fullButton = (Button) findViewById(R.id.fullscreen);
		fullButton.setOnClickListener(this);
		downButton = (Button) findViewById(R.id.showPanelDown);
		downButton.setOnClickListener(this);
		topButton = (Button) findViewById(R.id.showPanelTop);
		topButton.setOnClickListener(this);
		conButton = (Button) findViewById(R.id.connect);
		conButton.setOnClickListener(this);
		desButton = (Button) findViewById(R.id.destroy);
		desButton.setOnClickListener(this);
		baMButton = (Button) findViewById(R.id.showBannerMiddle);
		baMButton.setOnClickListener(this);
		baSButton = (Button) findViewById(R.id.showBannerSmall);
		baSButton.setOnClickListener(this);
		queryButton = (Button) findViewById(R.id.query);
		queryButton.setOnClickListener(this);
		consumeButton = (Button) findViewById(R.id.consume);
		consumeButton.setOnClickListener(this);
		preloadButton = (Button) findViewById(R.id.preload);
		preloadButton.setOnClickListener(this);
		getTypeButton = (Button) findViewById(R.id.gettype);
		getTypeButton.setText("getADData");
		getTypeButton.setOnClickListener(this);
		initSpinner = (Spinner) findViewById(R.id.spinner);
//		selectAppSpinner = (Spinner) findViewById(R.id.selectapp);
		relativeLayout = (RelativeLayout) findViewById(R.id.afdemoly);

		List<String> spinnerList = new ArrayList<String>();
		spinnerList.add("NONE");
		spinnerList.add("TAB_GAME");
		spinnerList.add("TAB_APP");
		spinnerList.add("GAME");
		spinnerList.add("APP");
		spinnerList.add("ALL");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		initSpinner.setAdapter(adapter);
		initSpinner.setPrompt("Init");

		AFRequestDelegate initDelegate = new AFRequestDelegate() {
			@Override
			public void onFinish(JSONObject arg0) {
				Log.v("TOKEN", arg0.toString());
			}
		};

		// AppFlood.showList(this, AppFlood.LIST_ALL);
		initSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedString = parent.getItemAtPosition(position).toString();
				if (selectedString == "TAB_GAME") {
					AppFlood.showList(AFDemoActivity.this, AppFlood.LIST_TAB_GAME);
				} else if (selectedString == "TAB_APP") {
					AppFlood.showList(AFDemoActivity.this, AppFlood.LIST_TAB_APP);
				} else if (selectedString == "GAME") {
					AppFlood.showList(AFDemoActivity.this, AppFlood.LIST_GAME);
				} else if (selectedString == "APP") {
					AppFlood.showList(AFDemoActivity.this, AppFlood.LIST_APP);
				} else if (selectedString == "ALL") {
					AppFlood.showList(AFDemoActivity.this, AppFlood.LIST_ALL);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Log.i(LOGTAG, "initialize appflood");
		requestDelegate = new AFRequestDelegate() {

			@Override
			public void onFinish(final JSONObject json) {
				if (json != null && json.length() != 0) {
					json.optInt("point");
					// Integer.valueOf(json.get("point").toString());
					Log.v(LOGTAG, "JSON Finished =================================>" + json.toString());
					runOnUiThread(new Runnable() {
						public void run() {
							landTextView.setText(json.toString());
							porTextView.setText(json.toString());
						}
					});

				}
			}
		};

		AppFlood.setEventDelegate(new AppFlood.AFEventDelegate() {

			public void onClose(JSONObject arg0) {
				Log.v(LOGTAG, "JSON Closed =================================>" + arg0.toString());
			}

			public void onClick(JSONObject arg0) {
				Log.v(LOGTAG, "JSON Clicked =================================>" + arg0.toString());
			}
		});
		appKeyList = new ArrayList<String>();
		secretList = new ArrayList<String>();
		appKeyList.add("None");
		secretList.add("NONE");
		for (int i = 0; i < (appArray.length + 1) / 2; i++) {
			appKeyList.add(appArray[2 * i]);
			secretList.add(appArray[2 * i + 1]);
		}
		
		AppFlood.initialize(AFDemoActivity.this, appid, secretkey, AppFlood.AD_ALL);
		AppFlood.showNotification(this, true, AppFlood.NOTIFICATION_STYLE_TEXT);

	}

	public void showBanner(AFBannerView afBannerView, Activity context, int type) {
		afBannerView = new AFBannerView(AFDemoActivity.this, type, true);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (type == AppFlood.BANNER_MIDDLE) {
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}

		relativeLayout.addView(afBannerView, params);
	}

	public void showRaw() {
		Intent intent = new Intent();
		intent.setClass(AFDemoActivity.this, RawDataActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "exit()");
		menu.add(1, 1, 0, "appflood demo 1.41");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			AppFlood.destroy();
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			landTextView.setVisibility(View.GONE);
			porTextView.setVisibility(View.VISIBLE);
		} else {
			landTextView.setVisibility(View.VISIBLE);
			porTextView.setVisibility(View.GONE);
		}

		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		AppFlood.destroy();
		Log.v(LOGTAG, "=================================================> destroying Appflood");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fullscreen:
			Log.v(LOGTAG, "=================================================> showing Full Screen Ads");
			Toast.makeText(AFDemoActivity.this, "showing Full Screen Ads", Toast.LENGTH_SHORT).show();

			AppFlood.showFullScreen(this);

			break;
		case R.id.showPanelDown:
			Log.v(LOGTAG, "=================================================> showing Panel Land Ads");
			Toast.makeText(AFDemoActivity.this, "showing Panel Land Ads", Toast.LENGTH_SHORT).show();

			AppFlood.showPanel(this, AppFlood.PANEL_BOTTOM);

			break;
		case R.id.showPanelTop:
			Log.v(LOGTAG, "=================================================> showing Panel Port Ads");
			Toast.makeText(AFDemoActivity.this, "showing Panel Port Ads", Toast.LENGTH_SHORT).show();

			AppFlood.showPanel(this, AppFlood.PANEL_TOP);

			break;
		case R.id.showBannerMiddle:
			Log.v(LOGTAG, "=================================================> showing Banner Middle Ads");
			Toast.makeText(AFDemoActivity.this, "showing Banner Middle Ads", Toast.LENGTH_SHORT).show();
			showBanner(afBannerView, AFDemoActivity.this, AppFlood.BANNER_MIDDLE);
			break;
		case R.id.showBannerSmall:
			Log.v(LOGTAG, "=================================================> showing Banner Small Ads");
			Toast.makeText(AFDemoActivity.this, "showing Banner Small Ads", Toast.LENGTH_SHORT).show();
			showBanner(afBannerView, AFDemoActivity.this, AppFlood.BANNER_SMALL);
			break;
		case R.id.connect:
			Log.v(LOGTAG, "=================================================> Testing connection");
			if (AppFlood.isConnected()) {
				Toast.makeText(AFDemoActivity.this, "Connection is OK!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(AFDemoActivity.this, "Connection is BAD!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.destroy:
			AppFlood.getADData(new AFRequestDelegate() {
				@Override
				public void onFinish(JSONObject arg0) {
					try {
						// Log.v("AFDEMO", arg0.toString());
						Log.v("AFDEMO", arg0.getString("success"));
						// Log.v("AFDEMO", arg0.get("data").toString());
						// jsonArray =
						// Utils.parseJsonArray(arg0.getString("data"));
						// showRaw();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			break;
		case R.id.preload:
			Log.v(LOGTAG, "=================================================> Preloading Appflood");
			AppFlood.preload(AppFlood.AD_FULLSCREEN, new AFRequestDelegate() {
				@Override
				public void onFinish(JSONObject arg0) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AFDemoActivity.this, "preload finished", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
			Toast.makeText(getApplicationContext(), "Preloading Appflood!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.query:
			Log.v(LOGTAG, "=================================================> Querying PPY Point!");
			AppFlood.queryAFPoint(requestDelegate);
			break;
		case R.id.consume:
			Log.v(LOGTAG, "=================================================> Consumming PPY Point");
			AppFlood.consumeAFPoint(1, requestDelegate);
			break;
		case R.id.gettype:
			Log.v(LOGTAG, "=================================================> Getting ADType!");
			AppFlood.getADData(new AFRequestDelegate() {
				@Override
				public void onFinish(JSONObject arg0) {
					Log.v("ADEMO", arg0.toString());
				}
			});
			break;
		default:
			break;
		}
	}
}
