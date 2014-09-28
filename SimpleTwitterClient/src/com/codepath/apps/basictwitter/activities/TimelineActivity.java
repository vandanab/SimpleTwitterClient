package com.codepath.apps.basictwitter.activities;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.listeners.TwitterEndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {
	private static final int COMPOSE_REQUEST = 2;
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> tweetsAdapter;
	private ListView lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		populateTimeline(1L);
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		tweetsAdapter = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(tweetsAdapter);
		setupScrollListener();
	}

	private void setupScrollListener() {
		lvTweets.setOnScrollListener(new TwitterEndlessScrollListener() {
			@Override
			public void onLoadMore(long maxId) {
				loadMoreDataFromApi(maxId);	
			}
		});
    }

	private void loadMoreDataFromApi(long maxId) {
		client.getHomeTimelinePaginated(maxId, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}

			@Override
			public void onSuccess(JSONArray jsonResponse) {
				tweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_timeline, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.miCompose:
    		composeTweet();
    		return true;
    	default: return super.onOptionsItemSelected(item);
    	}
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == COMPOSE_REQUEST) {
			if (resultCode == RESULT_OK) {
				long sinceId = getIntent().getLongExtra("id", 1);
				populateTimeline(sinceId);
				Toast.makeText(this, "Tweet composed!", Toast.LENGTH_SHORT).show();
			}
    	}
	}

	public void populateTimeline(long sinceId) {
		client.getHomeTimeline(sinceId, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}

			@Override
			public void onSuccess(JSONArray jsonResponse) {
				tweetsAdapter.clear();
				tweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
			}

		});
	}

    public void replyToTweet(View v) {
    	
    }

    public void retweet(View v) {
    	/*TextView retweets = (TextView) v;
    	String numRetweets = retweets.getText().toString();
    	int num = (numRetweets != "" && Long.valueOf(numRetweets) != null) ? Long.valueOf(numRetweets).intValue() : 0;
    	boolean isSelected = retweets.isSelected();
    	if (isSelected) {
    		retweets.setText(String.valueOf(num + 1));
    	} else {
    		retweets.setText(String.valueOf(num - 1));
    	}*/
    }

    public void markFavorite(View v) {
    	/*TextView favorites = (TextView) v;
    	String numFavorites = favorites.getText().toString();
    	int num = (numFavorites != "" && Long.valueOf(numFavorites) != null) ? Long.valueOf(numFavorites).intValue() : 0;
    	boolean isSelected = favorites.isSelected();
    	if (isSelected) {
    		favorites.setText(String.valueOf(num + 1));
    	} else {
    		favorites.setText(String.valueOf(num - 1));
    	}*/
    }

    private void composeTweet() {
    	Intent i = new Intent(this, ComposeActivity.class);
    	startActivityForResult(i, COMPOSE_REQUEST);
    }
}
