package com.example.taskmanagement.ui.main;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.taskmanagement.R;

public class Taskdetail extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail);
        String arr[] = getIntent().getStringArrayExtra("taskobject");
        TextView t1=(TextView)findViewById(R.id.title);
        TextView t2=(TextView)findViewById(R.id.end);
        TextView t3=(TextView)findViewById(R.id.assign);
        t1.setText(arr[0]);
        t2.setText(arr[1]);
        t3.setText(arr[2]);

    }
}
