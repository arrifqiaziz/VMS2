package com.pegadaian.vms.activity;

// MOHAMAD ALFIQKO MAULANA
// ARRIFQI AZIZ ARDHIANSYAH

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pegadaian.vms.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CheckActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    @BindView(R.id.qrReader) QRCodeReaderView qrCodeReaderView;

    DatabaseReference databaseReference;

    // MENDAPATKAN TANGGAL & WAKTU TERKINI
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    String myCurrentDateTime = simpleDateFormat.format(new Date());

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Visitor");

        // CUSTOM TOAST
        Toasty.Config.getInstance()
                .setToastTypeface(ResourcesCompat.getFont(this, R.font.montserratbold))
                .setTextSize(12)
                .apply();

        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        Intent getData = getIntent();
        int code = getData.getIntExtra("requestCode", 0);

        if (code == 1) {
            // MENGUPDATE DATA
            databaseReference.child(text).child("itemCheckin").setValue(myCurrentDateTime);
            databaseReference.child(text).child("itemStatus").setValue("Checked In");

            this.finish();
            Toasty.success(CheckActivity.this, "Checkin berhasil",
                    Toast.LENGTH_SHORT, true).show();

        } else if (code == 2) {
            // MENGUPDATE DATA
            databaseReference.child(text).child("itemCheckout").setValue(myCurrentDateTime);
            databaseReference.child(text).child("itemStatus").setValue("Checked Out");

            this.finish();
            Toasty.success(CheckActivity.this, "Checkout berhasil",
                    Toast.LENGTH_SHORT, true).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    // BUTTON BACK
    public void btnBackCheck(View view) {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
