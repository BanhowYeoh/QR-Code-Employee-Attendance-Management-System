package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditEmployee extends AppCompatActivity {

    EditText txtEmpID;
    EditText txtEmpName;
    EditText txtEmail;
    EditText txtContactNo;
    Spinner spUserType;

    Button btnUpdate;
    Button btnDelete;
    ProgressBar pBar;

    String strEmpID;
    String strEmpName;
    String strEMail;
    String strContactNo;
    String strUserType;

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    String RecMsg;

    String DeleteFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Update Employee")));

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        txtEmpID = (EditText) findViewById(R.id.txtEmpID);
        txtEmpName = (EditText) findViewById(R.id.txtEmpName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtContactNo = (EditText) findViewById(R.id.txtContactNo);
        spUserType = (Spinner) findViewById(R.id.spUserType);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Admin");
        categories.add("Employee");
        categories.add("Staff");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserType.setAdapter(dataAdapter);

        RecMsg = getIntent().getStringExtra("strData");

        String[] empListDetails = RecMsg.split(",");

        txtEmpID.setText(empListDetails[0].toString());
        txtEmpName.setText(empListDetails[1].toString());
        txtContactNo.setText(empListDetails[3].toString());
        txtEmail.setText(empListDetails[4].toString());

        if(empListDetails[5].toString().equals("Admin"))
        {
            spUserType.setSelection(1);
        }
        else if(empListDetails[5].toString().equals("Employee"))
        {
            spUserType.setSelection(2);
        }
        else if(empListDetails[5].toString().equals("Staff"))
        {
            spUserType.setSelection(3);
        }
        else
        {
            spUserType.setSelection(0);
        }


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEmployee.this);
                builder.setCancelable(false);
                builder.setMessage("Would you like to DELETE this Employee?");
                builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectivityManager cManager = (ConnectivityManager) getSystemService(EditEmployee.this.CONNECTIVITY_SERVICE);
                        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                        if (nInfo != null && nInfo.isConnected()) {
                            pBar.setVisibility(View.VISIBLE);
                            btnDelete.setEnabled(false);

                            strEmpID = txtEmpID.getText().toString().toUpperCase();

                            DeleteFunction= "true";
                            ipAddress = getString(R.string.filename_delemp).toString() + "?strEmpID=" + strEmpID;
                            getJSON(ipAddress);

                        } else {
                            pBar.setVisibility(View.GONE);
                            btnDelete.setEnabled(true);
                            Toast.makeText(EditEmployee.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
                        }
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEmployee.this);
                builder.setCancelable(false);
                builder.setMessage("Would you like to UPDATE this Employee?");
                builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectivityManager cManager = (ConnectivityManager) getSystemService(EditEmployee.this.CONNECTIVITY_SERVICE);
                        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                        if (nInfo != null && nInfo.isConnected()) {
                            pBar.setVisibility(View.VISIBLE);
                            btnUpdate.setEnabled(false);

                            strEmpID = txtEmpID.getText().toString().toUpperCase();
                            strEmpName = txtEmpName.getText().toString().toUpperCase();
                            strEMail = txtEmail.getText().toString().toLowerCase();
                            strContactNo = txtContactNo.getText().toString();
                            strUserType = spUserType.getSelectedItem().toString();

                            DeleteFunction= "false";
                            ipAddress = getString(R.string.filename_updateemp).toString() + "?strEmpID=" + strEmpID + "&strEmpName=" + strEmpName + "&strEMail=" + strEMail + "&strContactNo=" + strContactNo + "&strUserType=" + strUserType;
                            getJSON(ipAddress);

                        } else {
                            pBar.setVisibility(View.GONE);
                            btnUpdate.setEnabled(true);
                            Toast.makeText(EditEmployee.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
                        }
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
                    if (DeleteFunction.equals("true"))
                    {
                        ChoppingData(s);
                    }
                    else if (DeleteFunction.equals("false"))
                    {
                        ChoppingUpdateData(s);
                    }

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
                    Toast.makeText(EditEmployee.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(EditEmployee.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }



    private void ChoppingData(String json) throws JSONException {

        if (MsgReceived.equals("SUCCESSFULLY DELETED EMPLOYEE!")) {
            pBar.setVisibility(View.GONE);
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
            Toast.makeText(EditEmployee.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (MsgReceived.equals("ERROR DELETE EMPLOYEE!")) {
            pBar.setVisibility(View.GONE);
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
            Toast.makeText(EditEmployee.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ChoppingUpdateData(String json) throws JSONException {

        Toast.makeText(EditEmployee.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        if (MsgReceived.equals("SUCCESSFULLY UPDATE EMPLOYEE!")) {
            pBar.setVisibility(View.GONE);
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
            Toast.makeText(EditEmployee.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (MsgReceived.equals("ERROR UPDATE EMPLOYEE!")) {
            pBar.setVisibility(View.GONE);
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
            Toast.makeText(EditEmployee.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
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