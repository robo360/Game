package com.example.game.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.ItemSearchBinding;
import com.example.game.models.Community;
import com.example.game.models.User;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseFile;

import java.util.List;

public class CommunitySearchAdapter extends RecyclerView.Adapter<CommunitySearchAdapter.ViewHolder> {
    private List<Community> communities;
    private Context context;

    public CommunitySearchAdapter(List<Community> communities, Context context){
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemSearchBinding binding = ItemSearchBinding.bind(itemView);
            tvCreator = binding.creator;
            tvTitle = binding.tvTitle;
            btnAction = binding.btnAction;
            ivProfile = binding.ivProfile;
        }

        public void bind(Community community) {
            tvTitle.setText(community.getName());
            if(community.getCreator() != null){
                tvCreator.setText(String.format("by %s", community.getCreator().getString(User.KEY_NAME)));
            } else {
                tvCreator.setText(String.format("by %s", context.getString(R.string.app_label)));
            }

            ParseFile image = community.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivProfile);
            } else {
                ivProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_image_24));
            }
        }
    }
}
