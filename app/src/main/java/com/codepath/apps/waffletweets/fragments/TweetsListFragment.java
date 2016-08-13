package com.codepath.apps.waffletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seetha on 8/9/16.
 */
public class TweetsListFragment extends Fragment {
    private TweetsArrayAdapter mTweetsAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;

    private MaterialRefreshLayout mMaterialRefreshLayout;
//    @BindView(R.id.lvTweets)
    RecyclerView mRecyclerView;
//    @BindView(R.id.fabCompose)
    FloatingActionButton mFloatingActionButton;

    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
//        ButterKnife.bind(getActivity());

        mRecyclerView = (RecyclerView) v.findViewById(R.id.lvTweets);
//        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.fabCompose);
        //connect adapter to recyclerview
        mRecyclerView.setAdapter(mTweetsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


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

    public void addAll(List<Tweet> tweets){
        mTweetsAdapter.addAll(tweets);
    }
}
