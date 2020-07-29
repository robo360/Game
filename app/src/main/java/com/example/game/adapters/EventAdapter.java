package com.example.game.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.ItemEventBinding;
import com.example.game.fragments.CommunityFragment;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.text.MessageFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static final String TAG ="EventAdapter";

    private Context context;
    private List<Event> events;
    private Community community;
    private CommunityFragment fragment;

    public EventAdapter(Context context, List<Event> events, Community community, CommunityFragment fragment) {
        this.context = context;
        this.events = events;
        this.community = community;
        this.fragment = fragment;
    }

    public interface OnClickBtnDetail{
        void onClickedBtnDetail(Event event, View view);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton btnDetail;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvDate;
        TextView tvOrganizer;
        TextView tvCommunity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemEventBinding binding = ItemEventBinding.bind(itemView);
            btnDetail = binding.btnGo;
            tvTitle = binding.tvTitle;
            tvDate = binding.tvDate;
            tvOrganizer = binding.tvOrganizer;
            ivImage = binding.ivImage;
            tvCommunity = binding.tvCommunity;
            btnDetail.setOnClickListener(view -> fragment.onClickedBtnDetail(events.get(getAdapterPosition()), ivImage));
        }

        public void bind(Event event) {
            try {
                tvOrganizer.setText(event.getUser().fetchIfNeeded().get(User.KEY_NAME).toString());
            } catch (ParseException e) {
                Log.e(TAG, "Error getting the name of the user:" +e);
            }

            tvCommunity.setText(MessageFormat.format("@{0}", event.getCommunity().getName()));
            tvDate.setText(event.getDate().toString());
            tvTitle.setText(event.getTitle());
            ParseFile image = event.getImage();
            if(image != null){
                Glide.with(context).load(event.getImage().getUrl()).into(ivImage);
            }
        }
    }
}
