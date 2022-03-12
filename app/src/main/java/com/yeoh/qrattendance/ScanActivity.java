package com.yeoh.qrattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//public class ScanActivity extends AppCompatActivity {
public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    //camera permission is needed.
    String StatusReply;
    List dbRecordsJSON = new ArrayList();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getSupportActionBar().hide();

        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        //onBackPressed();
        String data = result.getContents().toString();

        String[] items = data.split(",");
        //String ServerURL = "http://www.qryeoh.com/attedance/insertdata.php?stdname=" + items[0].toString() + "&stdclass=" + items[1].toString();
        //getJSON(ServerURL);
        Intent intent = new Intent(ScanActivity.this, SubmitSummary.class);
        intent.putExtra("Name", items[0].toString());
        intent.putExtra("ID", items[1].toString());

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd"); // Format date
        String tmpDate =  df1.format(Calendar.getInstance().getTime());
        intent.putExtra("EntryDate", df1.format(Calendar.getInstance().getTime()));

        DateFormat df = new SimpleDateFormat("HH:mm:ss"); // Format time
        String tmpTime =  df1.format(Calendar.getInstance().getTime());
        intent.putExtra("EntryTime", df.format(Calendar.getInstance().getTime()));

        String ServerURL = "http://www.qryeoh.com/att/insertentry.php?empname=" + items[0].toString() + "&empid=" + items[1].toString() + "&empdate=" + tmpDate + "&enttime=" + tmpTime;
        getJSON(ServerURL);
        startActivity(intent);
        //http://www.qryeoh.com/att/insertentry.php?empname=Raja&empid=123&empdate=20-05-2021&enttime=15:27
        //onBackPressed();
        //sleep(5000);
        //mScannerView.resumeCameraPreview(this);

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
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(10000);

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    Integer i = 0;
                    dbRecordsJSON.clear();

                    while ((json = bufferedReader.readLine()) != null) {
                        StatusReply = json.toString().trim();
                        dbRecordsJSON.add(i, StatusReply.toString());
                    }
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void ChoppingData(String json) throws JSONException {

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScanActivity.this, android.R.layout.simple_spinner_item, dbRecordsJSON);
        //Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_SHORT).show();
    }
}
/*
    public void InsertData(final String name){
        //public void InsertData(final String name, final String email){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String ServerURL = "http://www.ragfilsc.com/sttheresa/insertdata.php?stdname=JAAHNAVI A/P RAJA%20KUMARAN&stdclass=1 BARATHI";
                //Db tilammur_mypresent_sttheresa; user :admimypresent ; Password : dQolp(EL@I3)
                String NameHolder = name ;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("name", NameHolder));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                //Toast.makeText(ScanActivity.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(name);
    }
}*/