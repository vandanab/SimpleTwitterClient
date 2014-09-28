package com.codepath.apps.basictwitter.listeners;

import com.codepath.apps.basictwitter.models.Tweet;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class TwitterEndlessScrollListener implements OnScrollListener {
	// The minimum amount of items to have below your current scroll position
	// before loading more.
	private int visibleThreshold = 5;
	// The current offset index of data you have loaded
	private long currentMaxId = 0L;
	// The total number of items in the dataset after the last load
	private int previousTotalItemCount = 0;
	// True if we are still waiting for the last set of data to load.
	private boolean loading = true;
	// Sets the starting page index
	private long startingMaxId = 0L;

	public TwitterEndlessScrollListener() {
	}

	public TwitterEndlessScrollListener(int visibleThreshold) {
		this.visibleThreshold = visibleThreshold;
	}

	public TwitterEndlessScrollListener(int visibleThreshold, long startMaxId) {
		this.visibleThreshold = visibleThreshold;
		this.startingMaxId = startMaxId;
		this.currentMaxId = startMaxId;
	}

	public TwitterEndlessScrollListener(long startMaxId) {
		this.startingMaxId = startMaxId;
		this.currentMaxId = startMaxId;
	}

	// This happens many times a second during a scroll, so be wary of the code you place here.
	// We are given a few useful parameters to help us work out if we need to load some more data,
	// but first we check if we are waiting for the previous load to finish.
	@Override
	public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount) {
		// If the total item count is zero and the previous isn't, assume the
		// list is invalidated and should be reset back to initial state
		if (totalItemCount < previousTotalItemCount) {
			this.currentMaxId = this.startingMaxId;
			this.previousTotalItemCount = totalItemCount;
			if (totalItemCount == 0) { this.loading = true; } 
		}

		// If it’s still loading, we check to see if the dataset count has
		// changed, if so we conclude it has finished loading and update the current page
		// number and total item count.
		if (loading && (totalItemCount > previousTotalItemCount)) {
			loading = false;
			previousTotalItemCount = totalItemCount;
			Tweet lastTweetInList = (Tweet) view.getAdapter().getItem(totalItemCount - 1);
			this.currentMaxId = lastTweetInList.getId();
		}
		
		// If it isn’t currently loading, we check to see if we have breached
		// the visibleThreshold and need to reload more data.
		// If we do need to reload some more data, we execute onLoadMore to fetch the data.
		if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
		    onLoadMore(this.currentMaxId + 1); // maxId is inclusive so adding 1.
		    loading = true;
		}
	}

	// Defines the process for actually loading more data based on page
	public abstract void onLoadMore(long maxId);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Don't take any action on changed
	}
}
