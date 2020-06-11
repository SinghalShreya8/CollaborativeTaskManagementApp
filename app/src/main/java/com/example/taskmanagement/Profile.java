package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText pname,pemail,pnumber,ppost,pabout;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Intent data = getIntent();
        final String fullName = data.getStringExtra("name");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("number");
        String postnow = data.getStringExtra("post");
        String aboutme = data.getStringExtra("about");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        pname = findViewById(R.id.name);
        pemail = findViewById(R.id.email);
        pnumber = findViewById(R.id.number);
        ppost = findViewById(R.id.post);
        pabout = findViewById(R.id.about);
        profileImageView = findViewById(R.id.imageview);
        saveBtn = findViewById(R.id.b2);

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile images.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pname.getText().toString().isEmpty() || pemail.getText().toString().isEmpty() || pnumber.getText().toString().isEmpty()|| ppost.getText().toString().isEmpty()|| pabout.getText().toString().isEmpty()){
                    Toast.makeText(Profile.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = pemail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("email",email);
                        edited.put("name",pname.getText().toString());
                        edited.put("number",pnumber.getText().toString());
                        edited.put("post",ppost.getText().toString());
                        edited.put("aboutme",pabout.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Profile.this,MainActivity.class);
                                startActivity(intent);
                               // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(Profile.this, "Email is changed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        pemail.setText(email);
        pname.setText(fullName);
        pnumber.setText(phone);
        ppost.setText(postnow);
        pabout.setText(aboutme);

        Log.d(TAG, "onCreate: " + fullName + " " + email + " " + phone + " " + postnow + " " + aboutme);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);


            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile images.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
