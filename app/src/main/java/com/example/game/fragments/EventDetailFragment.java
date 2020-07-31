package com.example.game.fragments;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.FragmentEventDetailBinding;
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

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailFragment";
    public static final String KEY_EVENT = "event";
    public static final String KEY_COMMUNITY = "community";

    @Nullable private Event event;
    private TextView tvGoing;
    private ImageButton btnGo;
    private ImageButton btnBookMark;

    public static EventDetailFragment newInstance(Event event, Community community) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_EVENT, Parcels.wrap(event));
        args.putParcelable(KEY_COMMUNITY, Parcels.wrap(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        @Nullable Community community = null;
        if (args != null) {
            community = Parcels.unwrap(args.getParcelable(KEY_COMMUNITY));
            event = Parcels.unwrap(args.getParcelable(KEY_EVENT));
        }
        FragmentEventDetailBinding binding = FragmentEventDetailBinding.bind(view);
        TextView tvTitle = binding.tvTitle;
        TextView tvDate = binding.tvDate;
        TextView tvOrganizer = binding.tvOrganizer;
        ImageView ivImage = binding.ivImage;
        TextView tvCommunity = binding.tvCommunity;
        TextView tvDetail = binding.tvDetail;
        TextView tvAddress = binding.tvAddress;
        btnGo = binding.btnGo;
        tvGoing = binding.tvGoing;
        btnBookMark = binding.btnBookMark;

        //show the status of the user on this event:
        ParseQuery<Attendance> attendance = ParseQuery.getQuery(Attendance.class);
        attendance.whereEqualTo(Attendance.KEY_EVENT, event);
        attendance.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        attendance.whereEqualTo(Attendance.KEY_ATTEND_STATUS, true);
        attendance.getFirstInBackground((object, e) -> {
            if (object != null && e == null) {
                btnGo.setVisibility(View.GONE);
                tvGoing.setVisibility(View.VISIBLE);
            } else {
                btnGo.setVisibility(View.VISIBLE);
            }
        });

        ParseQuery<Attendance> attendance1 = ParseQuery.getQuery(Attendance.class);
        attendance1.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        attendance1.whereEqualTo(Attendance.KEY_LIKE_STATUS, true);
        attendance1.getFirstInBackground(new GetCallback<Attendance>() {
            @Override
            public void done(Attendance object, ParseException e) {
                if(e == null){
                    btnBookMark.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_bookmark_24));
                }
            }
        });

        //fill the rest of the views:
        try {
            tvCommunity.setText(String.format("@%s", Objects.requireNonNull(community).fetchIfNeeded().get(Community.KEY_NAME)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            tvOrganizer.setText(Objects.requireNonNull(Objects.requireNonNull(event).getUser().fetchIfNeeded().get(User.KEY_NAME)).toString());
        } catch (ParseException e) {
            tvOrganizer.setText(getString(R.string.app_label));
        }
        tvTitle.setText(event.getTitle());

        if (event.getAddressString() == null) {
            tvAddress.setText(R.string.no_address);
        } else {
            tvAddress.setText(event.getAddressString());
        }
        ParseFile image = event.getImage();
        if (image != null) {
            Glide.with(Objects.requireNonNull(getContext())).load(event.getImage().getUrl()).into(ivImage);
        }
        tvDetail.setText(event.getDescription());

        btnGo.setOnClickListener(view1 -> saveAttendanceChange());

        tvGoing.setOnClickListener(view12 -> saveAttendanceChange());
        btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookMarkEvent(event);
            }
        });

        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                bookMarkEvent(event);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, HH:mm");
        tvDate.setText(formatter.format(event.getDate()));

        ivImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }

    private void saveAttendanceChange() {
        ParseQuery<Attendance> attendance1 = ParseQuery.getQuery(Attendance.class);
        attendance1.whereEqualTo(Attendance.KEY_EVENT, event);
        attendance1.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        attendance1.whereEqualTo(Attendance.KEY_ATTEND_STATUS, true);
        attendance1.getFirstInBackground((object, e) -> {
            if (object != null) {
                if (btnGo.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getContext(), R.string.going, Toast.LENGTH_SHORT).show();
                    btnGo.setVisibility(View.GONE);
                    tvGoing.setVisibility(View.VISIBLE);
                    object.setAttendStatus(true);
                } else {
                    Toast.makeText(getContext(), R.string.not_attend, Toast.LENGTH_SHORT).show();
                    btnGo.setVisibility(View.VISIBLE);
                    tvGoing.setVisibility(View.GONE);
                    object.setAttendStatus(false);
                }
                object.saveInBackground();
            } else {
                //Create Attendance record:
                Attendance attendanceQuery = new Attendance();
                attendanceQuery.setUser(ParseUser.getCurrentUser());
                attendanceQuery.setEvent(event);
                attendanceQuery.setAttendStatus(true);
                attendanceQuery.saveInBackground(e1 -> {
                    if (e == null) {
                        if (btnGo.getVisibility() == View.VISIBLE) {
                            Toast.makeText(getContext(), R.string.going, Toast.LENGTH_SHORT).show();
                            btnGo.setVisibility(View.GONE);
                            tvGoing.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), R.string.not_attend, Toast.LENGTH_SHORT).show();
                            btnGo.setVisibility(View.VISIBLE);
                            tvGoing.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
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
                    btnBookMark.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_bookmark_border_24));
                } else {
                    object.setLikeStatus(true);
                    btnBookMark.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_bookmark_24));
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
                        btnBookMark.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_bookmark_24));
                    }
                });
            }
        });

        addInteraction(event);
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
                    subscription.setInteractionCount( 1 );
                    subscription.saveEventually();
                }
            }
        });
    }
}
