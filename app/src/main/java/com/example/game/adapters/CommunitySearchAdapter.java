package com.example.game.adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

public class CommunitySearchAdapter extends RecyclerView.Adapter<CommunitySearchAdapter.ViewHolder> {
    private static final String TAG = "CommunitySearchAdapter";

    private List<Community> communities;
    private Context context;
    private MaterialAlertDialogBuilder deleteFollowingAlert;

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
                        addInteraction(communities.get(getAdapterPosition()));
                        AddFollowing();
                    } else {
                        Toast.makeText(context, "Was not able to follow", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error while following a community:" + e);
                    }
                });

            });

            tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFollowingAlert = new MaterialAlertDialogBuilder(context)
                            .setMessage("Are you sure you want to leave this community?")
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ParseQuery<Subscription> subscriptionParseQuery1 = ParseQuery.getQuery(Subscription.class);
                                    subscriptionParseQuery1.include(Subscription.KEY_COMMUNITY);
                                    subscriptionParseQuery1.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
                                    subscriptionParseQuery1.whereEqualTo(Subscription.KEY_COMMUNITY, communities.get(getAdapterPosition()));
                                    Log.i(TAG, "Position:" + getAdapterPosition());
                                    subscriptionParseQuery1.getFirstInBackground(new GetCallback<Subscription>() {
                                        @Override
                                        public void done(Subscription object, ParseException e) {
                                            if (object != null && e == null) {
                                                object.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            removeFollowing();
                                                            btnAction.setVisibility(View.VISIBLE);
                                                            tvStatus.setVisibility(View.GONE);
                                                        } else {
                                                            Log.e(TAG, "Error while deleting community" + e);
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.e(TAG, "Error while unfollowing community" + e);
                                            }
                                        }
                                    });
                                }
                            });
                    deleteFollowingAlert.show();
                }
            });
        }

        private void addInteraction(Community community) {
            ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
            subscriptionParseQuery.getFirstInBackground((object, e) -> {
                if(e == null){
                    object.setInteractionCount(object.getInteractionCount().intValue() + 1);
                    object.saveInBackground();
                }
            });
        }

        private void removeFollowing() {
            ParseUser user = ParseUser.getCurrentUser();
            int count = Objects.requireNonNull(user.getNumber(User.KEY_FOLLOWING_COUNT)).intValue();
            user.put(User.KEY_FOLLOWING_COUNT, count - 1);
            user.saveEventually();

        }

        public void AddFollowing() {
            ParseUser user = ParseUser.getCurrentUser();
            int count = Objects.requireNonNull(user.getNumber(User.KEY_FOLLOWING_COUNT)).intValue();
            user.put(User.KEY_FOLLOWING_COUNT, count + 1);
            user.saveEventually();
        }

        public void bind(Community community) {
            tvTitle.setText(community.getName());
            ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
            subscriptionParseQuery.include(Subscription.KEY_COMMUNITY);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
            subscriptionParseQuery.getFirstInBackground((object, e) -> {
                if (object != null && e == null) {
                    btnAction.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Error while checking status" + e);
                    tvStatus.setVisibility(View.GONE);
                    btnAction.setVisibility(View.VISIBLE);
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
