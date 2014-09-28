package com.codepath.apps.basictwitter.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	private static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvBody;
		TextView tvName;
		TextView tvScreenName;
		TextView tvCreatedAt;
		TextView tvRetweetedBy;
		TextView tvRetweets;
		TextView tvFavorites;
	}

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.tweet_item, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);

		ViewHolder viewHolder;
		if (convertView == null) {  // No recycled view.
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.tweet_item, parent, false);
			initializeViews(viewHolder, convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
		viewHolder.tvBody.setText(tweet.getBody());
		viewHolder.tvName.setText(Html.fromHtml("<b>" + tweet.getUser().getName() + "</b>"));
		viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		viewHolder.tvCreatedAt.setText(tweet.getRelativeTimeString());
		if (tweet.getRetweetCount() > 0) {
			viewHolder.tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
		}
		if (tweet.getFavoritesCount() > 0) {
			viewHolder.tvFavorites.setText(String.valueOf(tweet.getFavoritesCount()));
		}
		if (tweet.isRetweet()) {
			viewHolder.tvRetweetedBy.setText(tweet.getRetweetedBy() + " retweeted");
		} else {
			viewHolder.tvRetweetedBy.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void initializeViews(ViewHolder viewHolder, View convertView) {
		viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
		viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
		viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
		viewHolder.tvRetweetedBy = (TextView) convertView.findViewById(R.id.tvRetweetedBy);
		viewHolder.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
		viewHolder.tvFavorites = (TextView) convertView.findViewById(R.id.tvFavorites);
		convertView.setTag(viewHolder);
	}
}