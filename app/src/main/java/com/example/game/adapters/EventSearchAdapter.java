package com.example.game.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.ItemSearchBinding;
import com.example.game.fragments.EventSearchFragment;
import com.example.game.models.Event;
import com.example.game.models.User;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class EventSearchAdapter extends RecyclerView.Adapter<EventSearchAdapter.ViewHolder> {
    private static final String TAG = "EventSearchAdapter";

    private List<Event> events;
    private Context context;
    private EventSearchFragment fragment;

    public EventSearchAdapter(List<Event> events, Context context, EventSearchFragment fragment) {
        this.events = events;
        this.context = context;
        this.fragment = fragment;
    }

    public interface OnViewClickHandler{
        void btnOnClickedListener(Event event);
    }

    @NonNull
    @Override
    public EventSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new EventSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventSearchAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvCreator;
        private ImageView ivProfile;
        private MaterialButton btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemSearchBinding binding = ItemSearchBinding.bind(itemView);
            btnAction = binding.btnAction;
            tvCreator = binding.creator;
            tvTitle = binding.tvTitle;
            ivProfile = binding.ivProfile;

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.btnOnClickedListener(events.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Event event) {
            try {
                tvTitle.setText(event.fetchIfNeeded().getString(Event.KEY_TITLE));
            } catch (ParseException e) {
                Log.e(TAG, "Error loading the title" + e);
            }
            if (event.getUser() != null) {
                try {
                    tvCreator.setText(String.format("by %s", event.getUser().fetchIfNeeded().getString(User.KEY_NAME)));
                } catch (ParseException e) {
                    Log.e(TAG, "Error loading event creator" + e);
                }
            } else {
                tvCreator.setText(String.format("by %s", context.getString(R.string.app_label)));
            }

            ParseFile image = event.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivProfile);
            } else {
                ivProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_image_24));
            }
            btnAction.setText(R.string.view);
        }
    }
}
