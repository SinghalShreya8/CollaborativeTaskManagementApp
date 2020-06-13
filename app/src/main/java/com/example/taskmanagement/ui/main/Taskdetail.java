package com.example.taskmanagement.ui.main;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.taskmanagement.Popup;
import com.example.taskmanagement.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;

import static android.content.ContentValues.TAG;

public class Taskdetail extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail);
        Button completeButton = findViewById(R.id.complete);
        final FirebaseFirestore db_completeTasks ,db_ongoingTasks ;
        FirebaseAuth fAuth;
        final String emailId;
        fAuth = FirebaseAuth.getInstance();
        emailId = fAuth.getCurrentUser().getEmail();
        db_completeTasks = FirebaseFirestore.getInstance();
        db_ongoingTasks = FirebaseFirestore.getInstance();
     //   Query queryOngoing = db_ongoingTasks.collection("users").document(emailId).collection("ongoingtask");
     //    FirestoreRecyclerOptions<WonderModel> item = new FirestoreRecyclerOptions.Builder<WonderModel>()
     //            .setQuery(queryOngoing, WonderModel.class)
     //           .build();
        Bundle b = this.getIntent().getExtras();
        WonderModel model = (WonderModel) b.getSerializable("taskObject");
        String timeleft = getIntent().getStringExtra("time");
        TextView t1 = findViewById(R.id.title);
        TextView t2 = findViewById(R.id.time);
        TextView t3 = findViewById(R.id.assign);
        TextView t4 =findViewById(R.id.description);
        TextView t5 = findViewById(R.id.num_assigned_txt);
        TextView t6 = findViewById(R.id.num_txt);
        t1.setText(model.getTitle());
        t2.setText(timeleft);
        t3.setText(model.getassignedBy());
        t4.setText(model.getDescription());
        t5.setText(model.getassignedBy().substring(0,1).toUpperCase());
        String userName = fAuth.getCurrentUser().getDisplayName();
        t6.setText(userName.substring(0,1).toUpperCase());
//        t5.setText(Character.toUpperCase(model.getassignedBy().charAt(0)));

        completeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
  //              String id = getSnapshots().getSnapshot(position).getId();
  //              DocumentReference db_ongoing =db_ongoingTasks.collection("users").document(emailId).collection("ongoingtask").document("taskObject");
   //             DocumentReference db_completed = db_completeTasks.collection("users").document(emailId).collection("completedtask").document();
   //             moveFirestoreDocument(db_ongoing,db_completed);

            }
        });
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
