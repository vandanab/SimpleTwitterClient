package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.codepath.apps.basictwitter.models.Tweet.Source;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
	private ImageView ivProfileImage;
	private TextView tvScreenName;
	private TextView tvName;
	private EditText etStatus;
	private TwitterClient client;
	private TextView tvNumChars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		populateView();
		client = TwitterApplication.getRestClient();
		setUpTextChangeListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_compose, menu);
		tvNumChars = new TextView(this);
		tvNumChars.setPadding(0, 0, 15, 0);
		tvNumChars.setTextSize(12);
        final MenuItem menuItem = menu.findItem(R.id.miNumCharsCompose);
        menuItem.setActionView(tvNumChars).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        tvNumChars.setText(getString(R.string.num_chars_default));
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

    private void setUpTextChangeListener() {
	    etStatus.addTextChangedListener(new TextWatcher()
	    {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	
	        @Override
	        public void afterTextChanged(Editable s)
	        {
	        	tvNumChars.setText(String.valueOf(140 - s.toString().length()));
	        }
	    });
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
				Tweet tweet = Tweet.fromJSON(jsonResponse, Tweet.Source.COMPOSE);
				i.putExtra("id", Long.valueOf(tweet.getTid() - 1L));
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
