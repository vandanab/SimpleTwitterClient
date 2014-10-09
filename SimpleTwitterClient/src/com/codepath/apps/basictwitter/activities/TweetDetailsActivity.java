package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.NetworkMonitor;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailsActivity extends Activity {
	private ImageView ivProfileImage;
	private ImageView ivDetailsMedia;
	private TextView tvScreenName;
	private TextView tvName;
	private EditText etReply;
	private TextView tvNumChars;
	private TextView tvBody;
	private TextView tvCreatedAt;
	private TextView tvRetweetedBy;
	private TextView tvRetweets;
	private TextView tvFavorites;
	private TwitterClient client;
	private Tweet t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);
		t = (Tweet) getIntent().getSerializableExtra("tweet");
		populateView();
		client = TwitterApplication.getRestClient();
		setUpTextChangeListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_details, menu);
		tvNumChars = new TextView(this);
		tvNumChars.setPadding(0, 0, 15, 0);
		tvNumChars.setTextSize(12);
        final MenuItem menuItem = menu.findItem(R.id.miNumCharsReply);
        menuItem.setActionView(tvNumChars).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        tvNumChars.setText(getString(R.string.num_chars_default));
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.miTweetSendAction:
    		saveTweet();
    		return true;
    	default: return super.onOptionsItemSelected(item);
    	}
    }

	private void populateView() {
		User u = t.getUser();
    	ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
    	ivDetailsMedia = (ImageView) findViewById(R.id.ivDetailsMedia);
    	tvScreenName = (TextView) findViewById(R.id.tvScreenName);
    	tvName = (TextView) findViewById(R.id.tvName);
    	tvBody = (TextView) findViewById(R.id.tvBody);
		tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
		tvRetweetedBy = (TextView) findViewById(R.id.tvRetweetedBy);
		tvRetweets = (TextView) findViewById(R.id.tvRetweetsCount);
		tvFavorites = (TextView) findViewById(R.id.tvFavoritesCount);
    	etReply = (EditText) findViewById(R.id.etReply);

    	ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(u.getProfileImageUrl(), ivProfileImage);
		tvName.setText(Html.fromHtml("<b>" + u.getName() + "</b>"));
		tvScreenName.setText("@" + u.getScreenName());
		tvBody.setText(t.getBody());
		tvCreatedAt.setText(t.getCreatedAt().toString());
		
		if (t.isRetweet()) {
			tvRetweetedBy.setText(t.getRetweetedBy() + " retweeted");
			tvRetweetedBy.setVisibility(View.VISIBLE);
			tvRetweetedBy.setLayoutParams(getLayoutParams(View.VISIBLE));
		} else {
			tvRetweetedBy.setVisibility(View.INVISIBLE);
			tvRetweetedBy.setLayoutParams(getLayoutParams(View.INVISIBLE));
		}
		
		if (t.getMediaUrl() != null) {
			ivDetailsMedia.setImageResource(android.R.color.transparent);
			imageLoader.displayImage(t.getMediaUrl(), ivDetailsMedia);
			ivDetailsMedia.setVisibility(View.VISIBLE);
			ivDetailsMedia.setLayoutParams(getLayoutParamsImage(View.VISIBLE));
		} else {
			ivDetailsMedia.setVisibility(View.INVISIBLE);
			ivDetailsMedia.setLayoutParams(getLayoutParamsImage(View.INVISIBLE));
		}
		
		tvRetweets.setText(String.valueOf(t.getRetweetCount()));
		tvFavorites.setText(String.valueOf(t.getFavoritesCount()));
		
		etReply.setText("@" + u.getScreenName() + " ");
		etReply.setSelection(etReply.getText().length());
	}

	private RelativeLayout.LayoutParams getLayoutParamsImage(int visible) {
		if (visible == View.VISIBLE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 480);
			params.addRule(RelativeLayout.BELOW, R.id.tvBody);
			params.setMargins(10, 0, 10, 0);
			return params;
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0);
			params.addRule(RelativeLayout.BELOW, R.id.tvBody);
			params.setMargins(0, 0, 0, 0);
			return params;
		}
	}

	private void setUpTextChangeListener() {
	    etReply.addTextChangedListener(new TextWatcher()
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
		String status = etReply.getText().toString();
		if(!NetworkMonitor.isNetworkAvailable(this)) {
			Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
			return;
		}
		client.postReply(status, t.getTid(), new JsonHttpResponseHandler() {

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

	private RelativeLayout.LayoutParams getLayoutParams(int visibility) {
		if (visibility == View.VISIBLE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			params.setMargins(0, 5, 0, 0);
			return params;
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			return params;
		}
	}
}
