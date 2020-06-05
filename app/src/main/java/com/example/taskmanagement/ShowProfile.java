package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    TextView editname , editemail , editnumber , editpost , editabout;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        floatingActionButton = findViewById(R.id.floatingbtnsp);
        editname = findViewById(R.id.namesp);
        editemail = findViewById(R.id.emailsp);
        editnumber = findViewById(R.id.numbersp);
        editpost = findViewById(R.id.postsp);
        editabout = findViewById(R.id.aboutsp);
        imageView = findViewById(R.id.imageViewsp);
        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfile.this,Profile.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String nameexist = task.getResult().getString("name");
                            String emailexist = task.getResult().getString("email");
                            String numberexist = task.getResult().getString("number");
                            String postexist = task.getResult().getString("post");
                            String aboutexist = task.getResult().getString("aboutme");
                            String Url = task.getResult().getString("url");
                            Picasso.get().load(Url).into(imageView);
                            editname.setText(nameexist);
                            editemail.setText(emailexist);
                            editnumber.setText(numberexist);
                            editpost.setText(postexist);
                            editabout.setText(aboutexist);
                        }else {
                            Toast.makeText(ShowProfile.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

}