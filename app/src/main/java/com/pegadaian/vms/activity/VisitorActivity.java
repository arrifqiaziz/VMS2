package com.pegadaian.vms.activity;

// MOHAMAD ALFIQKO MAULANA

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pegadaian.vms.R;
import com.pegadaian.vms.adapter.VisitorAdapter;
import com.pegadaian.vms.model.VisitorData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VisitorActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.imgLogo) ImageView imgLogo;
    @BindView(R.id.rlAppbar) RelativeLayout rlAppbar;
    @BindView(R.id.imgBack) ImageView btnBack;
    @BindView(R.id.tvAppbar) TextView txtData;
    @BindView(R.id.imgFilter) ImageView btnFilter;
    @BindView(R.id.rlSearch) RelativeLayout rlSearch;
    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.imgSearch) ImageView imgSearch;
    @BindView(R.id.imgClear) ImageView imgClear;
    @BindView(R.id.rvVisitor) RecyclerView recyclerView;
    @BindView(R.id.imgNoData) ImageView imgNoData;
    @BindView(R.id.lnOffline) LinearLayout lnOffline;

    List<VisitorData> visitorDataList;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    VisitorAdapter visitorAdapter;
    RadioButton rbSemua, rbIn, rbOut;
    Query query;
    Dialog dialog;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        dialog = new Dialog(this);
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        query = databaseReference.child("Visitor");

        // SET APPBAR
        imgLogo.setVisibility(View.GONE);
        rlAppbar.setVisibility(View.VISIBLE);
        btnFilter.setVisibility(View.VISIBLE);
        rlSearch.setVisibility(View.VISIBLE);

        tampilData();
        searchData();
    }

    @Override
    public void onClick(View v) {
        if (v == btnFilter) {
            dialog.show();
            dialog.setContentView(R.layout.filter_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setGravity(Gravity.TOP | Gravity.END);

            rbSemua = dialog.findViewById(R.id.rbSemua);
            rbIn = dialog.findViewById(R.id.rbCheckedin);
            rbOut = dialog.findViewById(R.id.rbCheckedout);

            // MENSETTING CHECKED PADA RADIO BUTTON
            if (txtData.getText().equals("Semua Data")) {
                rbSemua.setChecked(true);
            } else if (txtData.getText().equals("Checked In")) {
                rbIn.setChecked(true);
            } else {
                rbOut.setChecked(true);
            }

            // MELIHAT PERUBAHAN CHECKED
            rbSemua.setOnCheckedChangeListener((compoundButton, b) -> {
                if (compoundButton.isChecked()) {
                    txtData.setText("Semua Data");
                    query = databaseReference.child("Visitor");
                    tampilData();
                }
            });
            rbIn.setOnCheckedChangeListener((compoundButton, b) -> {
                if (compoundButton.isChecked()) {
                    txtData.setText(rbIn.getText());
                    query = databaseReference.child("Visitor").orderByChild("itemStatus").equalTo("Checked In");
                    tampilData();
                }
            });
            rbOut.setOnCheckedChangeListener((compoundButton, b) -> {
                if (compoundButton.isChecked()) {
                    txtData.setText(rbOut.getText());
                    query = databaseReference.child("Visitor").orderByChild("itemStatus").equalTo("Checked Out");
                    tampilData();
                }
            });

        } else if (v == imgClear) {
            etSearch.setText(null);
            etSearch.clearFocus();
        }
    }

    // BUTTON COBA KONEKSI
    public void btnCobaLagi(View view) {

        tampilData();
    }

    // MENAMPILKAN PROGRESS DIALOG
    public void tampilProgress() {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // MENAMPILKAN DATA DARI FIREBASE BERDASARKAN RADIO BUTTON
    public void tampilData() {

        // MENSETTING ULANG TAMPILAN
        etSearch.clearFocus();
        etSearch.setText(null);
        imgNoData.setVisibility(View.GONE);
        dialog.dismiss();
        tampilProgress();

        // SET ADAPTER
        visitorDataList = new ArrayList<>();
        visitorAdapter = new VisitorAdapter(VisitorActivity.this, visitorDataList);
        recyclerView.setAdapter(visitorAdapter);

        // CONVERTER RECYCLER VIEW
        GridLayoutManager gridLayoutManager = new GridLayoutManager(VisitorActivity.this, 1);
        gridLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        // MENGECEK KONEKSI INTERNET
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            lnOffline.setVisibility(View.GONE);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    visitorDataList.clear();
                    visitorAdapter.notifyDataSetChanged();

                    if (dataSnapshot.exists()) {
                        imgNoData.setVisibility(View.GONE);

                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                            VisitorData visitorData = itemSnapshot.getValue(VisitorData.class);
                            visitorData.setKey(itemSnapshot.getKey());
                            visitorDataList.add(visitorData);
                        }

                    } else {
                        imgNoData.setVisibility(View.VISIBLE);
                    }
                    new Handler().postDelayed(() -> progressDialog.dismiss(), 1000);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        } else {
            new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
            new Handler().postDelayed(() -> lnOffline.setVisibility(View.VISIBLE), 2000);
        }
    }

    // CARI DATA VISITOR
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
                ArrayList<VisitorData> filterList = new ArrayList<>();

                for (VisitorData item : visitorDataList) {
                    if (item.getItemNama().toLowerCase().contains(s.toString().toLowerCase())) {
                        filterList.add(item);
                        imgNoData.setVisibility(View.GONE);
                    } else if (filterList.isEmpty()){
                        imgNoData.setVisibility(View.VISIBLE);
                    } else {
                        imgNoData.setVisibility(View.GONE);
                    }
                }
                visitorAdapter.filteredList(filterList);
            }
        });
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
