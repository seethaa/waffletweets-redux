package com.codepath.apps.waffletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.codepath.apps.waffletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by seetha on 8/9/16.
 */
public abstract class TweetsListFragment extends Fragment {
    protected TweetsArrayAdapter mTweetsAdapter;
    protected ArrayList<Tweet> mTweets;
    protected LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;
    private TwitterClient mTwitterClient;


    protected MaterialRefreshLayout mMaterialRefreshLayout;
    //    @BindView(R.id.lvTweets)
    RecyclerView mRecyclerView;


//    @BindView(R.id.fabCompose)
//    FloatingActionButton mFloatingActionButton;

    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
//        ButterKnife.bind(getActivity());
        mTwitterClient = TwitterApplication.getRestClient();


        mRecyclerView = (RecyclerView) v.findViewById(R.id.lvTweets);
//        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.fabCompose);
        //connect adapter to recyclerview
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

//        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                System.out.println("DEBUGGY: GOT HEREEEE");
//                showComposeTweetDialog();
//            }
//        });

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                int lastTweetIndex = mTweets.size() - 1;
                Long max_id = mTweets.get(lastTweetIndex).getUid();
                populateTimeline(max_id);
            }
        });




        return v;
    }

    protected abstract void populateTimeline(Long max_id);

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

//    @Override
//    public void onFinishComposeTweetDialog(Tweet tweet) {
//        System.out.println("DEBUGGY: Got to finish");
//
//        postTweet(tweet);
//
//        refreshItems();
//
//    }


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

    public void refreshItems(){
        mTweets.clear();
        mTweetsAdapter.notifyDataSetChanged();

    }

    public void addAll(List<Tweet> tweets){
        mTweetsAdapter.addAll(tweets);
    }

    public void addTweetAtTop(Tweet tweet) {
        mTweets.add(0, tweet);
        mTweetsAdapter.notifyItemInserted(0);
        mLinearLayoutManager.scrollToPosition(0);
    }
}
