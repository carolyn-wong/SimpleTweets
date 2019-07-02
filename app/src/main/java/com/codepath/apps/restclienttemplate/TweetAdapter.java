package com.codepath.apps.restclienttemplate;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.TimeFormatter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends PagedListAdapter<Tweet, TweetAdapter.ViewHolder> {

    private PagedList<Tweet> mTweets;
    // context defined as global variable so Glide in onBindViewHolder has access
    Context context;

    // item callback
    // compute difference between new and old lists
    public static final DiffUtil.ItemCallback<Tweet> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Tweet>() {
                @Override
                public boolean areItemsTheSame(@NonNull Tweet oldTweet, @NonNull Tweet newTweet) {
                    return oldTweet.getUid() == newTweet.getUid();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Tweet oldTweet, @NonNull Tweet newTweet) {
                    return oldTweet.getBody().equals(newTweet.getBody());
                }
            };

    // adapter invokes DIFF_CALLBACK
    public TweetAdapter() {
        super(DIFF_CALLBACK);
    }

    // helper function to add tweets
    public void addMoreTweets(List<Tweet> newTweets) {
        mTweets.addAll(newTweets);
        submitList((PagedList<Tweet>) mTweets); // DiffUtil takes care of checking tweets
    }

    // for each row, inflate layout and cache references into ViewHolder

    // method invoked only when creating a new row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        // inflate layout, need to get context first
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind values based on element position
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // adapter should not retain copy of current list
        Tweet tweet = getItem(position);

        // convert timestamp to relative time
        String formattedCreatedAt = TimeFormatter.getTimeDifference(tweet.createdAt);

        // populate views according to data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvCreatedAt.setText(formattedCreatedAt);

        // TODO - get better resolution images by changing image link from "normal" to "bigger"
        // load image using Glide
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvCreatedAt;

        // constructor takes in inflated layout
        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
        }
    }

    // RecyclerView adapter helper methods to clear items from or add items to underlying dataset
    // clean recycler elements
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // add list of tweets - change list type depending on item type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
