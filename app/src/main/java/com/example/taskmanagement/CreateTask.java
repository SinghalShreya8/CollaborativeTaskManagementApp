package com.example.taskmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class CreateTask extends AppCompatActivity {

    EditText title,description,start,end;
    Button b1,b2;
    String tit;
    String desc;
    String sd,ed;
    DatePickerDialog picker;
    AutoCompleteTextView auto_mail;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    TextView textFile;
    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        title= findViewById(R.id.title);
        description=findViewById(R.id.description);
        start= findViewById(R.id.start);
        end=findViewById(R.id.end);
        b1= findViewById(R.id.b1);
        b2= findViewById(R.id.b2);
        auto_mail=(AutoCompleteTextView)findViewById(R.id.autoemail);
        addAdapterToViews();
        start.setInputType(InputType.TYPE_NULL);
        end.setInputType(InputType.TYPE_NULL);
        start.setOnClickListener(new View.OnClickListener() {
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
                                start.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
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
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.putExtra(Intent.EXTRA_STREAM, uri);
           //     chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
            sd = start.getText().toString();
            ed =end.getText().toString();

    }
//    public void openFolder() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("file/*");
//        startActivityForResult(intent,PICKFILE_RESULT_CODE);
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    textFile.setText(FilePath);
                }
                break;

        }
    }
}






