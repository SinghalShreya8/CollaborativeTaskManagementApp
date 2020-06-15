package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class ShowProfile extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView viewname,viewemail,viewnumber,viewpost,viewabout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button editProfile;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;
    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        viewnumber = findViewById(R.id.numbersp);
        viewname = findViewById(R.id.namesp);
        viewemail    = findViewById(R.id.emailsp);
        viewpost    = findViewById(R.id.postsp);
        viewabout  = findViewById(R.id.aboutsp);

        profileImage = findViewById(R.id.imageViewsp);
        editProfile = findViewById(R.id.changeprofile);


        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getEmail();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        StorageReference profileRef = storageReference.child("users/"+ userId +"/profile image.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    viewnumber.setText(user.getPhoneNumber());
                    viewname.setText(acct.getDisplayName());
                    viewemail.setText(userId);
                    viewpost.setText(documentSnapshot.getString("post"));
                    viewabout.setText(documentSnapshot.getString("aboutme"));
                  //  String Url = documentSnapshot.get().getString("url");
                   // Picasso.get().load(documentSnapshot.get().getString("imageurl")).into(profileImage);

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(),Profile.class);
                i.putExtra("imageurl",acct.getPhotoUrl());
                i.putExtra("name",acct.getDisplayName());
                i.putExtra("email",user.getEmail());
                i.putExtra("number",user.getPhoneNumber());
                i.putExtra("post",viewpost.getText().toString());
                i.putExtra("aboutme",viewabout.getText().toString());
                startActivity(i);
            }
        });
    }
}
