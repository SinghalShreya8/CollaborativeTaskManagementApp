package com.example.taskmanagement;
        import androidx.appcompat.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;

        import com.example.taskmanagement.R;

public class Profile extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        String arr[] = {"abc","abc@gmail.com","hey there!"};
        TextView t1=(TextView)findViewById(R.id.name);
        TextView t2=(TextView)findViewById(R.id.email);
        TextView t3=(TextView)findViewById(R.id.about_me);
        t1.setText(arr[0]);
        t2.setText(arr[1]);
        t3.setText(arr[2]);

    }
}

