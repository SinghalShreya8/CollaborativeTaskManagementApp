package com.example.taskmanagement.ui.main;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.Popup;
import com.example.taskmanagement.R;
import com.example.taskmanagement.ShowProfile;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Taskdetail extends AppCompatActivity {

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail);
        Button completeButton = findViewById(R.id.complete);
        FirebaseAuth fAuth;
        final FirebaseFirestore fStore;
        final FirebaseUser Cuser;
        fAuth = FirebaseAuth.getInstance();
        Bundle b = this.getIntent().getExtras();
        WonderModel model = (WonderModel) b.getSerializable("taskObject");
        final String requestId = getIntent().getStringExtra("requestId");
        String  completedtime=getIntent().getStringExtra("time");

        String ar=getIntent().getStringExtra("AcceptReject");
    if(ar!=null && (ar.equalsIgnoreCase("ar") || ar.equalsIgnoreCase("completed")))
        completeButton.setVisibility(View.GONE);
    else
        completeButton.setVisibility(View.VISIBLE);
        TextView t1 = findViewById(R.id.title);
        TextView t2 = findViewById(R.id.time);

        TextView t3 = findViewById(R.id.assign);
        TextView t4 =findViewById(R.id.description);
        TextView t5 = findViewById(R.id.num_assigned_txt);
        TextView t6 = findViewById(R.id.num_txt);
        String timeleft=model.getDeadline_Date()+" "+model.getDeadline_Time();
        try {

            Date deadlineDate = dateFormat.parse(timeleft);
            Date currentDate = (new Date());
            timeleft = getTimeDifference(deadlineDate, currentDate);

        } catch (ParseException e) {
            timeleft="0 days";
            e.printStackTrace();
        }
        t1.setText(model.getTitle());
        t2.setText(timeleft);
        if(completedtime!=null && completedtime.equals("Completed the task")){
            t2.setText("Completed the task");
        }
        t3.setText(model.getassignedBy());
        t4.setText(model.getDescription());
        t5.setText(model.getassignedBy().substring(0,1).toUpperCase());
        String userName = fAuth.getCurrentUser().getDisplayName();
        t6.setText(userName.substring(0,1).toUpperCase());
        fStore = FirebaseFirestore.getInstance();
        Cuser = fAuth.getCurrentUser();
        completeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //String id = getSnapshots().getSnapshot(position).getId();
                DocumentReference db_ongoing =fStore.collection("users").document(Cuser.getEmail()).collection("ongoingtask").document(requestId);
                DocumentReference db_completed = fStore.collection("users").document(Cuser.getEmail()).collection("completedtask").document();
                moveFirestoreDocument(db_ongoing,db_completed);
                Toast.makeText(v.getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Taskdetail.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

    public void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
