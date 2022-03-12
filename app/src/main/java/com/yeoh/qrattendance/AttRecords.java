package com.yeoh.qrattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AttRecords extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att_records);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Attendance Records")));

        setCurrentMonthOnView();

        SearchDate = (TextView) findViewById(R.id.SearchDate);

        SearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });


        /*
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd"); // Format date
        String SearchDate =  df1.format(Calendar.getInstance().getTime());

        String URLPage = getString(R.string.filename_history) + SearchDate;

        FlagLoad=false;

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
                } else {
                    loadingProgressBar.setVisibility(View.VISIBLE);

                }

            }

        });
         */
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

        strid =String.valueOf(newmonth) + "-" + String.valueOf(year) ;
        strttldays = String.valueOf(day);

        strSearchDDMM = strid;
        strSearchTtlDays = strttldays;

        URLPage = getString(R.string.filename_staffhistory) + SearchDate;

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

            strid = String.valueOf(newmonth) + "-" + String.valueOf(year);
            strttldays = String.valueOf(day);

            strSearchDDMM = strid;
            strSearchTtlDays = strttldays;

            URLPage = getString(R.string.filename_staffhistory) + SearchDate;

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
        }

        return super.onOptionsItemSelected(item);
    }
}