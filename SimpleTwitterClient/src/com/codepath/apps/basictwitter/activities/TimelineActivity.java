package com.codepath.apps.basictwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.listeners.SupportFragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;

public class TimelineActivity extends ActionBarActivity implements
		TweetsListFragment.OnImageClickListener {
	private static final int COMPOSE_REQUEST = 2;
	private static final int DETAILS_REQUEST = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab homeTimelineTab = actionBar
		    .newTab()
		    .setText("Home")
		    .setTag("HomeTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this,
                        "home", HomeTimelineFragment.class));

		actionBar.addTab(homeTimelineTab);
		actionBar.selectTab(homeTimelineTab);

		Tab mentionsTimelineTab = actionBar
		    .newTab()
		    .setText("Mentions")
		    .setTag("MentionsTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this,
                        "mentions", MentionsTimelineFragment.class));
		actionBar.addTab(mentionsTimelineTab);
		
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

    private TweetsListFragment getFragment() {
    	TweetsListFragment fragment = (TweetsListFragment) getSupportFragmentManager()
    			.findFragmentByTag("home");
    	if (fragment == null) {
    		fragment = (TweetsListFragment) getSupportFragmentManager()
        			.findFragmentByTag("mentions");
    	}
    	return fragment;
    }

    private void composeTweet() {
    	Intent i = new Intent(this, ComposeActivity.class);
    	startActivityForResult(i, COMPOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == COMPOSE_REQUEST || requestCode == DETAILS_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			TweetsListFragment fragment = getFragment();
    	    	if (fragment != null) {
    	    		fragment.refreshTimeline();
    	    	}
    		}
    	}
    }

    public void replyToTweet(View v) {
    	Tweet t = (Tweet) v.getTag();
    	Intent i = new Intent(this, TweetDetailsActivity.class);
		i.putExtra("tweet", t);
		startActivityForResult(i, DETAILS_REQUEST);
    }

    public void retweet(View v) {
    	TweetsListFragment fragment = getFragment();
    	if (fragment != null) {
    		fragment.retweet(v);
    	}
    }

    public void markFavorite(View v) {
    	TweetsListFragment fragment = getFragment();
    	if (fragment != null) {
    		fragment.markFavorite(v);
    	}
    }

    public void onProfileView(MenuItem mi) {
    	Intent i = new Intent(this, ProfileActivity.class);
    	startActivity(i);
    }

    public void onProfileImageClicked(View v) {
    	TweetsListFragment fragment = getFragment();
    	if (fragment != null) {
    		fragment.onProfileImageClicked(v);
    	}
    }
    
	@Override
	public void onProfileImageClicked(User user) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("user", user);
		startActivity(i);
	}
}
