package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.yeoh.qrattendance.R.id.txtBasic;
import static com.yeoh.qrattendance.R.id.txtEmail;

public class EmpRecords extends AppCompatActivity {

    EditText txtEmpID;
    EditText txtEmpName;
    EditText txtEmail;
    EditText txtContactNo;
    Spinner spUserType;
    Spinner spOTEntitle;

    Button btnRegister;
    ProgressBar pBar;

    String strEmpID;
    String strEmpName;
    String strEMail;
    String strContactNo;
    String strBasic;
    String strOTEntitle;
    String strTimeIn;
    String strTimeOut;
    String strUserType;

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    TimePickerDialog picker;
    EditText txtTimeIn;
    EditText txtTimeOut;
    EditText txtBasic;

    int strHH;
    int strMM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_records);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Register Employee")));

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        txtEmpID = (EditText) findViewById(R.id.txtEmpID);
        txtEmpName = (EditText) findViewById(R.id.txtEmpName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtContactNo = (EditText) findViewById(R.id.txtContactNo);
        spUserType = (Spinner) findViewById(R.id.spUserType);
        txtBasic = (EditText) findViewById(R.id.txtBasic);
        spOTEntitle = (Spinner) findViewById(R.id.spOTEntitle);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        txtTimeIn = (EditText) findViewById(R.id.txtStartTime);
        txtTimeOut =(EditText) findViewById(R.id.txtEndTime);

        txtTimeIn.setInputType(InputType.TYPE_NULL);
        txtTimeOut.setInputType(InputType.TYPE_NULL);

        txtTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);



                // time picker dialog
                picker = new TimePickerDialog(EmpRecords.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                DecimalFormat formatter = new DecimalFormat("00");
                                String strHH = formatter.format(sHour);
                                String strMM = formatter.format(sMinute);

                                txtTimeIn.setText(strHH + strMM);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        txtTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(EmpRecords.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                DecimalFormat formatter = new DecimalFormat("00");
                                String strHH = formatter.format(sHour);
                                String strMM = formatter.format(sMinute);

                                txtTimeOut.setText(strHH + strMM);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Admin");
        categories.add("Employee");
        categories.add("Staff");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spUserType.setAdapter(dataAdapter);

        List<String> categoriesot = new ArrayList<String>();
        categoriesot.add("Select");
        categoriesot.add("YES");
        categoriesot.add("NO");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterot = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesot);

        // Drop down layout style - list view with radio button
        dataAdapterot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spOTEntitle.setAdapter(dataAdapterot);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmpRecords.this);
                builder.setCancelable(false);
                builder.setMessage("Would you like to Register this Employee?");
                builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (txtEmpID.length() == 0) {
                            Toast.makeText(EmpRecords.this, "Employee ID is required!", Toast.LENGTH_SHORT).show();
                        } else if (txtEmpName.length() == 0) {
                            Toast.makeText(EmpRecords.this, "Employee Name is required!", Toast.LENGTH_SHORT).show();
                        } else if (txtEmail.length() == 0) {
                            Toast.makeText(EmpRecords.this, "Employee Email is required!", Toast.LENGTH_SHORT).show();
                        } else if (txtContactNo.length() == 0) {
                            Toast.makeText(EmpRecords.this, "Employee Contact Number is required!", Toast.LENGTH_SHORT).show();
                        } else if (spUserType.getSelectedItem().toString().equals("Select")) {
                            Toast.makeText(EmpRecords.this, "Employee User Type is required!", Toast.LENGTH_SHORT).show();
                        } else if (spOTEntitle.getSelectedItem().toString().equals("Select")) {
                            Toast.makeText(EmpRecords.this, "Employee OT Entitle Status is required!", Toast.LENGTH_SHORT).show();
                        } else {
                            ConnectivityManager cManager = (ConnectivityManager) getSystemService(EmpRecords.this.CONNECTIVITY_SERVICE);
                            NetworkInfo nInfo = cManager.getActiveNetworkInfo();

                            if (nInfo != null && nInfo.isConnected()) {
                                pBar.setVisibility(View.VISIBLE);
                                btnRegister.setEnabled(false);

                                strEmpID = txtEmpID.getText().toString().toUpperCase();
                                strEmpName = txtEmpName.getText().toString().toUpperCase();
                                strEMail = txtEmail.getText().toString().toLowerCase();
                                strContactNo = txtContactNo.getText().toString();
                                strUserType = spUserType.getSelectedItem().toString();
                                strBasic = txtBasic.getText().toString();
                                strTimeIn = txtTimeIn.getText().toString() + "00";
                                strTimeOut = txtTimeOut.getText().toString() + "00";
                                strOTEntitle = spOTEntitle.getSelectedItem().toString();

                                ipAddress = getString(R.string.filename_register).toString() + "?strEmpID=" + strEmpID + "&strEmpName=" + strEmpName + "&strEMail=" + strEMail + "&strContactNo=" + strContactNo + "&strUserType=" + strUserType + "&strBasic=" + strBasic +  "&strTimeIn=" + strTimeIn + "&strTimeOut=" + strTimeOut + "&strOTEntitle=" + strOTEntitle;
                                getJSON(ipAddress);

                            } else {
                                pBar.setVisibility(View.GONE);
                                btnRegister.setEnabled(true);
                                Toast.makeText(EmpRecords.this, "Connect to Wi-Fi Internet network or turn on Mobile Data.", Toast.LENGTH_SHORT).show();
                            }
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

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
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
                    Toast.makeText(EmpRecords.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(EmpRecords.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


    private void ChoppingData(String json) throws JSONException {

        if (MsgReceived.equals("ERROR REGISTER EMPLOYEE!")) {
            pBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(EmpRecords.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (MsgReceived.equals("SUCCESS REGISTER ACCOUNT!")) {
            pBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(EmpRecords.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (MsgReceived.equals("EMPLOYEE ID EXIST!")) {
            pBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(EmpRecords.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
        }
        else {
            pBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(EmpRecords.this, MsgReceived.toString(), Toast.LENGTH_SHORT).show();
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