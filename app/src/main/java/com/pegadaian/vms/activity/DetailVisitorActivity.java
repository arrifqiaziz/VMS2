package com.pegadaian.vms.activity;

// MOHAMAD ALFIQKO MAULANA
// ARRIFQI AZIZ ARDHIANSYAH

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.print.PrintHelper;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pegadaian.vms.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class DetailVisitorActivity extends AppCompatActivity {

    @BindView(R.id.tvDetailNama) TextView txtNama;
    @BindView(R.id.tvDetailInstansi) TextView txtPerusahaan;
    @BindView(R.id.tvDetailEmail) TextView txtEmail;
    @BindView(R.id.tvDetailTelp) TextView txtTelp;
    @BindView(R.id.tvDetailCheckin) TextView txtCheckin;
    @BindView(R.id.tvDetailCheckout) TextView txtCheckout;
    @BindView(R.id.tvPrintNama) TextView txtPrintNama;
    @BindView(R.id.imgDetailQr) ImageView imgQr;
    @BindView(R.id.imgDetailFoto) ImageView imgVisitor;
    @BindView(R.id.imgPrintQr) ImageView imgPrintQr;
    @BindView(R.id.lnPrint) LinearLayout lnPrintVis;

    ProgressDialog progressDialog;
    Bundle bundle;
    String key, fotoUrl, qrUrl = "";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailvisitor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);

        // CUSTOM TOAST
        Toasty.Config.getInstance()
                .setToastTypeface(ResourcesCompat.getFont(this, R.font.montserratbold))
                .setTextSize(15)
                .apply();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this)
                    .load(bundle.getString("Foto"))
                    .into(imgVisitor);
            Glide.with(this)
                    .load(bundle.getString("Qr"))
                    .into(imgQr);
            Glide.with(this)
                    .load(bundle.getString("Qr"))
                    .into(imgPrintQr);

            key = bundle.getString("keyValue");
            qrUrl = bundle.getString("Qr");
            fotoUrl = bundle.getString("Foto");
            txtNama.setText(bundle.getString("Nama"));
            txtPerusahaan.setText(bundle.getString("Perusahaan"));
            txtTelp.setText(bundle.getString("Telp"));
            txtEmail.setText(bundle.getString("Email"));
            txtPrintNama.setText(bundle.getString("Nama"));

            // MENGECEK WAKTU CHECK IN
            if (bundle.getString("Checkin").equals("")) {
                txtCheckin.setText("-");
            } else {
                txtCheckin.setText(bundle.getString("Checkin"));
            }

            // MENGECEK WAKTU CHECK OUT
            if (bundle.getString("Checkout").equals("")) {
                txtCheckout.setText("-");
            } else {
                txtCheckout.setText(bundle.getString("Checkout"));
            }
        }
    }

    // BUTTON CETAK
    public void btnCetak(View view) {

        lnPrintVis.setDrawingCacheEnabled(true);
        lnPrintVis.buildDrawingCache();
        Bitmap bitmapVis = lnPrintVis.getDrawingCache();

        PrintHelper photoPrinter = new PrintHelper(DetailVisitorActivity.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("visitor", bitmapVis);
    }

    // BUTTON HAPUS
    public void btnDelete(View view) {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        deleteQr();
    }

    // MENGHAPUS QR
    public void deleteQr() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Visitor");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference qrReference = storage.getReferenceFromUrl(qrUrl);

        qrReference.delete().addOnSuccessListener(aVoid -> {
            reference.child(key).removeValue();
            deleteFoto();
        });
    }

    // MENGHAPUS FOTO VISITOR
    public void deleteFoto() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Visitor");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fotoReference = storage.getReferenceFromUrl(fotoUrl);

        fotoReference.delete().addOnSuccessListener(aVoid -> {
            reference.child(key).removeValue();
            progressDialog.dismiss();
            Toasty.success(DetailVisitorActivity.this, "Data visitor telah dihapus",
                    Toast.LENGTH_SHORT, true).show();
            startActivity(new Intent(getApplicationContext(), VisitorActivity.class));
            finish();
        });
    }

    // BUTTON BACK
    public void btnBack(View view) {
        this.finish();
    }
}
