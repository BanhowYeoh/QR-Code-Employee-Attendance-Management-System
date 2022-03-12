package com.yeoh.qrattendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    Timer timer;
    int delay;

    private final static String useractivation = "login.txt";

    String FullName;
    String ActivationCode;
    String MessageLogin;
    String StatusLogin;

    Boolean ReadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        ReadUserDetails();

        /*delay = 2000;// in ms
        timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                Intent intent1 = new Intent(SplashScreen.this, Login.class);
                startActivity(intent1);
                finish();
            }
        }, delay);*/
    }

    public void ReadUserDetails()

    {
        try {

            InputStream in = openFileInput(useractivation);

            if (in != null) {
                InputStreamReader tmp=new InputStreamReader(in);
                BufferedReader reader=new BufferedReader(tmp);
                String str;
                Integer tmpIndex;
                tmpIndex =0;
                StringBuilder buf=new StringBuilder();

                while ((str = reader.readLine()) != null) {

                    buf.append(str + "n");

                    if(tmpIndex ==0)
                    {
                        FullName = str;
                    }
                    else if(tmpIndex ==1)
                    {
                        ActivationCode = str;
                    }
                    else if(tmpIndex ==2)
                    {
                        MessageLogin = str;
                    }
                    else if(tmpIndex ==3)
                    {
                        StatusLogin = str;
                    }

                    tmpIndex=tmpIndex +1;
                }

                in.close();
                ReadFile=true;

                delay = 2000;// in ms
                timer = new Timer();

                if (StatusLogin.equals("Login")) {
                    if (MessageLogin.equals("EMPLOYEE AUTHORIZED ACCESS!"))
                    {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                Intent intent1 = new Intent(SplashScreen.this, MainStaff.class);

                                intent1.putExtra("Name", FullName.toString());
                                intent1.putExtra("ID", ActivationCode.toString());
                                intent1.putExtra("Msg", MessageLogin.toString());

                                startActivity(intent1);
                                finish();
                            }
                        }, delay);
                    }
                    else if (MessageLogin.equals("ADMIN AUTHORIZED ACCESS!"))
                    {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                Intent intent1 = new Intent(SplashScreen.this, MainAdmin.class);

                                intent1.putExtra("Name", FullName.toString());
                                intent1.putExtra("ID", ActivationCode.toString());
                                intent1.putExtra("Msg", MessageLogin.toString());

                                startActivity(intent1);
                                finish();
                            }
                        }, delay);
                    }
                    else if (MessageLogin.equals("STAFF AUTHORIZED ACCESS!"))
                    {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                Intent intent1 = new Intent(SplashScreen.this, MainActivity.class);

                                intent1.putExtra("Name", FullName.toString());
                                intent1.putExtra("ID", ActivationCode.toString());
                                intent1.putExtra("Msg", MessageLogin.toString());

                                startActivity(intent1);
                                finish();
                            }
                        }, delay);
                    }

                }
                else {
                    timer.schedule(new TimerTask() {
                        public void run() {
                            Intent intent1 = new Intent(SplashScreen.this, Login.class);
                            startActivity(intent1);
                            finish();
                        }
                    }, delay);
                }

            }
        }

        catch (java.io.FileNotFoundException e) {
            delay = 2000;// in ms
            timer = new Timer();

            timer.schedule(new TimerTask() {
                public void run() {
                    Intent intent1 = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent1);
                    finish();
                }
            }, delay);
        }

        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
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
                ActivityCompat.finishAffinity(SplashScreen.this);
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