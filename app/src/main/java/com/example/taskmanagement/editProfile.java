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
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class editProfile extends AppCompatActivity {
    EditText Ename,Enumber,Epost,Eabout,Eemail;
    Button button;
    ProgressBar EprogressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE= 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageView = findViewById(R.id.editimage);
        Ename = findViewById(R.id.editname);
        Eemail = findViewById(R.id.editemail);
        Enumber= findViewById(R.id.editnumber);
        Epost = findViewById(R.id.editpost);
        Eabout = findViewById(R.id.editabout);
        button = findViewById(R.id.editb2);

        EprogressBar = findViewById(R.id.editprogressbar);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        documentReference = db.collection("users").document(user.getEmail());
        storageReference = firebaseStorage.getInstance().getReference("profile_images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(editProfile.this, "Updating Changes..", Toast.LENGTH_SHORT).show();
                Updateprofile();
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

                    Picasso.get().load(imageUri).into(imageView);
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



    private void Updateprofile(){
        final String name = Ename.getText().toString();
        final String email= Eemail.getText().toString();
        final String number = Enumber.getText().toString();
        final String post = Epost.getText().toString();
        final String about = Eabout.getText().toString();

       // System.currentTimeMillis() + "." + getFileExt(imageUri)
        if (imageUri != null){

            EprogressBar.setVisibility(View.INVISIBLE);
            final  StorageReference reference = storageReference.child(fileName);
            uploadTask = reference.putFile(imageUri);
           // progressBar.setVisibility(View.INVISIBLE);
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
                                final Uri downloadUri = task.getResult();
                                final DocumentReference sfDocRef = db.collection("users").document(user.getEmail());

                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                        //  transaction.update(sfDocRef, "population", newPopulation);
                                        transaction.update(sfDocRef,"name",name);
                                        transaction.update(sfDocRef,"email",email);
                                        transaction.update(sfDocRef,"number",number);
                                        transaction.update(sfDocRef,"post",post);
                                        transaction.update(sfDocRef,"about",about);
                                        transaction.update(sfDocRef,"url",downloadUri.toString());
                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        EprogressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(editProfile.this, "Updating Changes..", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(editProfile.this,ShowProfile.class);
                                        startActivity(intent);
                                        Toast.makeText(editProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(editProfile.this, "Update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }else {
            //if imageUri is null that is user doesn't pick new image -- we simply run transaction and do not upload the image

            EprogressBar.setVisibility(View.VISIBLE);
            final DocumentReference sfDocRef = db.collection("users").document(user.getEmail());
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    //  transaction.update(sfDocRef, "population", newPopulation);

                    transaction.update(sfDocRef,"name",name);
                    transaction.update(sfDocRef,"email",email);
                    transaction.update(sfDocRef,"number",number);
                    transaction.update(sfDocRef,"post",post);
                    transaction.update(sfDocRef,"about",about);

                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    EprogressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(editProfile.this,ShowProfile.class);
                    startActivity(intent);
                    Toast.makeText(editProfile.this, "Profile Updated..", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

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
                            String num_result = task.getResult().getString("number");
                            String post_result = task.getResult().getString("post");
                            String email_result = task.getResult().getString("email");
                            String about_result = task.getResult().getString("about");
                            //    String Url = task.getResult().getString("url");
                            //  Picasso.get().load(Url).into(imageView);

                            Ename.setText(name_result);
                            Eemail.setText(email_result);
                            Enumber.setText(num_result);
                            Epost.setText(post_result);
                            Eabout.setText(about_result);
                        }else {
                            Toast.makeText(editProfile.this, "No Profile exist", Toast.LENGTH_SHORT).show();
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