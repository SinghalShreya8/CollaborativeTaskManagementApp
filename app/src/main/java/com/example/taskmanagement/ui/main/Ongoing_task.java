package com.example.taskmanagement.ui.main;

import com.example.taskmanagement.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ongoing_task extends Fragment {

    RecyclerView MyRecyclerView;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String emailId;
    String d1, t1;;
    FirestoreRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private String getTimeDifference(Date deadlineDate, Date currentDate) {
        long diff = deadlineDate.getTime() - currentDate.getTime();
        long seconds = diff / 1000;
        int days = (int) (seconds / (24 * 60 * 60));
        seconds -= days * (24 * 60 * 60);
        long hours = (int) (seconds / (60 * 60));
        seconds -= hours * (60 * 60);
        int minutes = (int) (seconds / (60));
        seconds -= minutes * 60;

        String timeDifference = "";
        if (currentDate.before(deadlineDate)) {
            timeDifference = days + " Days " + hours + " hours " + minutes + " minutes left";
        }
        return timeDifference;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ongoing_task, container, false);
        fAuth = FirebaseAuth.getInstance();
        emailId = fAuth.getCurrentUser().getEmail();
        t1 = "";
        db = FirebaseFirestore.getInstance();
        final Query query = db.collection("users").document(emailId).collection("ongoingtask");

        FirestoreRecyclerOptions<WonderModel> item = new FirestoreRecyclerOptions.Builder<WonderModel>()
                .setQuery(query, WonderModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<WonderModel, CompletedTaskViewHolder>(item) {
            @NonNull
            @Override
            public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Log.i("query", String.valueOf(query));
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_items, parent, false);
                return new CompletedTaskViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position, @NonNull WonderModel model) {
                final String arr[] = {model.getTitle(), model.getDeadline_Date(), model.getassignedBy(), model.getDeadline_Time(), model.getDescription()};
                final WonderModel copy= model;
                d1 = arr[1] + " " + arr[3];
                try {

                    Date deadlineDate = dateFormat.parse(d1);
                    Date currentDate = (new Date());
                    t1 = getTimeDifference(deadlineDate, currentDate);

                } catch (ParseException e) {
                    t1="0 days";
                    e.printStackTrace();
                }
                holder.titleTextView.setText(arr[0]);
                holder.dateTextView.setText(t1);
                holder.assignedByTextView.setText(arr[2]);
                final String requestId=  getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), Taskdetail.class);
                        Bundle b = new Bundle();
                        b.putSerializable("taskObject", copy);
                        i.putExtras(b);
                 //       i.putExtra("AcceptReject","ongoing");
                        i.putExtra("requestId",requestId);
                        v.getContext().startActivity(i);
                    }
                });
            }
        };
        MyRecyclerView = view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        MyRecyclerView.setLayoutManager(MyLayoutManager);
        MyRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView assignedByTextView;
        public TextView dateTextView;

        public CompletedTaskViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            assignedByTextView = v.findViewById(R.id.assignedByTextView);
            dateTextView = v.findViewById(R.id.dateTextView);

        }
    }


}
