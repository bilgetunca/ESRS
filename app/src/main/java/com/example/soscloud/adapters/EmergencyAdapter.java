package com.example.soscloud.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.R;
import com.example.soscloud.Utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder> {
    private List<Emergency> emergencies;
    private Context context;
    private OnEmergencyClickListener listener;
    private OnEmergencyActionListener actionListener;
    private FirebaseFirestore db;

    public interface OnEmergencyClickListener {
        void onEmergencyClick(Emergency emergency, int position);
    }

    public interface OnEmergencyActionListener {
        void onShowMap(Emergency emergency);
        void onTakeAction(Emergency emergency);
        void onMarkComplete(Emergency emergency);
    }

    public EmergencyAdapter(Context context, List<Emergency> emergencies, OnEmergencyClickListener listener) {
        this.context = context;
        this.emergencies = emergencies;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setOnEmergencyActionListener(OnEmergencyActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_list, parent, false);
        return new EmergencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position) {
        Emergency emergency = emergencies.get(position);
        
        holder.tvEmergencyType.setText(emergency.getEmergencyType().getFriendlyName());
        holder.tvReporter.setText("Bildiren: " + emergency.getStudentNo());
        holder.tvTime.setText("Zaman: " + InputValidator.convertTimestampToDateTime(emergency.getTimestamp()));
        holder.tvLocation.setText(String.format("Konum: %.6f, %.6f", 
            emergency.getLatitude(), emergency.getLongitude()));
        holder.tvStatus.setText("Durum: " + emergency.getStatus().getFriendlyName());

        EmergencyStatus status = emergency.getStatus();
        if (status == EmergencyStatus.COMPLETED) {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.tvHeaderStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.tvHeaderStatus.setTextColor(ContextCompat.getColor(context, R.color.maintextcolor));
            holder.tvHeaderStatus.setText("Tamamlandı");
        } else if(status == EmergencyStatus.READ){
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
            holder.tvHeaderStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
            holder.tvHeaderStatus.setText(EmergencyStatus.READ.getFriendlyName());
        }
        else if(status == EmergencyStatus.SENT){
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
            holder.tvHeaderStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
            holder.tvHeaderStatus.setText(EmergencyStatus.SENT.getFriendlyName());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmergencyClick(emergency, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }

    public void updateData(List<Emergency> newEmergencies) {
        this.emergencies = newEmergencies;
        notifyDataSetChanged();
    }

    static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmergencyType;
        LinearLayout linearLayout;
        TextView tvReporter;
        TextView tvTime;
        TextView tvLocation;
        TextView tvStatus;
        TextView tvHeaderStatus;

        EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.content);
            tvEmergencyType = itemView.findViewById(R.id.tvEmergencyType);
            tvReporter = itemView.findViewById(R.id.tvReporter);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvHeaderStatus = itemView.findViewById(R.id.tvHeaderStatus);
        }
    }
} 