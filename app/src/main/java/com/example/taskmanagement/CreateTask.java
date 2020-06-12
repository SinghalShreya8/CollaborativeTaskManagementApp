package com.example.taskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CreateTask extends AppCompatActivity {

    EditText title,description,time,end;
    Button b1,b2;
    String tit;
    String desc;
    String et,ed;
    DatePickerDialog picker;
    TimePickerDialog timer;
    AutoCompleteTextView auto_mail;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    private int  mHour, mMinute;
    TextView textFile;
    private static final int PICKFILE_RESULT_CODE = 1;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser Cuser;
    StorageReference storageReference;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        title= findViewById(R.id.title);
        description=findViewById(R.id.description);
        time= findViewById(R.id.time);
        end=findViewById(R.id.end);
        b1= findViewById(R.id.b1);
        b2= findViewById(R.id.b2);
        textFile=findViewById(R.id.fileattached);
        auto_mail=(AutoCompleteTextView)findViewById(R.id.autoemail);
        addAdapterToViews();
        time.setInputType(InputType.TYPE_NULL);
        end.setInputType(InputType.TYPE_NULL);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timer = new TimePickerDialog(CreateTask.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timer.show();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                end.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        //attachment button listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_task();
            }
        });


    }

    private void addAdapterToViews() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        auto_mail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));

    }

    public void create_task(){
            tit = title.getText().toString();
            desc = description.getText().toString();
            ed =end.getText().toString();
            et =time.getText().toString();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Cuser = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        if(tit.isEmpty() || desc.isEmpty() || ed.isEmpty()|| auto_mail.getText().toString().isEmpty()|| et.isEmpty()){
            Toast.makeText(CreateTask.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        CollectionReference curUserRef = fStore.collection("users").document(Cuser.getEmail()).collection("ongoingtask");
        final CollectionReference tagUserRef = fStore.collection("users").document(auto_mail.getText().toString()).collection("taskrequests");
        final Map<String,Object> data = new HashMap<>();
        data.put("status","active");
        assert acct != null;
        data.put("assignedBy",acct.getDisplayName());
        data.put("assignedTo",auto_mail.getText().toString());
        data.put("title",tit);
        data.put("description",desc);
        data.put("Deadline_Date",ed);
        data.put("Deadline_Time",et);
        curUserRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                tagUserRef.add(data);
                Toast.makeText(CreateTask.this, "Task Created", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Intent intent = new Intent(CreateTask.this,MainActivity.class);
                startActivity(intent);
                // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateTask.this, "Failed to Create. Try again!", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error adding document", e);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    //String FilePath = data.getData().getPath();
                    Uri uri= data.getData();
                    String fileName =  getFileName(uri);


                    textFile.setText(fileName);
                }
                break;

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

}






