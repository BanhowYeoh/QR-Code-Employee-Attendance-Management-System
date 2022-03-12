package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewEmpRecords extends AppCompatActivity {

    List dbEmpList = new ArrayList();
    List dbEmpListChoped = new ArrayList();
    List empListRecords = new ArrayList();

    String[] empListDetails;

    String ipAddress;

    String FlagSearch;

    // Array of strings...
    ListView empListView;
    EditText txtSearchEmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_emp_records);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Employee Management")));

        empListView = (ListView)findViewById(R.id.listEMployee);
        txtSearchEmp = (EditText) findViewById(R.id.txtEmpName);

        txtSearchEmp.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                FlagSearch="true";
                ipAddress = getString(R.string.filename_searchemp).toString() + "?empname=" + txtSearchEmp.getText().toString()  ;
                getJSON(ipAddress);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FlagSearch="false";
        ipAddress = getString(R.string.filename_emplist).toString();
        getJSON(ipAddress);


        empListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = (String) parent.getItemAtPosition(position);

                //Toast.makeText(ViewEmpRecords.this, "1", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewEmpRecords.this, EditEmployee.class);

                intent.putExtra("strData", empListRecords.get(position).toString());
                //intent.putExtra("NAME", empListDetails[position].toString());

                startActivity(intent);

                //Toast.makeText(ViewEmpRecords.this, selectedItem.toString(), Toast.LENGTH_SHORT).show();
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

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(15000);

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    count = 0;
                    dbEmpList.clear();
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        dbEmpList.add(count, json);
                        empListRecords.add(count,json);

                        count = count + 1;
                    }

                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    Toast.makeText(ViewEmpRecords.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    Toast.makeText(ViewEmpRecords.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }

    private void ChoppingData(String json) throws JSONException {
        Integer ttlRecRceived;
        int a;
        int b;
        String strChopped;

        String currentString;
        ttlRecRceived = dbEmpList.size() ;
        dbEmpListChoped.clear();
        a=0;
        b=0;
        dbEmpList.clear();

        for(int i = 0; i < ttlRecRceived ; i++){

            currentString = empListRecords.get(i).toString();
            String[] empListDetails = currentString.split(",");

            dbEmpList.add(b,"\n" + "ID : " + empListDetails[0].toString() + "\n" + "NAME : " + empListDetails[1].toString() + "\n" + "PASSWORD : " + empListDetails[2].toString() + "\n" +  "CONTACT NO : " + empListDetails[3].toString() + "\n" + "EMAIL ADD : " + empListDetails[4].toString() + "\n" + "USER TYPE : " + empListDetails[5].toString() + "\n" + "REG DATE & TIME : " + empListDetails[6].toString() + "\n");
            b=b+1;

        }

        //empListView = (ListView)findViewById(R.id.listEMployee);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_emplistview, R.id.textView, dbEmpList);
        empListView.setAdapter(arrayAdapter);

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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }
}