package com.codepath.apps.basictwitter.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.NetworkMonitor;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends FragmentActivity implements
		TweetsListFragment.OnImageClickListener {
	private static final int COMPOSE_REQUEST = 2;
	private ImageView ivCoverPhoto;
	private ImageView ivProfileImage;
	private TextView tvScreenName;
	private TextView tvName;
	private TextView tvTweetsCount;
	private TextView tvFollowingCount;
	private TextView tvFollowersCount;
	private TextView tvTagline;

	private TwitterClient client;
	private ImageLoader imageLoader;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		client = TwitterApplication.getRestClient();
		imageLoader = ImageLoader.getInstance();
		user = (User) getIntent().getSerializableExtra("user");
		initializeViews();
		populateUserInfo();
	}

	private void setupFragment() {
		UserTimelineFragment fragment = (UserTimelineFragment) getSupportFragmentManager()
    			.findFragmentById(R.id.fragmentUserTimeline);
    	if (fragment != null) {
    		fragment.populateTimeline(user, 1L, 0, false);
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} if (id == R.id.miCompose) {
    		composeTweet();
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeViews() {
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		ivProfileImage.setImageResource(android.R.color.transparent);
		ivCoverPhoto = (ImageView) findViewById(R.id.ivCoverPhoto);
		ivCoverPhoto.setImageResource(android.R.color.transparent);
    	tvScreenName = (TextView) findViewById(R.id.tvScreenName);
    	tvName = (TextView) findViewById(R.id.tvName);
    	tvTweetsCount = (TextView) findViewById(R.id.tvTweetsCount);
    	tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
    	tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
    	tvTagline = (TextView) findViewById(R.id.tvTagline);
	}

	private void populateUserInfo() {
		final SharedPreferences prefs = getSharedPreferences(LoginActivity.USER_PREFS,  0);
		if (user != null) {
			getProfileBanner();
			setupFragment();
			populateUserInfoViews();
		} else {
			if (NetworkMonitor.isNetworkAvailable(this)) {
				client.getUserInfo(new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("DEBUG", e.toString());
						Log.d("DEBUG", s.toString());
					}
	
					@Override
					public void onSuccess(JSONObject jsonResponse) {
						user = User.fromJSON(jsonResponse);
						getProfileBanner();
						setupFragment();
						populateUserInfoViews();
						// update shared prefs
						SharedPreferences.Editor editor = prefs.edit();
					    editor.putString("screen_name", user.getScreenName());
					    editor.putString("name", user.getName());
					    editor.putLong("uid", user.getUid());
					    editor.putString("profile_image_url", user.getProfileImageUrl());
					    editor.commit();
					}
				});
			} else {
				user = User.fromSharedPreferences(prefs);
				getProfileBanner();
				setupFragment();
				populateUserInfoViews();
			}
		}
	}

	private void getProfileBanner() {
		if (NetworkMonitor.isNetworkAvailable(this)) {
			client.getProfileBanner(user.getUid(), new JsonHttpResponseHandler() {
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}

				@Override
				public void onSuccess(JSONObject jsonResponse) {
					String url = null;
					try {
						JSONObject sizes = jsonResponse.getJSONObject("sizes");
						if (sizes != null) {
							url = sizes.getJSONObject("mobile").getString("url");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (url != null) {
						user.setProfileBannerUrl(url);
						ivCoverPhoto.setVisibility(View.VISIBLE);
						imageLoader.displayImage(url, ivCoverPhoto);
					}
				}
			});
		}
	}
	
	private void populateUserInfoViews() {
		getActionBar().setTitle("@" + user.getScreenName());
		imageLoader.displayImage(user.getProfileImageUrl(), ivProfileImage);
		tvName.setText(Html.fromHtml("<b>" + user.getName() + "</b>"));
		tvScreenName.setText("@" + user.getScreenName());
		tvTweetsCount.setText(String.valueOf(user.getTweetsCount()));
		tvFollowingCount.setText(String.valueOf(user.getFollowingCount()));
		tvFollowersCount.setText(String.valueOf(user.getFollowersCount()));
		tvTagline.setText(user.getTagLine());
	}

	private void composeTweet() {
    	Intent i = new Intent(this, ComposeActivity.class);
    	startActivityForResult(i, COMPOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == COMPOSE_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			UserTimelineFragment fragment = (UserTimelineFragment) getSupportFragmentManager()
    	    			.findFragmentById(R.id.fragmentUserTimeline);
    	    	if (fragment != null) {
    	    		fragment.refreshTimeline();
    	    	}
    		}
    	}	
    }

	public void onProfileImageClicked(View v) {
    	TweetsListFragment fragment = (TweetsListFragment) getSupportFragmentManager()
    			.findFragmentById(R.id.fragmentUserTimeline);
    	if (fragment != null) {
    		fragment.onProfileImageClicked(v);
    	}
    }

	public void replyToTweet(View v) {
    	
    }

    public void retweet(View v) {
    	TweetsListFragment fragment = (TweetsListFragment) getSupportFragmentManager()
    			.findFragmentById(R.id.fragmentUserTimeline);
    	if (fragment != null) {
    		fragment.retweet(v);
    	}
    }

    public void markFavorite(View v) {
    	TweetsListFragment fragment = (TweetsListFragment) getSupportFragmentManager()
    			.findFragmentById(R.id.fragmentUserTimeline);
    	if (fragment != null) {
    		fragment.markFavorite(v);
    	}
    }
    
	@Override
	public void onProfileImageClicked(User user) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("user", user);
		startActivity(i);
	}
}
