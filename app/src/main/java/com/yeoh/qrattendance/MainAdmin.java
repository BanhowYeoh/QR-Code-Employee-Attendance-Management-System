package com.yeoh.qrattendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.OutputStreamWriter;

public class MainAdmin extends AppCompatActivity {

    Button btnRegAccount;
    Button btnUserAccount;
    Button btnCheckIn;
    Button btnAbout;
    TextView btnLogout;
    Button btnAttRecords;

    String FullName;
    String ActivationCode;
    String UserMessage;

    private final static String useractivation = "login.txt";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        getSupportActionBar().hide();

        btnLogout = (TextView) findViewById(R.id.btnLogout);

        btnRegAccount = (Button) findViewById(R.id.btnRegAccount);
        btnUserAccount = (Button) findViewById(R.id.btnUserAccount);
        btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
        btnAbout = (Button) findViewById (R.id.btnAbout);
        btnAttRecords = (Button) findViewById(R.id.btnAttRecords);

        ActivationCode = getIntent().getStringExtra("ID");
        FullName = getIntent().getStringExtra("Name");
        UserMessage = getIntent().getStringExtra("Msg");

        btnRegAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdmin.this, EmpRecords.class);
                startActivity(intent);
            }
        });

        btnUserAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdmin.this, ViewEmpRecords.class);
                startActivity(intent);
            }
        });

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdmin.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdmin.this, About.class);
                startActivity(intent);
            }
        });

        btnAttRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdmin.this, StaffAttRecords.class);

                intent.putExtra("Name", FullName.toString());
                intent.putExtra("ID", ActivationCode.toString());

                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainAdmin.this);
                builder.setCancelable(false);
                builder.setMessage("Would you like to Logout this application?");
                builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StrictMode.enableDefaults();
                        DeleteAccount();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    public void DeleteAccount()
    {
        try {
            OutputStreamWriter out= new OutputStreamWriter(openFileOutput(useractivation, 0));

            out.write(("") + "\n");
            out.write(("") + "\n");
            out.write(("") + "\n");
            out.write("Logout\n");
            out.close();

            ActivityCompat.finishAffinity(MainAdmin.this);
            moveTaskToBack(true);

            System.exit(0);
        }

        catch (Throwable t) {

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Would you like to Exit this application?");
        builder.setPositiveButton("YES, EXIT!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                moveTaskToBack(true);
                //finish();
                ActivityCompat.finishAffinity(MainAdmin.this);
                System.exit(0);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}