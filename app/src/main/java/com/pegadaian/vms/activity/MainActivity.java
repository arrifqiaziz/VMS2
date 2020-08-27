package com.pegadaian.vms.activity;

// MOHAMAD ALFIQKO MAULANA

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pegadaian.vms.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvGreeting) TextView txtGreeting;
    @BindView(R.id.lnVisBaru) LinearLayout cardTambah;
    @BindView(R.id.lnVisitor) LinearLayout cardVisitor;
    @BindView(R.id.lnCheckin) LinearLayout cardCheckin;
    @BindView(R.id.lnCheckout) LinearLayout cardCheckout;

    public static final int CAMERA_PERM_CODE = 19;
    boolean doubleBack = false;
    ProgressDialog progressDialog;
    Dialog dialog;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this);

        // CUSTOM TOAST
        Toasty.Config.getInstance()
                .setToastTypeface(ResourcesCompat.getFont(this, R.font.montserratbold))
                .setTextSize(12)
                .apply();

        cekKoneksi();
        greeting();
    }

    public void btnCobaLagi(View view) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
        cekKoneksi();
    }

    // MENGECEK KONEKSI INTERNET
    public void cekKoneksi() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            dialog.dismiss();
        } else {
            dialog.show();
            dialog.setContentView(R.layout.offline_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    // SETTING WAKTU GREETING
    private void greeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            txtGreeting.setText("Selamat pagi.");
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            txtGreeting.setText("Selamat siang.");
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            txtGreeting.setText("Selamat sore.");
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            txtGreeting.setText("Selamat malam.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == cardTambah) {
            startActivity(new Intent(MainActivity.this, AddVisActivity.class));
        } else if (v == cardVisitor) {
            startActivity(new Intent(MainActivity.this, VisitorActivity.class));
        } else if (v == cardCheckin) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            } else {
                startActivity(new Intent(MainActivity.this, CheckActivity.class)
                        .putExtra("requestCode", 1));
            }
        } else if (v == cardCheckout) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            } else {
                startActivity(new Intent(MainActivity.this, CheckActivity.class)
                        .putExtra("requestCode", 2));
            }
        }
    }

    public void onBackPressed() {

        if (doubleBack) {
            super.onBackPressed();
            return;
        }

        this.doubleBack = true;
        Toasty.warning(MainActivity.this, "Tekan sekali lagi untuk keluar",
                Toast.LENGTH_SHORT, true).show();

        new Handler().postDelayed(() -> doubleBack = false, 2000);
    }
}
