package com.codepath.apps.basictwitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TweetCursorAdapter extends SimpleCursorAdapter {
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

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

	public TweetCursorAdapter(Context context, Cursor c, String[] from, int[] to, int flags) {
		super(context, R.layout.tweet_item, c, from, to, flags);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	@Override
	public void bindView(View convertView, Context context, Cursor c) {
		Tweet tweet = new Tweet(); // correct this
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
		viewHolder.tvBody.setText(tweet.getBody());
		viewHolder.tvName.setText(Html.fromHtml("<b>" + tweet.getUser().getName() + "</b>"));
		viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		viewHolder.tvCreatedAt.setText(tweet.getRelativeTimeString());
		if (tweet.getRetweetCount() > 0) {
			viewHolder.tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
		} else {
			viewHolder.tvRetweets.setText("");
		}
		if (tweet.getFavoritesCount() > 0) {
			viewHolder.tvFavorites.setText(String.valueOf(tweet.getFavoritesCount()));
		} else {
			viewHolder.tvFavorites.setText("");
		}
		if (tweet.isRetweet()) {
			viewHolder.tvRetweetedBy.setText(tweet.getRetweetedBy() + " retweeted");
			viewHolder.tvRetweetedBy.setVisibility(View.VISIBLE);
			viewHolder.tvRetweetedBy.setLayoutParams(getLayoutParams(View.VISIBLE));
		} else {
			viewHolder.tvRetweetedBy.setVisibility(View.INVISIBLE);
			viewHolder.tvRetweetedBy.setLayoutParams(getLayoutParams(View.INVISIBLE));
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = inflater.inflate(R.layout.tweet_item, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		initializeViews(viewHolder, convertView);
		return convertView;
	}

	private RelativeLayout.LayoutParams getLayoutParams(int visibility) {
		if (visibility == View.VISIBLE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			params.setMargins(0, 5, 0, 0);
			return params;
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, 0);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
			return params;
		}
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
