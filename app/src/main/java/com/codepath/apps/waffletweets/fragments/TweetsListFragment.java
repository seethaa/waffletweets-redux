package com.codepath.apps.waffletweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.activities.ComposeTweetDialogFragment;
import com.codepath.apps.waffletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by seetha on 8/9/16.
 */
public class TweetsListFragment extends Fragment implements ComposeTweetDialogFragment.ComposeTweetDialogListener{
    public static TweetsArrayAdapter mTweetsAdapter;
    public static ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;

    private MaterialRefreshLayout mMaterialRefreshLayout;
//    @BindView(R.id.lvTweets)
    RecyclerView mRecyclerView;
//    @BindView(R.id.fabCompose)
    FloatingActionButton mFloatingActionButton;

    private TwitterClient mTwitterClient;


    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        ButterKnife.bind(getActivity());
        mTwitterClient = TwitterApplication.getRestClient();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.lvTweets);
        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.fabCompose);
        //connect adapter to recyclerview
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("DEBUGGY: GOT HEREEEE");
                showComposeTweetDialog();
            }
        });

//        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//
//                int lastTweetIndex = mTweets.size() - 1;
//                Long max_id = mTweets.get(lastTweetIndex).getUid();
//                populateTimeline(max_id);
//            }
//        });


        return v;
    }

    //creation lifeycle logic
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create arraylist (data source)
        mTweets = new ArrayList<>();
        //construct adapter from data source
        mTweetsAdapter = new TweetsArrayAdapter(getActivity(), mTweets);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());


    }

    @OnClick(R.id.fabCompose)
    public void composeTweet(FloatingActionButton fab) {
        System.out.println("DEBUGGY: GOT HEREEEE");
        showComposeTweetDialog();
    }

    /**
     * Calls Filter Dialog Fragment
     */
    private void showComposeTweetDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();

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






    public void refreshItems(String s) {
        mTweets.clear();
        mTweetsAdapter.notifyDataSetChanged();

    }


    public void addAll(List<Tweet> tweets){
        mTweetsAdapter.addAll(tweets);
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
     * Checks if an active network is available
     *
     * @return true if network is available, false otherwise
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Alert user to connect to network
     */
    protected void callNetworkDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("No Connection Available");
        alert.setMessage("Please check your network connection, and try again!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    public void onFinishComposeTweetDialog(Tweet tweet) {
        System.out.println("DEBUGGY: Got to finish");

        postTweet(tweet);

        refreshItems();

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
//            HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
//            HomeTimelineFragment.mTweets.clear();
//            HomeTimelineFragment.mTweetsAdapter.notifyDataSetChanged();

//            TweetsListFragment fragmentDemo = (TweetsListFragment)
//                    getSupportFragmentManager().findFragmentById(R.id.tweet_list_frag);
//            fragmentDemo.refreshItems("some string");

//            populateTimeline(null);

            // refresh complete
//            mMaterialRefreshLayout.finishRefresh();

            // load more refresh complete
//            mMaterialRefreshLayout.finishRefreshLoadMore();
        } else {
            callNetworkDialog();

        }
    }
}
