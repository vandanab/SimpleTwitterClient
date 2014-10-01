package com.codepath.apps.basictwitter.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TweetDetailsActivity;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.listeners.TwitterEndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {
	private static final int COMPOSE_REQUEST = 2;
	private static final int DETAILS_REQUEST = 3;
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> tweetsAdapter;
	private ListView lvTweets;
	private SwipeRefreshLayout swipeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		tweetsAdapter = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(tweetsAdapter);
		initializeTimeline();
		setupScrollListener();
		setupSwipeListener();
		setUpListViewListeners();
	}

	private void initializeTimeline() {
		List<Tweet> tweets = Tweet.getAll();
		if (tweets.size() > 0) {
			tweetsAdapter.addAll(tweets);
		} else {
			populateTimeline(false);
		}
	}

	private void setupSwipeListener() {
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            	populateTimeline(true);
            } 
        });
	}

	private void setupScrollListener() {
		lvTweets.setOnScrollListener(new TwitterEndlessScrollListener() {
			@Override
			public void onLoadMore(long maxId) {
				loadMoreDataFromApi(maxId);	
			}
		});
    }

	private void setUpListViewListeners() {
    	// Delete items on long click on an item in the list.
		lvTweets.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View item,
					int position, long id) {
				Tweet t = (Tweet) adapter.getItemAtPosition(position);
				Intent i = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
				i.putExtra("tweet", t);
		    	startActivityForResult(i, DETAILS_REQUEST);
				return false;
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
    	if (requestCode == COMPOSE_REQUEST || requestCode == DETAILS_REQUEST) {
			if (resultCode == RESULT_OK) {
		    	populateTimeline(false);
				Toast.makeText(this, "Tweet composed!", Toast.LENGTH_SHORT).show();
			}
    	}
	}

	public void populateTimeline(final boolean isSwipe) {
		long sinceId = 1L;
		if (tweetsAdapter.getCount() > 0) {
			Tweet t = tweetsAdapter.getItem(0);
			sinceId = t.getTid();
		}
		if (!isNetworkAvailable()) {
			Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
    		return;
		}
		client.getHomeTimeline(sinceId, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}

			@Override
			public void onSuccess(JSONArray jsonResponse) {
				tweets.addAll(0, Tweet.fromJSONArray(jsonResponse));
				tweetsAdapter.notifyDataSetChanged();
				if (isSwipe) {
					swipeContainer.setRefreshing(false);
				}
			}

		});
	}

	private boolean isNetworkAvailable() {
    	ConnectivityManager connectivityManager = 
    			(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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
