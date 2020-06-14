package com.example.taskmanagement.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.taskmanagement.CreateTask;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CreatedTask extends AppCompatActivity {

    RecyclerView MyRecyclerView;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String emailId;
    PopupWindow popupWindow;
    FirestoreRecyclerAdapter adapter;
    Button closePopupBtn;

TextView t;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ongoing_task);
        fAuth = FirebaseAuth.getInstance();
        emailId = fAuth.getCurrentUser().getEmail();

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(emailId).collection("createdTask");
        FirestoreRecyclerOptions<WonderModel> item = new FirestoreRecyclerOptions.Builder<WonderModel>()
                .setQuery(query, WonderModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<WonderModel,CreatedTaskViewHolder>(item) {
            @NonNull
            @Override
            public CreatedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.created_items, parent, false);
                return new CreatedTaskViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CreatedTask.CreatedTaskViewHolder holder, int position, @NonNull final WonderModel model) {
                final String arr[] = {model.getTitle(), model.getCreated_Date(),model.getAssignedTo()};
                final WonderModel copy= model;
                holder.titleCreated.setText(arr[0]);
                holder.createdDateTextView.setText(arr[1]);
                holder.assignedToTextView.setText(arr[2]);
               // final String requestId=  getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater layoutInflater = (LayoutInflater) CreatedTask.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View customView = layoutInflater.inflate(R.layout.created_popup,null);
                      closePopupBtn =  customView.findViewById(R.id.closePopupBtn);
                       t=customView.findViewById(R.id.createdDescription);
                       t.setText(model.getDescription());
                        popupWindow = new PopupWindow(customView, 600, 800);
                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                        //close the popup window on button click

                        closePopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });

                    }
                });
            }
        };
        MyRecyclerView = findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(CreatedTask.this);
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


    private class CreatedTaskViewHolder extends RecyclerView.ViewHolder {
        public TextView titleCreated;
        public TextView assignedToTextView;
        public TextView createdDateTextView;

        public CreatedTaskViewHolder(View v) {
            super(v);
            titleCreated = v.findViewById(R.id.titleCreated);
            assignedToTextView = v.findViewById(R.id.assignedToTextView);
            createdDateTextView = v.findViewById(R.id.createdDateTextView);

        }
    }




}


