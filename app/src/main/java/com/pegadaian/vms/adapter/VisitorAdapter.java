package com.pegadaian.vms.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pegadaian.vms.R;
import com.pegadaian.vms.activity.DetailVisitorActivity;
import com.pegadaian.vms.model.VisitorData;

import java.util.ArrayList;
import java.util.List;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder> {

    private Context context;
    private List<VisitorData> visitorDataList;
    private int lastPosition = -1;

    public VisitorAdapter(Context context, List<VisitorData> visitorDataList) {
        this.context = context;
        this.visitorDataList = visitorDataList;
    }

    // DATA VIEW HOLDER
    @Override
    public VisitorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_visitor, viewGroup, false);

        return new VisitorViewHolder(mView);
    }

    // MENDAPATKAN DATA VISITOR UNTUK DITAMPILKAN
    @Override
    public void onBindViewHolder(@NonNull final VisitorViewHolder visitorViewHolder, int i) {

        Glide.with(context)
                .load(visitorDataList.get(i).getItemFoto())
                .into(visitorViewHolder.imgVisitor);

        visitorViewHolder.tvNama.setText(visitorDataList.get(i).getItemNama());
        visitorViewHolder.tvTelp.setText(visitorDataList.get(i).getItemTelp());
        visitorViewHolder.tvInstansi.setText(visitorDataList.get(i).getItemInstansi());
        visitorViewHolder.tvJam.setText(visitorDataList.get(i).getItemJamDibuat());
        visitorViewHolder.tvTgl.setText(visitorDataList.get(i).getItemTglDibuat());
        visitorViewHolder.tvTujuan.setText(visitorDataList.get(i).getItemTujuan());

        if (visitorDataList.get(i).getItemStatus().isEmpty()) {
            visitorViewHolder.tvStatus.setText("-");
        } else {
            visitorViewHolder.tvStatus.setText(visitorDataList.get(i).getItemStatus());
        }

        visitorViewHolder.cardVisitor.setOnClickListener(view -> {

            Intent intent = new Intent(context, DetailVisitorActivity.class);
            intent.putExtra("Foto", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemFoto());
            intent.putExtra("Qr", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemQr());
            intent.putExtra("Nama", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemNama());
            intent.putExtra("Telp", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemTelp());
            intent.putExtra("Perusahaan", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemInstansi());
            intent.putExtra("Email", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemEmail());
            intent.putExtra("Checkin", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemCheckin());
            intent.putExtra("Checkout", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getItemCheckout());
            intent.putExtra("keyValue", visitorDataList.get(visitorViewHolder.getAdapterPosition()).getKey());
            context.startActivity(intent);
        });
        setAnimation(visitorViewHolder.itemView, i);
    }

    // ANIMASI LOAD DATA
    public void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {

            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return visitorDataList.size();
    }

    public void filteredList(ArrayList<VisitorData> filterList) {

        visitorDataList = filterList;
        notifyDataSetChanged();
    }

    // CLASS VIEW HOLDER
    public class VisitorViewHolder extends RecyclerView.ViewHolder {

        CardView cardVisitor;
        ImageView imgVisitor;
        TextView tvNama, tvTelp, tvInstansi, tvJam, tvTgl, tvTujuan, tvStatus;

        public VisitorViewHolder(@NonNull View itemView) {
            super(itemView);

            cardVisitor = itemView.findViewById(R.id.cvVisitor);
            imgVisitor = itemView.findViewById(R.id.imgVisitor);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvTelp = itemView.findViewById(R.id.tvTelp);
            tvInstansi = itemView.findViewById(R.id.tvInstansi);
            tvJam = itemView.findViewById(R.id.tvJamDibuat);
            tvTgl = itemView.findViewById(R.id.tvTglDibuat);
            tvTujuan = itemView.findViewById(R.id.tvTujuan);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}