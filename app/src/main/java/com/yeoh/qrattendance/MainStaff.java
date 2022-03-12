package com.yeoh.qrattendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.accounts.Account;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.io.OutputStreamWriter;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainStaff extends AppCompatActivity {

    private ImageView qrCodeIV;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    TextView txtName;
    TextView txtID;
    TextView btnLogout;
    Button btnAttRecords;

    String UserMessage;

    private final static String useractivation = "login.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_staff);

        getSupportActionBar().hide();

        qrCodeIV = findViewById(R.id.idIVQRCode);
        txtName = findViewById(R.id.txtTitle2);
        txtID = findViewById(R.id.txtTitle3);

        btnAttRecords = (Button) findViewById(R.id.btnAttRecords);
        btnLogout = (TextView) findViewById(R.id.btnLogout);

        txtName.setText(getIntent().getStringExtra("Name"));
        txtID.setText(getIntent().getStringExtra("ID"));
        UserMessage = getIntent().getStringExtra("Msg");

        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(txtName.getText().toString() + "," + txtID.getText().toString(), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }

        btnAttRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainStaff.this, History.class);
                intent.putExtra("ID", txtID.getText().toString());
                intent.putExtra("Name", txtName.getText().toString());
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainStaff.this);
                builder.setCancelable(false);
                builder.setMessage("Would you like to Logout this application?");
                builder.setPositiveButton("YES, I AM SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StrictMode.enableDefaults();
                        DeleteAccount();
                        dialog.cancel();
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

    public void DeleteAccount()
    {
        try {
            OutputStreamWriter out= new OutputStreamWriter(openFileOutput(useractivation, 0));

            out.write(("") + "\n");
            out.write(("") + "\n");
            out.write(("") + "\n");
            out.write("Logout\n");
            out.close();

            ActivityCompat.finishAffinity(MainStaff.this);
            moveTaskToBack(true);

            System.exit(0);
        }

        catch (Throwable t) {

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
                ActivityCompat.finishAffinity(MainStaff.this);
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