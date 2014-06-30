package com.reflectmobile.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.reflectmobile.R;
import com.reflectmobile.utility.NetworkManager.HttpPostTask;
import com.reflectmobile.utility.NetworkManager.HttpTaskHandler;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddStoryActivity extends BaseActivity {

	private String TAG = "AddStoryActivity";

	private int photoId;
	private boolean addButtonPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		hasNavigationDrawer = false;
		setContentView(R.layout.add_story);
		super.onCreate(savedInstanceState);

		// Modify action bar title
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView title = (TextView) findViewById(titleId);
		title.setTextColor(getResources().getColor(R.color.yellow));
		title.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf"));

		// Set margin before title
		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) title
				.getLayoutParams();
		mlp.setMargins(5, 0, 0, 0);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ImageView view = (ImageView) findViewById(android.R.id.home);
		view.setPadding(10, 0, 0, 0);

		addButtonPressed = false;
		photoId = getIntent().getIntExtra("photo_id", 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_story_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_add_story:
			if (!addButtonPressed){
				addButtonPressed = true;
				addStory();
			}
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addStory() {
		Log.d(TAG, "PhotoId " + photoId);
		HttpTaskHandler httpPostTaskHandler = new HttpTaskHandler() {
			@Override
			public void taskSuccessful(String result) {
				Log.d("POST", result);
				finish();
			}

			@Override
			public void taskFailed(String reason) {
				Log.e("POST", "Error within POST request: " + reason);
			}
		};
		JSONObject storyData = new JSONObject();
		EditText storyEditText = (EditText) findViewById(R.id.story_text);
		String storyText = storyEditText.getText().toString();
		try {
			storyData.put("photo_id", photoId);
			storyData.put("memory_type", "story");
			storyData.put("memory_content", storyText);
		} catch (JSONException e) {
			Log.e(TAG, "Error forming JSON");
		}
		String payload = storyData.toString();
		new HttpPostTask(httpPostTaskHandler, payload)
				.execute("http://rewyndr.truefitdemo.com/api/memories");
	}
}
