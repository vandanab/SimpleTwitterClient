package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.activities.TweetDetailsActivity;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.listeners.TwitterEndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;

public abstract class TweetsListFragment extends Fragment {
	private ArrayList<Tweet> tweets;
	private ListView lvTweets;
	private ArrayAdapter<Tweet> tweetsAdapter;
	private SwipeRefreshLayout swipeContainer;

	private OnImageClickListener listener;

	public interface OnImageClickListener {
		public void onProfileImageClicked(User user);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets_list, container,
				false /* don't attach to container yet */);
		// Assign view preferences
		lvTweets = (ListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdapter);
		setupScrollListener();
		setupSwipeListener(v);
		setUpListViewListeners();
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		tweets = new ArrayList<Tweet>();
		tweetsAdapter = new TweetArrayAdapter(getActivity(), tweets);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnImageClickListener) {
			listener = (OnImageClickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement TweetsListFragment.OnImageClickListener");
		}
	}

	public void onProfileImageClicked(View v) {
		listener.onProfileImageClicked((User) v.getTag());
	}

	public void addAll(ArrayList<Tweet> tweetsList) {
		tweets.addAll(tweetsList);
		tweetsAdapter.notifyDataSetChanged();
	}

	public void addAll(int insertIndex, ArrayList<Tweet> tweetsList) {
		tweets.addAll(insertIndex, tweetsList);
		tweetsAdapter.notifyDataSetChanged();
	}

	private void setupSwipeListener(View v) {
		swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Tweet t = tweetsAdapter.getItem(0);
    			long sinceId = t.getTid();
            	loadNewDataFromApi(sinceId);
            	swipeContainer.setRefreshing(false);
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
		lvTweets.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View item,
					int position, long id) {
				Tweet t = (Tweet) adapter.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), TweetDetailsActivity.class);
				i.putExtra("tweet", t);
		    	startActivity(i);
		    	return true;
			}
		});
	}

	public void markFavorite(View v) {
		TextView favorites = (TextView) v;
    	String numFavorites = favorites.getText().toString();
    	int num = (numFavorites != "" && Long.valueOf(numFavorites) != null) ? Long.valueOf(numFavorites).intValue() : 0;
    	boolean isSelected = favorites.isSelected();
    	if (isSelected) {
    		favorites.setText(String.valueOf(num + 1));
    	} else {
    		favorites.setText(String.valueOf(num - 1));
    	}
	}

	public void retweet(View v) {
		TextView retweets = (TextView) v;
    	String numRetweets = retweets.getText().toString();
    	int num = (numRetweets != "" && Long.valueOf(numRetweets) != null) ? Long.valueOf(numRetweets).intValue() : 0;
    	boolean isSelected = retweets.isSelected();
    	if (isSelected) {
    		retweets.setText(String.valueOf(num + 1));
    	} else {
    		retweets.setText(String.valueOf(num - 1));
    	}
	}

	public void refreshTimeline() {
		Tweet t = tweetsAdapter.getItem(0);
		long sinceId = t.getTid();
    	loadNewDataFromApi(sinceId);
	}

	public void goToTop() {
		lvTweets.setSelection(0);
	}

	public abstract void loadMoreDataFromApi(long maxId);

	public abstract void loadNewDataFromApi(long sinceId);
}
