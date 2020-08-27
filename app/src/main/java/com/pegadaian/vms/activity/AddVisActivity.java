package com.pegadaian.vms.activity;

// MOHAMAD ALFIQKO MAULANA
// ARRIFQI AZIZ ARDHIANSYAH

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pegadaian.vms.R;
import com.pegadaian.vms.adapter.LocationAdapter;
import com.pegadaian.vms.model.LocationData;
import com.pegadaian.vms.model.VisitorData;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AddVisActivity extends AppCompatActivity {

    @BindView(R.id.imgLogo) ImageView imgLogo;
    @BindView(R.id.rlAppbar) RelativeLayout rlAppbar;
    @BindView(R.id.tvAppbar) TextView txtAppbar;
    @BindView(R.id.imgAddFoto) ImageView visitorImage;
    @BindView(R.id.imgAddQr) ImageView qrImage;
    @BindView(R.id.tvAmbil) TextView txtAmbil;
    @BindView(R.id.etNama) EditText etNama;
    @BindView(R.id.etTelp) EditText etTelp;
    @BindView(R.id.etInstansi) EditText etInstansi;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.spHost) MaterialSpinner spHost;
    @BindView(R.id.tvLokasi) TextView txtLokasi;
    @BindView(R.id.etTujuan) EditText etTujuan;

    public static final int CAMERA_PERM_CODE = 19;
    public static final int CAMERA_REQ_CODE = 45;

    List<LocationData> locationDataList;
    LocationAdapter locationAdapter;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    EditText etSearch;
    ImageView imgSearch, imgClear;
    String id, dataFoto, dataQr, kosong = "";
    Dialog dialog;

    // MENDAPATKAN TANGGAL & WAKTU TERKINI
    SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvis);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this);

        id = dateFormat.format(new Date()) + clockFormat.format(new Date()) +  UUID.randomUUID();
        storageReference = FirebaseStorage.getInstance().getReference();

        // SET APPBAR
        imgLogo.setVisibility(View.GONE);
        rlAppbar.setVisibility(View.VISIBLE);
        txtAppbar.setText("Tambah Data");

        // CUSTOM TOAST
        Toasty.Config.getInstance()
                .setToastTypeface(ResourcesCompat.getFont(this, R.font.montserratbold))
                .setTextSize(12)
                .apply();

        // DAFTAR NAMA HOST
        List<String> host = new ArrayList<>();
        host.add("Joko Supriyatno (Transformation Office)");
        host.add("Bowo Subono (Keuangan)");
        host.add("Eka Jayadi (Hukum)");
        host.add("Adam Iskandar (Budaya Kerja)");
        spHost.setItems(host);

        // JIKA ITEM SPINNER HOST DI KLIK, MAKA...
        spHost.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                spHost.setOnItemSelectedListener(this::onItemSelected);
            }
        });
    }

    // BUTTON COBA LAGI KONEKSI
    public void btnCobaLagi(View view) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
        new Handler().postDelayed(() -> btnSimpan(view), 2000);
    }

    // BUTTON AMBIL FOTO
    public void btnAmbilFoto(View view) {

        // MENDAPATKAN PERMISSION KAMERA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    // MENGECEK PERMISSION KAMERA
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toasty.error(AddVisActivity.this, "Izin kamera dibutuhkan",
                        Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    // MEMBUKA KAMERA
    public void openCamera() {

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQ_CODE);
    }

    // SETELAH MELAKUKAN FOTO, MAKA...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQ_CODE && resultCode == RESULT_OK) {
            Bitmap imageVisitor = (Bitmap) data.getExtras().get("data");
            visitorImage.setImageBitmap(imageVisitor);
            txtAmbil.setText("U B A H");
        } else {
            if (visitorImage.getDrawable() == null) {
                Toasty.warning(AddVisActivity.this, "Foto belum diambil",
                        Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.warning(AddVisActivity.this, "Foto tidak diubah",
                        Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    // MEMILIH LOKASI KUNJUNGAN
    public void pilihLokasi(View view) {

        dialog.show();
        dialog.setContentView(R.layout.location_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        etSearch = dialog.findViewById(R.id.etSearchLoc);
        imgSearch = dialog.findViewById(R.id.imgSearchLoc);
        imgClear = dialog.findViewById(R.id.imgClearLoc);
        recyclerView = dialog.findViewById(R.id.rvLocation);

        imgClear.setOnClickListener(view1 -> {
            etSearch.setText(null);
            etSearch.clearFocus();
        });

        tampilLokasi();
        searchData();
    }

    // MENAMPILKAN DAFTAR LOKASI
    public void tampilLokasi() {

        // SET ADAPTER
        locationDataList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationDataList, this::recyclerViewListClicked);
        recyclerView.setAdapter(locationAdapter);

        // CONVERTER RECYCLER VIEW
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Lokasi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                locationDataList.clear();
                locationAdapter.notifyDataSetChanged();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                    LocationData locationData = itemSnapshot.getValue(LocationData.class);
                    locationDataList.add(locationData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    // JIKA ITEM DI KLIK, MAKA...
    public void recyclerViewListClicked(View v, String nama) {

        txtLokasi.setText(nama);
        dialog.dismiss();
    }

    // CARI LOKASI
    public void searchData() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                imgClear.setVisibility(View.GONE);
                imgSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().isEmpty()) {
                    imgClear.setVisibility(View.GONE);
                    imgSearch.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.VISIBLE);
                    imgSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                // FILTER DATA
                ArrayList<LocationData> filterList = new ArrayList<>();

                for (LocationData item : locationDataList) {
                    if (item.getItemNama().toLowerCase().contains(s.toString().toLowerCase())) {
                        filterList.add(item);
                    }
                }
                locationAdapter.filteredList(filterList);
            }
        });
    }

    // BUTTON SIMPAN
    public void btnSimpan(View view) {

        // MENGECEK KONEKSI INTERNET
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            dialog.dismiss();
            if (visitorImage.getDrawable() == null) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (etNama.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (etTelp.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (etInstansi.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (etEmail.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (spHost.getText().toString().equals("-")) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (txtLokasi.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else if (etTujuan.getText().toString().isEmpty()) {
                Toasty.error(AddVisActivity.this, "Data belum lengkap",
                        Toast.LENGTH_SHORT, true).show();
            } else {
                uploadImageQr();
            }
        } else {
            dialog.show();
            dialog.setContentView(R.layout.offline_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    // UPLOAD QR KE FIREBASE
    public void uploadImageQr() {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // MEMBUAT QR DARI TANGGAL
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(id, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            // MENGUBAH UKURAN BITMAP QR
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // MENDAPATKAN DATA DARI IMAGE VIEW
        Bitmap bitmapQr = ((BitmapDrawable) qrImage.getDrawable()).getBitmap();
        ByteArrayOutputStream streamQr = new ByteArrayOutputStream();

        // KOMPRESS BITMAP KE JPEG
        bitmapQr.compress(Bitmap.CompressFormat.JPEG, 100, streamQr);
        byte[] bytesQr = streamQr.toByteArray();

        // MENENTUKAN FILE PENYIMPANAN
        UploadTask uploadTaskQr = storageReference.child("QrImage").child(dateFormat.format(new Date()))
                .child(etNama.getText().toString()).putBytes(bytesQr);

        uploadTaskQr.addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri urlImage = uriTask.getResult();
            dataQr = urlImage.toString();
            uploadImageVisitor();
        }).addOnFailureListener(e -> Toasty.error(AddVisActivity.this, "Qr gagal disimpan",
                Toast.LENGTH_SHORT, true).show());
    }

    // UPLOAD FOTO KE FIREBASE
    public void uploadImageVisitor() {

        // MENDAPATKAN DATA DARI IMAGE VIEW
        Bitmap bitmapVisitor = ((BitmapDrawable) visitorImage.getDrawable()).getBitmap();
        ByteArrayOutputStream streamVisitor = new ByteArrayOutputStream();

        // KOMPRESS BITMAP KE JPEG
        bitmapVisitor.compress(Bitmap.CompressFormat.JPEG, 100, streamVisitor);
        byte[] bytesVisitor = streamVisitor.toByteArray();

        // MENENTUKAN FILE PENYIMPANAN
        UploadTask uploadTask = storageReference.child("VisitorImage").child(dateFormat.format(new Date()))
                .child(etNama.getText().toString()).putBytes(bytesVisitor);

        uploadTask.addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri urlImage = uriTask.getResult();
            dataFoto = urlImage.toString();
            uploadVisitor();
        }).addOnFailureListener(e -> Toasty.error(AddVisActivity.this, "Foto gagal disimpan",
                Toast.LENGTH_SHORT, true).show());
    }

    // UPLOAD DATA VISITOR KE FIREBASE
    public void uploadVisitor() {

        VisitorData visitorData = new VisitorData(etNama.getText().toString(), etTelp.getText().toString(),
                etInstansi.getText().toString(), etEmail.getText().toString(), spHost.getText().toString(),
                txtLokasi.getText().toString(), etTujuan.getText().toString(), kosong, kosong, dataFoto,
                dataQr, clockFormat.format(new Date()), dateFormat.format(new Date()), kosong);

        FirebaseDatabase.getInstance().getReference("Visitor").child(id).setValue(visitorData)
                .addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                progressDialog.dismiss();
                startActivity(new Intent(AddVisActivity.this, MainActivity.class));
                this.finish();
                Toasty.success(AddVisActivity.this, "Data visitor tersimpan",
                        Toast.LENGTH_SHORT, true).show();
            }
        }).addOnFailureListener(e -> Toasty.error(AddVisActivity.this, "Data gagal disimpan",
                Toast.LENGTH_SHORT, true).show());
    }

    // BUTTON BACK
    public void btnBack(View view) {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}