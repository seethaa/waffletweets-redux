package com.codepath.apps.waffletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Takes Tweet objects and turns them into views that will be displayed in the list
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {
    // Store a member variable for the contacts
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    /**
     * Returns current context
     * @return
     */
    public Context getContext(){
        return mContext;
    }


    /**
     * Refreshes tweets and notifies adapter
     * @param tweets
     */
    public void addAll(ArrayList<Tweet> tweets) {
        if (tweets!=null){
            mTweets.addAll(tweets);
            notifyDataSetChanged();
        }

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvCreatedAt;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

             ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
             tvName = (TextView) itemView.findViewById(R.id.tvFullName);
             tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
             tvBody = (TextView) itemView.findViewById(R.id.tvBody);
             tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);

        }
    }


    // "Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss Z yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder holder, int position) {

        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on your views and data model
        String createdAt = tweet.getCreatedAt();
        //Wed Mar 03 19:37:35 +0000 2010
        SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy");
        Date d = null;
        try {
            d = f.parse(createdAt);
            long postmilliseconds = d.getTime();
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(postmilliseconds, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.tvCreatedAt.setText(relativeTime);
        } catch (ParseException e) {
            Log.d("DEBUG", "got here");
            e.printStackTrace();
        }

        User u = tweet.getUser();

        if (u!=null) {
            String username = tweet.getUser().getScreenName();
            String name = u.getName();

            holder.tvUserName.setText("@" +username);
            holder.tvName.setText(name);


        }

        //populate data into subviews
        holder.tvBody.setText(tweet.getBody());
        holder.ivProfileImage.setImageResource(android.R.color.transparent);


        String thumbnail = tweet.getUser().getProfileImageURL();
        //check if thumbnail is empty since NYTimesAPI sometimes returns ""
        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(getContext()).load(thumbnail).centerCrop().placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new RoundedCornersTransformation(holder.itemView.getContext(), 5, 5))
            .into(holder.ivProfileImage);

        } else {
            Glide.with(getContext()).load(R.drawable.ic_launcher).centerCrop().placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new RoundedCornersTransformation(holder.itemView.getContext(), 5, 5))
                    .into(holder.ivProfileImage);

        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

}
