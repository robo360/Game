package com.example.game.adapters;

import android.content.Context;
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
import com.example.game.fragments.CommunityFragment;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    List<Event> events;
    Community community;
    CommunityFragment fragment;

    public EventAdapter(Context context, List<Event> events, Community community, CommunityFragment fragment) {
        this.context = context;
        this.events = events;
        this.community = community;
        this.fragment = fragment;
    }

    public interface OnClickBtnDetail{
        void onClickedBtnDetail(Event event, Community community);
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
        TextView tvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDetail = itemView.findViewById(R.id.btnGo);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvCommunity = itemView.findViewById(R.id.tvCommunity);

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.onClickedBtnDetail(events.get(getAdapterPosition()), community);
                }
            });
        }

        public void bind(Event event) {
            tvCommunity.setText("@"+community.getName());
            try {
                tvOrganizer.setText(event.getUser().fetchIfNeeded().get(User.KEY_NAME).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDate.setText(event.getDate().toString());
            tvTitle.setText(event.getTitle());
            ParseFile image = event.getImage();
            if(image != null){
                Glide.with(context).load(event.getImage().getUrl()).into(ivImage);
            }
        }
    }
}
