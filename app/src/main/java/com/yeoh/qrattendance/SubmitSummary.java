package com.yeoh.qrattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SubmitSummary extends AppCompatActivity {

    Timer timer;
    int delay;

    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
    String strSaveTodayRecords = df1.format(Calendar.getInstance().getTime()) + ".txt";

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    String strName;
    String strID;
    String strDate;
    String strTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_summary);

        getSupportActionBar().hide();

        TextView txtName = (TextView) findViewById(R.id.Title05);
        TextView txtClass = (TextView) findViewById(R.id.Title07);
        TextView txtDate = (TextView) findViewById(R.id.Title09);
        TextView txtTime = (TextView) findViewById(R.id.Title11);

        txtName.setText(getIntent().getStringExtra("Name"));
        txtClass.setText(getIntent().getStringExtra("ID"));
        txtDate.setText(getIntent().getStringExtra("EntryDate"));
        txtTime.setText(getIntent().getStringExtra("EntryTime"));

        strName = txtName.getText().toString();
        strID = txtClass.getText().toString();
        strDate = txtDate.getText().toString();
        strTime = txtTime.getText().toString();

        ipAddress = getString(R.string.filename_insertatt).toString() + "?empname=" + strName + "&empid=" + strID + "&empdate=" + strDate + "&enttime=" + strTime ;
        getJSON(ipAddress);

        //SaveTransaction();

        delay = 2000;// in ms
        timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                Intent intent1 = new Intent(SubmitSummary.this, ScanActivity.class);
                startActivity(intent1);
                finish();
            }
        }, delay);
    }

    public void SaveTransaction()
    {
        String RecDataString;
        RecDataString = getIntent().getStringExtra("StudentName") + "," + getIntent().getStringExtra("StudentClass")  + "," + getIntent().getStringExtra("EntryDate")  + "," + getIntent().getStringExtra("EntryTime") + "\n";

        try {

            OutputStreamWriter out= new OutputStreamWriter(openFileOutput(strSaveTodayRecords, 0));
            out.write(RecDataString);
            out.close();
            //Toast.makeText(getApplicationContext(), RecDataString.toString(), Toast.LENGTH_SHORT).show();
        }

        catch (Throwable t) {
            //Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
        }
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
                            MsgReceived = json.toString();
                            count = count + 1;
                    }



                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    FlagTimeout = true;
                    Toast.makeText(SubmitSummary.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(SubmitSummary.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


    private void ChoppingData(String json) throws JSONException {


        if (MsgReceived.equals("SUCCESSFULLY!")) {
            Toast.makeText(SubmitSummary.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SubmitSummary.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}