package com.reflectmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.reflectmobile.R;
import com.reflectmobile.data.Moment;
import com.reflectmobile.utility.NetworkManager.HttpGetImageTask;
import com.reflectmobile.utility.NetworkManager.HttpGetTask;
import com.reflectmobile.utility.NetworkManager.HttpImageTaskHandler;
import com.reflectmobile.utility.NetworkManager.HttpTaskHandler;

public class MomentActivity extends BaseActivity {

	private String TAG = "MomentActivity";

	private Moment moment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// It is important to set content view before calling super.onCreate
		// because BaseActivity uses references to side menu
		setContentView(R.layout.activity_moment);
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

		int momentId = getIntent().getIntExtra("moment_id", 0);

		// Retreive data from the web
		final HttpTaskHandler getMomentHandler = new HttpTaskHandler() {
			@Override
			public void taskSuccessful(String result) {
				// Parse JSON to the list of communities
				moment = Moment.getMomentInfo(result);
				setTitle(moment.getName());
				GridView parentView = (GridView) findViewById(R.id.parentView);
				parentView.setAdapter(new ImageAdapter(MomentActivity.this));
			}

			@Override
			public void taskFailed(String reason) {
				Log.e(TAG, "Error within GET request: " + reason);
			}
		};

		new HttpGetTask(getMomentHandler)
				.execute("http://rewyndr.truefitdemo.com/api/moments/"
						+ momentId);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.moment_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
		// Handle action buttons
		// switch (item.getItemId()) {
		// case :
		//
		// default:
		// return super.onOptionsItemSelected(item);
		// }
	}

	private class ImageAdapter extends BaseAdapter {

		private String TAG = "Image";

		private Context mContext;
		private Drawable[] mDrawables;

		public ImageAdapter(Context context) {

			mContext = context;

			mDrawables = new Drawable[moment.getNumOfPhotos()];
			for (int count = 0; count < moment.getNumOfPhotos(); count++) {
				final int index = count;

				new HttpGetImageTask(new HttpImageTaskHandler() {
					private int drawableIndex = index;

					@Override
					public void taskSuccessful(Drawable drawable) {
						mDrawables[drawableIndex] = drawable;
						notifyDataSetChanged();
					}

					@Override
					public void taskFailed(String reason) {
						Log.e(TAG, "Error downloading the image");
					}
				}).execute(moment.getPhoto(count).getImageMediumThumbURL());

			}

		}

		@Override
		public int getCount() {
			return moment.getNumOfPhotos();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		private class ImageViewHolder {
			public ImageView image;
			public int position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			// If there is no view to recycle - create a new one
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.moment_photo,
						parentView, false);
				final ImageViewHolder holder = new ImageViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.photo);
				holder.image.setScaleType(ScaleType.CENTER_CROP);
				holder.image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = ((ImageViewHolder) v.getTag()).position;
						Intent intent = new Intent(mContext, PhotoActivity.class);
						intent.putExtra("photo_id", moment.getPhoto(position).getId());
						intent.putExtra("moment_id", moment.getId());
						mContext.startActivity(intent);
					}
				});
				convertView.setTag(holder);
			}
			
			final ImageViewHolder holder = (ImageViewHolder) convertView
					.getTag();
			holder.image.setImageDrawable(mDrawables[position]);
			holder.position = position;

			return convertView;
		}

	}

}
