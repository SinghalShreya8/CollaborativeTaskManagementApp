package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    EditText et_name,et_number,et_post,et_email,et_about;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE= 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    FirebaseUser user;
    FirebaseAuth fAuth;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        imageView = findViewById(R.id.imageview);
        et_name = findViewById(R.id.name);
        et_email = findViewById(R.id.email);
        et_number = findViewById(R.id.number);
        et_post = findViewById(R.id.post);
        et_about = findViewById(R.id.about);
        button = findViewById(R.id.b2);
        progressBar = findViewById(R.id.progressbar);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        documentReference = db.collection("users").document(user.getEmail());
        storageReference = firebaseStorage.getInstance().getReference("profile_images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadData();
            }
        });
    }

    public void ChooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                data != null || data.getData() != null){
            imageUri = data.getData();
            fileName =  getFileName(imageUri);
            Toast.makeText(Profile.this, "Profile Created", Toast.LENGTH_SHORT).show();
            Picasso.get().load(imageUri).into(imageView);   //Picasso is open sourec library to load images in imageView
           // Toast.makeText(Profile.this, "Uploading Profile Pic", Toast.LENGTH_SHORT).show();
        }
    }


    private String getFileName(Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }

        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadData(){

        final String name = et_name.getText().toString();
        final String number= et_number.getText().toString();
        final String post = et_post.getText().toString();
        final String about = et_about.getText().toString();
        //final String email;
        et_email.setText(user.getEmail());

        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(number) || !TextUtils.isEmpty(post) ||
                !TextUtils.isEmpty(about) || !TextUtils.isEmpty((CharSequence) et_email) || imageUri != null ){

            progressBar.setVisibility(View.VISIBLE);
            final  StorageReference reference = storageReference.child(fileName);
            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return  reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();     //gives downloadUri
                                Map<String ,String > profile = new HashMap<>();
                                profile.put("name",name);
                                profile.put("number",number);
                                profile.put("email",user.getEmail());
                                profile.put("post",post);
                                profile.put("about",about);
                                profile.put("url",downloadUri.toString());

                                documentReference.set(profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(Profile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Profile.this,ShowProfile.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Profile.this, "failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

        }else {
            Toast.makeText(this, "All Fields required", Toast.LENGTH_SHORT).show();
        }
    }

    //onStart to check if data exists or not; if data exits it will be retrieved in textview then we edit it
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
                           String email_result = task.getResult().getString("email");
                           String post_result = task.getResult().getString("post");
                           String about_result = task.getResult().getString("about");
                           String Url = task.getResult().getString("url");

                           Picasso.get().load(Url).into(imageView);

                           et_name.setText(name_result);
                           et_email.setText(email_result);
                           et_number.setText(number_result);
                           et_post.setText(post_result);
                           et_about.setText(about_result);
                       }else {
                           Toast.makeText(Profile.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                      }
                 }
              }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}