package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
	private ImageView ivProfileImage;
	private TextView tvScreenName;
	private TextView tvName;
	private EditText etStatus;
	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		populateView();
		client = TwitterApplication.getRestClient();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_compose, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.miTweetAction:
    		saveTweet();
    		return true;
    	default: return super.onOptionsItemSelected(item);
    	}
    }

	private void saveTweet() {
		String status = etStatus.getText().toString();
		client.postUpdate(status, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
				finish();
			}

			@Override
			public void onSuccess(JSONObject jsonResponse) {
				Intent i = new Intent();
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Tweet tweet = Tweet.fromJSON(jsonResponse);
				i.putExtra("id", tweet.getId() - 1);
				setResult(RESULT_OK, i);
				finish();
			}

		});
	}

	private void populateView() {
		SharedPreferences prefs = getSharedPreferences(LoginActivity.USER_PREFS,  0);
    	User user = User.fromSharedPreferences(prefs);
    	ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
    	tvScreenName = (TextView) findViewById(R.id.tvScreenName);
    	tvName = (TextView) findViewById(R.id.tvName);
    	etStatus = (EditText) findViewById(R.id.etStatus);
    	ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(user.getProfileImageUrl(), ivProfileImage);
		tvName.setText(Html.fromHtml("<b>" + user.getName() + "</b>"));
		tvScreenName.setText("@" + user.getScreenName());
	}
}
