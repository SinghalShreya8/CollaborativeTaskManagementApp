package com.example.taskmanagement.ui.main;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.taskmanagement.R;

import java.io.Serializable;

public class Taskdetail extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail);

        Bundle b = this.getIntent().getExtras();
        WonderModel model = (WonderModel) b.getSerializable("taskObject");
        String timeleft = getIntent().getStringExtra("time");
        TextView t1 = findViewById(R.id.title);
        TextView t2 = findViewById(R.id.time);
        TextView t3 = findViewById(R.id.assign);
        TextView t4 =findViewById(R.id.description);
        t1.setText(model.getTitle());
        t2.setText(timeleft);
        t3.setText(model.getassignedBy());
        t4.setText(model.getDescription());

    }
}
