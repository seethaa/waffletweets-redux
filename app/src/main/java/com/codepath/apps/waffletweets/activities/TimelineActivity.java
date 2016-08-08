package com.codepath.apps.waffletweets.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.codepath.apps.waffletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener {
    private TwitterClient mTwitterClient;
    private TweetsArrayAdapter mTweetsAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;

    private MaterialRefreshLayout mMaterialRefreshLayout;
    @BindView(R.id.lvTweets)
    RecyclerView mRecyclerView;
    @BindView(R.id.fabCompose)
    FloatingActionButton mFloatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //set background color of actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.waffletweetslogofinal);

        ButterKnife.bind(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        //create arraylist (data source)
        mTweets = new ArrayList<>();
        //construct adapter from data source
        mTweetsAdapter = new TweetsArrayAdapter(this, mTweets);
        //connect adapter to recyclerview
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                int lastTweetIndex = mTweets.size() - 1;
                Long max_id = mTweets.get(lastTweetIndex).getUid();
                populateTimeline(max_id);
            }
        });

        mTwitterClient = TwitterApplication.getRestClient();
        getCurrentUserInfo(); //get user account info and save as user object
        populateTimeline(null); //populate initial timeline

        //set pull to refresh
        mMaterialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        mMaterialRefreshLayout.setIsOverLay(false);
        mMaterialRefreshLayout.setWaveShow(false);

        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                refreshItems();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //load more refreshing...
            }
        });


    }

    @OnClick(R.id.fabCompose)
    public void composeTweet(FloatingActionButton fab) {
        showComposeTweetDialog();
    }

    /**
     * Checks if an active network is available
     *
     * @return true if network is available, false otherwise
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checks if device is connected to the internet
     *
     * @return true if device is connected, false otherwise
     */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Alert user to connect to network
     */
    protected void callNetworkDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(TimelineActivity.this);
        alert.setTitle("No Connection Available");
        alert.setMessage("Please check your network connection, and try again!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }


    /**
     * Calls Filter Dialog Fragment
     */
    private void showComposeTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();

        //pass in current user
        Bundle args = new Bundle();
        args.putParcelable("currUser", Parcels.wrap(mCurrentUser));

        ComposeTweetDialogFragment composeTweetDialogFragment = newInstance();
        composeTweetDialogFragment.setArguments(args);
        composeTweetDialogFragment.show(fm, "compose_tweet");
    }

    /**
     * Used for creating ComposeTweetDialogFragment and binding arguments
     *
     * @return
     */
    static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment f = new ComposeTweetDialogFragment();
        return f;
    }

    private void getCurrentUserInfo() {
        mTwitterClient.getUserInfo(new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());


                mCurrentUser = User.fromJSON(jsonResponse);

                Log.d("DEBUG", mTweetsAdapter.toString());
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }

    //send an API request to get the timeline JSON
    //fill in the listview by creating the tweet objects from the JSON
    private void populateTimeline(Long max_id) {
        mTwitterClient.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                //deserialize json
                //create models and add them to adapter
                //load model data into listview
                mTweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
                Log.d("DEBUG", mTweetsAdapter.toString());
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }

    /**
     *  Send an API request to get the timeline JSON.
     *  Fill in the recyclerview by creating the tweet objects from the JSON
     */
    private void postTweet(final Tweet tweet) {
        mTwitterClient.postTweet(tweet, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());

                mTweets.add(0, tweet);
                mTweetsAdapter.notifyItemInserted(0);

            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        });


    }

    /**
     * Checks if network is available and if device is online. If connected, it refreshes timeline items
     */
    private void refreshItems() {

        if (isNetworkAvailable() && isOnline()) {
            mTweets.clear();
            mTweetsAdapter.notifyDataSetChanged();
            populateTimeline(null);
            // refresh complete
            mMaterialRefreshLayout.finishRefresh();

            // load more refresh complete
            mMaterialRefreshLayout.finishRefreshLoadMore();
        } else {
            callNetworkDialog();

        }
    }


    @Override
    public void onFinishComposeTweetDialog(Tweet tweet) {

        postTweet(tweet);

        refreshItems();
    }

//    private void addTweet(Tweet t) {
//
//        System.out.println("DEBUGGY printing" + t.getBody());
//    }
}
