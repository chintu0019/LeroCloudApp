package ie.lero.chintucloud.openstack.lerocloudapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticatePWD extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "ie.lero.chintucloud.openstack.lerocloudapp.MESSAGE";
    public static String message = "admin";
    //Button authButton = (Button) findViewById(R.id.button2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_pwd);
        //authButton.setEnabled(true);
    }
    public void authenticate_with_password(View view){
        EditText userName = (EditText) findViewById(R.id.editText2);
        EditText userPWD = (EditText) findViewById(R.id.editText3);

        if (!"admin".equals(userName.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(AuthenticatePWD.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("User \"" + userName.getText() + "\" not found");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            //authButton.setEnabled(false);
        } else if (!"openstack".equals(userPWD.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(AuthenticatePWD.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Incorrect Password");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            //authButton.setEnabled(false);
        } else {
            Intent intent = new Intent(this, Openstack.class);
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }
}
