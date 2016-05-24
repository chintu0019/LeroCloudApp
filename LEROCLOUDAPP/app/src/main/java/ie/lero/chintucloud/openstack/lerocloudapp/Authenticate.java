package ie.lero.chintucloud.openstack.lerocloudapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class Authenticate extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "ie.lero.chintucloud.openstack.lerocloudapp.MESSAGE";
    public static String message = "admin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

    }

    public void authenticate(View view) {
        EditText userName = (EditText) findViewById(R.id.editText);
        if (!"admin".equals(userName.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(Authenticate.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("User \""+ userName.getText() +"\" not found");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        } else {
            Intent intent = new Intent(this, Openstack.class);
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }
}