package com.example.taskmanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Completed_task extends AppCompatActivity {

    RecyclerView MyRecyclerView;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String emailId;
    String d1, t1;;
    FirestoreRecyclerAdapter adapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.fragment_ongoing_task);
        fAuth = FirebaseAuth.getInstance();
        emailId = fAuth.getCurrentUser().getEmail();
        t1 = "";
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(emailId).collection("completedtask");
        FirestoreRecyclerOptions<WonderModel> item = new FirestoreRecyclerOptions.Builder<WonderModel>()
                .setQuery(query, WonderModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<WonderModel,CompletedTaskViewHolder>(item) {
            @NonNull
            @Override
            public Completed_task.CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_items, parent, false);
                return new Completed_task.CompletedTaskViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Completed_task.CompletedTaskViewHolder holder, int position, @NonNull WonderModel model) {
                final String arr[] = {model.getTitle(), model.getDescription(),model.getassignedBy()};
                final WonderModel copy= model;
                holder.titleTextView.setText(arr[0]);
                holder.dateTextView.setText("Completed on : "+ new Date().toString());
                holder.assignedByTextView.setText(arr[2]);
                final String requestId=  getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), Taskdetail.class);
                        Bundle b = new Bundle();
                        b.putSerializable("taskObject", copy);
                        i.putExtras(b);
                        i.putExtra("time","Completed the task");
                        i.putExtra("AcceptReject","completed");
                        i.putExtra("requestId",requestId);
                        v.getContext().startActivity(i);
                    }
                });
            }
        };
        MyRecyclerView = findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(Completed_task.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        MyRecyclerView.setLayoutManager(MyLayoutManager);
        MyRecyclerView.setAdapter(adapter);

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


