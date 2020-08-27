package com.pegadaian.vms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pegadaian.vms.R;
import com.pegadaian.vms.model.LocationData;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationData> locationDataList;
    private RecyclerViewClickListener itemListener;

    public LocationAdapter(List<LocationData> locationDataList, RecyclerViewClickListener itemListener) {

        this.locationDataList = locationDataList;
        this.itemListener = itemListener;
    }

    // DATA VIEW HOLDER
    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_location, viewGroup, false);

        return new LocationAdapter.LocationViewHolder(mView);
    }

    // MENDAPATKAN DATA VISITOR UNTUK DITAMPILKAN
    @Override
    public void onBindViewHolder(@NonNull final LocationAdapter.LocationViewHolder locationViewHolder, int i) {

        locationViewHolder.txtLokasi.setText(locationDataList.get(i).getItemNama());
    }

    @Override
    public int getItemCount() {

        return locationDataList.size();
    }

    public void filteredList(ArrayList<LocationData> filterList) {

        locationDataList = filterList;
        notifyDataSetChanged();
    }

    // CLASS VIEW HOLDER
    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtLokasi;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLokasi = itemView.findViewById(R.id.tvViewLokasi);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            itemListener.recyclerViewListClicked(v, txtLokasi.getText().toString());
        }
    }
}
