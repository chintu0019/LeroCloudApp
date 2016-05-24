package ie.lero.chintucloud.openstack.lerocloudapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Okay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okay);
        Intent intent = getIntent();
        String message = intent.getStringExtra(Authenticate.EXTRA_MESSAGE);
        TextView TV = (TextView) findViewById(R.id.textView6);
        TV.setText("Welcome " + message);
    }
}
