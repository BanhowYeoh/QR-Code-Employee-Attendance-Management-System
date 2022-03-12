package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetPword extends AppCompatActivity {

    EditText txtemailaddress;

    Button btnRetrive;

    ProgressBar pBar;

    String stremailadd;

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Reset Password")));

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        btnRetrive = (Button) findViewById(R.id.btnRetrive);

        btnRetrive.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                txtemailaddress = (EditText) findViewById(R.id.txtEmailAddress);

                if (txtemailaddress.length() == 0) {
                    Toast.makeText(ResetPword.this, "Email Address is required!", Toast.LENGTH_SHORT).show();
                } else {
                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(ResetPword.this.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                    if (nInfo != null && nInfo.isConnected()) {
                        pBar.setVisibility(View.VISIBLE);
                        btnRetrive.setEnabled(false);
                        stremailadd = txtemailaddress.getText().toString();

                        ipAddress = getString(R.string.filename_resetpword).toString() + "?stremailadd=" + stremailadd ;
                        getJSON(ipAddress);

                    } else {
                        pBar.setVisibility(View.GONE);
                        btnRetrive.setEnabled(true);
                        Toast.makeText(ResetPword.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
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
                        if (count==0)
                        {
                            MsgReceived = json.toString();
                        }
                        count = count + 1;
                    }



                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    FlagTimeout = true;
                    Toast.makeText(ResetPword.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(ResetPword.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


    private void ChoppingData(String json) throws JSONException {

        if (MsgReceived.equals("INVALID EMAIL ADDRESS!")) {
            pBar.setVisibility(View.GONE);
            btnRetrive.setEnabled(true);
            Toast.makeText(ResetPword.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (MsgReceived.equals("SUCCESS RETRIVE LOGIN DETAILS!")) {
            pBar.setVisibility(View.GONE);
            btnRetrive.setEnabled(false);

            String msg;
            msg = "Your login details was sent to email " + stremailadd + ". Please check your email and relogin with the details.";
            //Toast.makeText(ResetPword.this, msg.toString(), Toast.LENGTH_LONG).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent1 = new Intent(ResetPword.this, Login.class);
                    startActivity(intent1);
                    finish();

                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}