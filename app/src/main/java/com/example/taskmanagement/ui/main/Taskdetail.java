package com.example.taskmanagement.ui.main;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.google.firebase.auth.FirebaseAuth;

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
        TextView t5 = findViewById(R.id.num_assigned_txt);
        TextView t6 = findViewById(R.id.num_txt);
        t1.setText(model.getTitle());
        t2.setText(timeleft);
        t3.setText(model.getassignedBy());
        t4.setText(model.getDescription());
        t5.setText(model.getassignedBy().substring(0,1).toUpperCase());
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userName = fAuth.getCurrentUser().getDisplayName();
        t6.setText(userName.substring(0,1).toUpperCase());
//        t5.setText(Character.toUpperCase(model.getassignedBy().charAt(0)));
    }
}
