package com.example.game.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.ItemSearchBinding;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.example.game.models.User;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CommunitySearchAdapter extends RecyclerView.Adapter<CommunitySearchAdapter.ViewHolder> {
    private static final String TAG = "CommunitySearchAdapter";

    private List<Community> communities;
    private Context context;

    public CommunitySearchAdapter(List<Community> communities, Context context) {
        this.communities = communities;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new CommunitySearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.bind(community);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvCreator;
        private MaterialButton btnAction;
        private ImageView ivProfile;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemSearchBinding binding = ItemSearchBinding.bind(itemView);
            tvStatus = binding.tvStatus;
            tvCreator = binding.creator;
            tvTitle = binding.tvTitle;
            btnAction = binding.btnAction;
            ivProfile = binding.ivProfile;

            btnAction.setOnClickListener(view -> {
                Subscription subscription = new Subscription();
                subscription.setUser(ParseUser.getCurrentUser());
                subscription.setCommunity(communities.get(getAdapterPosition()));
                subscription.setInteractionCount(1);
                subscription.saveInBackground(e -> {
                    if (e == null) {
                        btnAction.setVisibility(View.GONE);
                        tvStatus.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Was not able to follow", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error while following a community:" + e);
                    }
                });

            });
        }

        public void bind(Community community) {
            tvTitle.setText(community.getName());
            ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
            subscriptionParseQuery.getFirstInBackground((object, e) -> {
                if (object != null && e == null) {
                    btnAction.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Error while checking status" + e);
                }
            });

            if (community.getCreator() != null) {
                try {
                    tvCreator.setText(String.format("by %s", community.getCreator().fetchIfNeeded().getString(User.KEY_NAME)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                tvCreator.setText(String.format("by %s", context.getString(R.string.app_label)));
            }

            ParseFile image = community.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivProfile);
            } else {
                ivProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_image_24));
            }
        }
    }
}
