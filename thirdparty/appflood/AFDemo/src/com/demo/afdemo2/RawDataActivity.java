package com.demo.afdemo2;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.demo.afdemo2.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class RawDataActivity extends Activity{
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
	private TextView show1 = null;
	private TextView show2 = null;
	private TextView show3 = null;
	private TextView show4 = null;
	private TextView show5 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.rawdata);
		if (AFDemoActivity.jsonArray == null) {
			finish();
		}
		show1 = new TextView(this);
		show2 = new TextView(this);
		show3 = new TextView(this);
		show4 = new TextView(this);
		show5 = new TextView(this);
		show1 = (TextView)findViewById(R.id.show1);
		show2 = (TextView)findViewById(R.id.show2);
		show3 = (TextView)findViewById(R.id.show3);
		show4 = (TextView)findViewById(R.id.show4);
		show5 = (TextView)findViewById(R.id.show5);
		
		JSONArray cuArray = AFDemoActivity.jsonArray;
		Log.v("AFDEMO1", cuArray.toString());
		try {
		    Log.v("AFDEMO1", cuArray.length()+"");
			for (int i = 0; i < cuArray.length(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject json = cuArray.getJSONObject(i);
				map.put("name", json.getString("name"));
				map.put("type", json.getString("app_type"));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (list.isEmpty()) {
			finish();
		}
		//Log.v("AFDEMO", list.size()+"");
		show1.setText(list.get(0).get("name")+"\n"+list.get(0).get("type"));
		show2.setText(list.get(1).get("name")+"\n"+list.get(1).get("type"));
		show3.setText(list.get(2).get("name")+"\n"+list.get(2).get("type"));
		show4.setText(list.get(3).get("name")+"\n"+list.get(3).get("type"));
		show5.setText(list.get(4).get("name")+"\n"+list.get(4).get("type"));
	}
}
