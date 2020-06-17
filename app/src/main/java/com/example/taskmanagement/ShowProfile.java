package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {
    TextView nameEt,numberEt,postEt,emailEt,aboutEt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DocumentReference documentReference;
    ImageView imageView;
    Button editProfile;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String imagename;
   // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        editProfile = findViewById(R.id.changeprofile);
        nameEt = findViewById(R.id.namesp);
        numberEt = findViewById(R.id.numbersp);
        postEt = findViewById(R.id.postsp);
        emailEt = findViewById(R.id.emailsp);
        aboutEt = findViewById(R.id.aboutsp);
        imageView = findViewById(R.id.imageViewsp);

        documentReference = db.collection("users").document(user.getEmail());
        storageReference = firebaseStorage.getInstance().getReference("profile_images");
        Log.e("email" , user.getEmail());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfile.this,editProfile.class);
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
                            String name_result = task.getResult().getString("name");
                            String number_result = task.getResult().getString("number");
                            String post_result = task.getResult().getString("post");
                            String email_result = task.getResult().getString("email");
                            String about_result = task.getResult().getString("about");
                            String Url = task.getResult().getString("url");

                            Picasso.get().load(Url).into(imageView);
                            nameEt.setText(name_result);
                            numberEt.setText(number_result);
                            postEt.setText(post_result);
                            emailEt.setText(email_result);
                            aboutEt.setText(about_result);
                            Log.e("email" , user.getEmail());
                        }else {
                            Toast.makeText(ShowProfile.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}