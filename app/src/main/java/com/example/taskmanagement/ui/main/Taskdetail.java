package com.example.taskmanagement.ui.main;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.R;
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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import static android.content.ContentValues.TAG;

public class Taskdetail extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DocumentReference db_ongoing;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Uri uri;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail);
        Button completeButton = findViewById(R.id.complete);
        final Button documentButton = findViewById(R.id.document);
        FirebaseAuth fAuth;
        final FirebaseFirestore fStore;
        final FirebaseUser Cuser;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Cuser = fAuth.getCurrentUser();

        Bundle b = this.getIntent().getExtras();
        final WonderModel model = (WonderModel) b.getSerializable("taskObject");
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
        final TextView t7 = findViewById((R.id.file));
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

        completeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //String id = getSnapshots().getSnapshot(position).getId();
                db_ongoing =fStore.collection("users").document(Cuser.getEmail()).collection("ongoingtask").document(requestId);
                DocumentReference db_completed = fStore.collection("users").document(Cuser.getEmail()).collection("completedtask").document();
                moveFirestoreDocument(db_ongoing,db_completed);
                Toast.makeText(v.getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Taskdetail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        final String path = model.getDocument_path();
        if(path!=null){
            t7.setText(path);
            documentButton.setVisibility(View.VISIBLE);
            documentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Taskdetail.this, "File Downloading", Toast.LENGTH_SHORT).show();
                    StorageReference docref = storageReference.child("task_document").child(path);
                    Log.e("Document path" , String.valueOf(docref));
                    docref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            downloadFile(Taskdetail.this,path,getFileExt(uri), DIRECTORY_DOWNLOADS,uri.toString());
                            Toast.makeText(Taskdetail.this, "File Downloaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                            // Handle any errors
                        }
                    });
                }
            });
        }
        else{
            t7.setText("No Attachment");
            documentButton.setVisibility(View.GONE);
        }
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void downloadFile(Taskdetail taskdetail,  String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) this.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(fileName);
        request.setDescription("Downloading file...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(taskdetail, destinationDirectory, fileName + fileExtension);
        downloadmanager.enqueue(request);
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
