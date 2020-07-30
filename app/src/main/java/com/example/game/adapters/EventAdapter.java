package com.example.game.adapters;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.game.models.Attendance;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.example.game.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.MessageFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnDetail;
        ImageButton btnBookMark;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvDate;
        TextView tvOrganizer;
        TextView tvCommunity;

        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            ItemEventBinding binding = ItemEventBinding.bind(itemView);
            btnDetail = binding.btnGo;
            btnBookMark = binding.btnBookMark;
            tvTitle = binding.tvTitle;
            tvDate = binding.tvDate;
            tvOrganizer = binding.tvOrganizer;
            ivImage = binding.ivImage;
            tvCommunity = binding.tvCommunity;

            btnDetail.setOnClickListener(view -> fragment.onClickedBtnDetail(events.get(getAdapterPosition()), ivImage));

            btnBookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookMarkEvent(events.get(getAdapterPosition()));
                }
            });

            GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    bookMarkEvent(events.get(getAdapterPosition()));
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                    return false;
                }
            });

            ivImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    gestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }


        public void bind(Event event) {
            try {
                tvOrganizer.setText(event.getUser().fetchIfNeeded().get(User.KEY_NAME).toString());
            } catch (ParseException e) {
                Log.e(TAG, "Error getting the name of the user:" +e);
            }

            ParseQuery<Attendance> attendance = ParseQuery.getQuery(Attendance.class);
            attendance.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
            attendance.whereEqualTo(Attendance.KEY_LIKE_STATUS, true);
            attendance.getFirstInBackground(new GetCallback<Attendance>() {
                @Override
                public void done(Attendance object, ParseException e) {
                    if(e == null){
                        btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
                    }
                }
            });

            tvCommunity.setText(MessageFormat.format("@{0}", event.getCommunity().getName()));
            tvDate.setText(event.getDate().toString());
            tvTitle.setText(event.getTitle());
            ParseFile image = event.getImage();
            if(image != null){
                Glide.with(context).load(event.getImage().getUrl()).into(ivImage);
            }
        }
        private void bookMarkEvent(Event event) {
            ParseQuery<Attendance> attendance = ParseQuery.getQuery(Attendance.class);
            attendance.whereEqualTo(Attendance.KEY_EVENT, event);
            attendance.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
            attendance.getFirstInBackground((object, e) -> {
                if (object != null) {
                    boolean status = object.getLikeStatus();
                    if (status == true){
                        object.setLikeStatus(false);
                        btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_border_24));
                    } else {
                        object.setLikeStatus(true);
                        btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
                    }
                    object.saveInBackground();
                } else {
                    //Create Attendance record:
                    Attendance attendanceQuery = new Attendance();
                    attendanceQuery.setUser(ParseUser.getCurrentUser());
                    attendanceQuery.setEvent(event);
                    attendanceQuery.setLikeStatus(true);
                    attendanceQuery.saveInBackground(e1 -> {
                        if (e == null) {
                            btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
                        }
                    });
                }
            });

            addInteraction(event);
        }
    }

    public  void addInteraction(Event event){
        //add the interaction to the db
        ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, event.getCommunity());
        subscriptionParseQuery.getFirstInBackground(new GetCallback<Subscription>() {
            @Override
            public void done(Subscription object, ParseException e) {
                if(object != null && e == null){
                    int count = object.getInteractionCount().intValue();
                    object.setInteractionCount(count + 1);
                    object.saveEventually();
                } else {
                    //create interaction
                    Subscription subscription = new Subscription();
                    subscription.setCommunity(event.getCommunity());
                    subscription.setUser(ParseUser.getCurrentUser());
                    subscription.setInteractionCount(1);
                    subscription.saveEventually();
                }
            }
        });
    }

}
