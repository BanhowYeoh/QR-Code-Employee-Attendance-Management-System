package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class StaffAttRecords extends AppCompatActivity {

    private static final String FILE_TEXT_EXT = ".txt";

    ListView simpleList;
    String strData;
    String OpenFileName;

    String ViewDate;

    WebView webView;
    String strMonth;

    private ProgressBar mProgressBar;
    ProgressBar loadingProgressBar,loadingTitle;

    String URLPage;
    Boolean FlagLoad;

    private int year;
    private int month;
    private int month1;
    private String month2;
    private int day1;
    private String day2;
    private String day3;
    private String day4;
    private int day;
    String newday;
    String newmonth;

    String strid;
    String strttldays;

    static final int DATE_DIALOG_ID = 998;

    TextView SearchDate;

    Button btnBackDate;
    Button btnForwarddate;

    String strSearchDDMM;
    String strSearchTtlDays;

    PrintJob printJob;
    String strempid;
    String strempname;

    Spinner spNameList;

    String ipAddress;
    Boolean FlagTimeout;
    String ErrMsg;
    String MsgReceived;

    List<String> DataValue = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_att_records);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "History")));

        ipAddress = getString(R.string.filename_empnamelist).toString() ;
        getJSON(ipAddress);

        DataValue.add(0, getIntent().getStringExtra("ID") + " - " + getIntent().getStringExtra("Name"));

        spNameList =(Spinner) findViewById(R.id.spNameList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DataValue);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNameList.setAdapter(dataAdapter);

        setCurrentMonthOnView();

        SearchDate = (TextView) findViewById(R.id.SearchDate);

        SearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pdf_refresh, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, year, month-1, day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                return dialog;
        }

        return null;
    }

    public void setCurrentMonthOnView() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        day1 = c.get(Calendar.DAY_OF_WEEK);

        if ((day) < 10) {
            newday = "0" + day;
        } else {
            newday = String.valueOf(day);
            ;
        }

        month = month + 1;

        if (month == 1)
            month2 = "Jan";
        else if (month == 2)
            month2 = "Feb";
        else if (month == 3)
            month2 = "Mar";
        else if (month == 4)
            month2 = "Apr";
        else if (month == 5)
            month2 = "May";
        else if (month == 6)
            month2 = "Jun";
        else if (month == 7)
            month2 = "Jul";
        else if (month == 8)
            month2 = "Aug";
        else if (month == 9)
            month2 = "Sep";
        else if (month == 10)
            month2 = "Oct";
        else if (month == 11)
            month2 = "Nov";
        else if (month == 12)
            month2 = "Dec";

        if ((month) < 10) {
            newmonth = "0" + (month);
        } else {
            newmonth = String.valueOf(month);
            ;
        }

        if (day1 == 1)
            day2 = "Sun";
        else if (day1 == 2)
            day2 = "Mon";
        else if (day1 == 3)
            day2 = "Tue";
        else if (day1 == 4)
            day2 = "Wed";
        else if (day1 == 5)
            day2 = "Thu";
        else if (day1 == 6)
            day2 = "Fri";
        else if (day1 == 7)
            day2 = "Sat";

        TextView SearchStartDate = (TextView) findViewById(R.id.SearchDate);

        SearchStartDate.setText(new StringBuilder()
                .append(newmonth).append("-")
                .append(year));

        strSearchDDMM = String.valueOf(newmonth) + "-" + String.valueOf(year) ;
        strttldays = String.valueOf(day);

        String strData1 = spNameList.getSelectedItem().toString();

        strempid = getIntent().getStringExtra("ID");
        strempname = getIntent().getStringExtra("Name");

        URLPage = getString(R.string.filename_staffhistory) + "?empid=" + strempid + "&empname=" + strempname + "&month=" + strSearchDDMM;

        webView = (WebView) findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(URLPage);

        loadingProgressBar=(ProgressBar)findViewById(R.id.progressBar);

        webView.setWebChromeClient(new WebChromeClient() {

            // this will be called on page loading progress

            @Override

            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);


                loadingProgressBar.setProgress(newProgress);

                if (newProgress == 100) {
                    loadingProgressBar.setVisibility(View.GONE);


                } else{
                    loadingProgressBar.setVisibility(View.VISIBLE);

                }

            }

        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String sHtmlTemplate = "<html><head></head><body><img src=\"file:///android_asset/weberror.png\" style ='width:100%'></body></html>";
                webView.loadDataWithBaseURL(null, sHtmlTemplate, "text/html", "utf-8", null);
            }
        });


    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month1 = selectedMonth;
            day = selectedDay;

            GregorianCalendar calendar = new GregorianCalendar(selectedYear, selectedMonth, selectedDay);
            int i = calendar.get(Calendar.DAY_OF_WEEK);

            if ((day) < 10) {
                newday = "0" + day;
            } else {
                newday = String.valueOf(day);
                ;
            }

            month1 = month1 + 1;
            if ((month1) < 10) {
                newmonth = "0" + (month1);
            } else {
                newmonth = String.valueOf(month1);
                ;
            }

            if (i == 1)
                day3 = "Sun";
            else if (i == 2)
                day3 = "Mon";
            else if (i == 3)
                day3 = "Tue";
            else if (i == 4)
                day3 = "Wed";
            else if (i == 5)
                day3 = "Thu";
            else if (i == 6)
                day3 = "Fri";
            else if (i == 7)
                day3 = "Sat";

            if (month == 1)
                month2 = "Jan";
            else if (month == 2)
                month2 = "Feb";
            else if (month == 3)
                month2 = "Mar";
            else if (month == 4)
                month2 = "Apr";
            else if (month == 5)
                month2 = "May";
            else if (month == 6)
                month2 = "Jun";
            else if (month == 7)
                month2 = "Jul";
            else if (month == 8)
                month2 = "Aug";
            else if (month == 9)
                month2 = "Sep";
            else if (month == 10)
                month2 = "Oct";
            else if (month == 11)
                month2 = "Nov";
            else if (month == 12)
                month2 = "Dec";


            TextView SearchStartDate = (TextView) findViewById(R.id.SearchDate);

            SearchStartDate.setText(new StringBuilder()
                    .append(newmonth).append("-")
                    .append(year).append(" "));

            strSearchDDMM = String.valueOf(newmonth) + "-" + String.valueOf(year);

            String strData = spNameList.getSelectedItem().toString();

            String[] ListDetails = strData.toString().split(" - ");

            strempid = ListDetails[0].toString(); //getIntent().getStringExtra("ID");
            strempname =ListDetails[1].toString(); //getIntent().getStringExtra("Name");

            URLPage = getString(R.string.filename_staffhistory) + "?empid=" + strempid + "&empname=" + strempname + "&month=" + strSearchDDMM;

            webView = (WebView) findViewById(R.id.WebView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.loadUrl(URLPage);

            loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);

            webView.setWebChromeClient(new WebChromeClient() {

                // this will be called on page loading progress

                @Override

                public void onProgressChanged(WebView view, int newProgress) {

                    super.onProgressChanged(view, newProgress);


                    loadingProgressBar.setProgress(newProgress);

                    if (newProgress == 100) {
                        loadingProgressBar.setVisibility(View.GONE);
                        //Toast.makeText(StaffAttRecords.this, ListDetails[0].toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(StaffAttRecords.this, ListDetails[1].toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        loadingProgressBar.setVisibility(View.VISIBLE);

                    }

                }

            });

            webView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    String sHtmlTemplate = "<html><head></head><body><img src=\"file:///android_asset/weberror.png\" style ='width:100%'></body></html>";
                    webView.loadDataWithBaseURL(null, sHtmlTemplate, "text/html", "utf-8", null);
                }
            });

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.actRefresh:

                strempid = getIntent().getStringExtra("ID");
                strempname = getIntent().getStringExtra("Name");

                URLPage = getString(R.string.filename_staffhistory) + "?empid=" + strempid + "&empname=" + strempname + "&month=" + strSearchDDMM;

                webView = (WebView) findViewById(R.id.WebView);

                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.loadUrl(URLPage) ;

                loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);

                webView.setWebChromeClient(new WebChromeClient() {

                    @Override

                    public void onProgressChanged(WebView view, int newProgress) {

                        super.onProgressChanged(view, newProgress);


                        loadingProgressBar.setProgress(newProgress);

                        if (newProgress == 100) {
                            loadingProgressBar.setVisibility(View.GONE);

                        } else {
                            loadingProgressBar.setVisibility(View.VISIBLE);

                        }
                    }

                });


                webView.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        String sHtmlTemplate = "<html><head></head><body><img src=\"file:///android_asset/famehouselogowhite.png\" style ='width:100%'><h2 align = 'center'>Unable to connect to the server</h2><p align = 'center'>The page you are looking for might have been removed had its name changed or is temporarily unavailable.</p></body></html>";
                        webView.loadDataWithBaseURL(null, sHtmlTemplate, "text/html", "utf-8", null);
                    }
                });

                return true;

            case R.id.actPDF:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

                    createWebPrintJob(webView);
                } else {
                    Toast.makeText(StaffAttRecords.this, "No PDF Service Available!", Toast.LENGTH_SHORT).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " History - " + SearchDate;
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        if (printManager != null) {
            printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (printJob != null) {
            if (printJob.isCompleted()) {
                Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
            } else if (printJob.isStarted()) {
                Toast.makeText(this, "isStarted", Toast.LENGTH_SHORT).show();

            } else if (printJob.isBlocked()) {
                Toast.makeText(this, "isBlocked", Toast.LENGTH_SHORT).show();

            } else if (printJob.isCancelled()) {
                Toast.makeText(this, "isCancelled", Toast.LENGTH_SHORT).show();

            } else if (printJob.isFailed()) {
                Toast.makeText(this, "isFailed", Toast.LENGTH_SHORT).show();

            } else if (printJob.isQueued()) {
                Toast.makeText(this, "isQueued", Toast.LENGTH_SHORT).show();

            }
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

                count = 1;

                try {
                    FlagTimeout = false;
                    ErrMsg = "";
                    java.net.URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(15000);

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        DataValue.add(count, json);
                        count = count + 1;

                    }
                    return sb.toString().trim();

                } catch (java.net.SocketTimeoutException e) {
                    FlagTimeout = true;
                    Toast.makeText(StaffAttRecords.this, "Timeout", Toast.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    ErrMsg = e.toString();
                    Toast.makeText(StaffAttRecords.this, "Error : " + ErrMsg.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }


    private void ChoppingData(String json) throws JSONException {
        //DataValue.add(json.toString());
        //Toast.makeText(StaffAttRecords.this, "1", Toast.LENGTH_SHORT).show();
    }



}