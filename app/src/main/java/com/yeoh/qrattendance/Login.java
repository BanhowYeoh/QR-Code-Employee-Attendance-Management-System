package com.yeoh.qrattendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Login extends AppCompatActivity {

    EditText txtUsername;
    EditText txtActivationCode;

    Button btnActivate;
    TextView btnForgetPword;

    ProgressBar pBar;

    String strusername;
    String stractivationcode;

    String FullName;

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    private final static String useractivation = "login.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtActivationCode = (EditText) findViewById(R.id.txtActivationCode);
        btnActivate = (Button) findViewById(R.id.btnActivate);
        btnForgetPword  = (TextView) findViewById(R.id.btnForgetPword);

        btnForgetPword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, ResetPword.class);
                startActivity(intent1);
            }

        });

        btnActivate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                txtUsername = (EditText) findViewById(R.id.txtUsername);
                txtActivationCode = (EditText) findViewById(R.id.txtActivationCode);

                if (txtUsername.length() == 0) {
                    Toast.makeText(Login.this, "Username is required!", Toast.LENGTH_SHORT).show();
                } else if (txtActivationCode.length() == 0) {
                    Toast.makeText(Login.this, "Password is required!", Toast.LENGTH_SHORT).show();
                } else {
                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected()) {
                        pBar.setVisibility(View.VISIBLE);
                        btnActivate.setEnabled(false);
                        strusername = txtUsername.getText().toString();
                        stractivationcode = txtActivationCode.getText().toString();

                        ipAddress = getString(R.string.filename_login).toString() + "?strusername=" + strusername + "&stractivationcode=" + stractivationcode ;
                        getJSON(ipAddress);

                    } else {
                        pBar.setVisibility(View.GONE);
                        btnActivate.setEnabled(true);
                        Toast.makeText(Login.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });

    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    ChoppingData(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            protected String doInBackground(Void... voids) {

                Integer count;

                count = 0;

                try {
                    FlagTimeout = false;
                    ErrMsg = "";
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(15000);

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        //Toast.makeText(Login.this, json.toString(), Toast.LENGTH_SHORT).show();
                        if (count==0)
                        {
                            FullName = json.toString();
                            //Toast.makeText(Login.this, FullName.toString(), Toast.LENGTH_SHORT).show();
                        }
                        else if (count==1)
                        {
                            MsgReceived = json.toString();
                            //Toast.makeText(Login.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
                        }
                        count = count + 1;
                    }



                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    FlagTimeout = true;
                    Toast.makeText(Login.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(Login.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


    private void ChoppingData(String json) throws JSONException {

            if (MsgReceived.equals("EMPLOYEE AUTHORIZED ACCESS!")) {
                pBar.setVisibility(View.GONE);
                btnActivate.setEnabled(true);
                //Toast.makeText(Login.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
                SaveActivation();
            }
            else if (MsgReceived.equals("STAFF AUTHORIZED ACCESS!")) {
                pBar.setVisibility(View.GONE);
                btnActivate.setEnabled(true);
                //Toast.makeText(Login.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
                SaveActivation();
            }
            else if (MsgReceived.equals("ADMIN AUTHORIZED ACCESS!")) {
                pBar.setVisibility(View.GONE);
                btnActivate.setEnabled(true);
                //Toast.makeText(Login.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
                SaveActivation();
            }
            else {
                pBar.setVisibility(View.GONE);
                btnActivate.setEnabled(true);
                //Toast.makeText(Login.this, json, Toast.LENGTH_SHORT).show();
            }

    }

    public void SaveActivation() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(useractivation, 0));
            out.write((FullName) + "\n");
            out.write((stractivationcode) + "\n");
            out.write((MsgReceived) + "\n");
            out.write(("Login") );
            out.close();

            if (MsgReceived.equals("EMPLOYEE AUTHORIZED ACCESS!")) {
                Intent intent1 = new Intent(Login.this, MainStaff.class);

                intent1.putExtra("Name", FullName.toString());
                intent1.putExtra("ID", stractivationcode.toString());
                intent1.putExtra("Msg", MsgReceived.toString());

                startActivity(intent1);
                finish();
            }
            else if (MsgReceived.equals("STAFF AUTHORIZED ACCESS!")) {
                Intent intent1 = new Intent(Login.this, MainActivity.class);

                intent1.putExtra("Name", FullName.toString());
                intent1.putExtra("ID", stractivationcode.toString());
                intent1.putExtra("Msg", MsgReceived.toString());

                startActivity(intent1);
                finish();
            }
            else if (MsgReceived.equals("ADMIN AUTHORIZED ACCESS!")) {
                Intent intent1 = new Intent(Login.this, MainAdmin.class);

                intent1.putExtra("Name", FullName.toString());
                intent1.putExtra("ID", stractivationcode.toString());
                intent1.putExtra("Msg", MsgReceived.toString());

                startActivity(intent1);
                finish();
            }

        } catch (Throwable t) {
            Toast.makeText(Login.this, "Error : " + t.toString(), Toast.LENGTH_SHORT).show();

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
                ActivityCompat.finishAffinity(Login.this);
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