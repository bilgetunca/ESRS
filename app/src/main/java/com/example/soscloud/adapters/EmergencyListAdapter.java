package com.example.soscloud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.R;
import com.example.soscloud.Utils.InputValidator;

import java.util.List;

public class EmergencyListAdapter extends RecyclerView.Adapter<EmergencyListAdapter.ViewHolder> {
    private List<Emergency> emergencies;
    private Context context;
    private OnEmergencyClickListener listener;

    public interface OnEmergencyClickListener {
        void onEmergencyClick(Emergency emergency);
    }

    public EmergencyListAdapter(Context context, List<Emergency> emergencies, OnEmergencyClickListener listener) {
        this.context = context;
        this.emergencies = emergencies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emergency emergency = emergencies.get(position);
        
        // Set header
        holder.tvEmergencyType.setText(emergency.getEmergencyType().getFriendlyName());
        
        // Set header status
        if (emergency.getStatus() == EmergencyStatus.COMPLETED) {
            holder.tvHeaderStatus.setText("Tamamlandı");
            holder.tvHeaderStatus.setBackgroundResource(android.R.color.holo_green_light);
            holder.tvHeaderStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvHeaderStatus.setText("Acil");
            holder.tvHeaderStatus.setBackgroundResource(android.R.color.holo_red_light);
            holder.tvHeaderStatus.setVisibility(View.VISIBLE);
        }
        
        // Set content
        holder.tvReporter.setText("Bildiren: " + emergency.getStudentNo());
        holder.tvTime.setText("Zaman: " + InputValidator.convertTimestampToDateTime(emergency.getTimestamp()));
        holder.tvLocation.setText(String.format("Konum: %.6f, %.6f", 
            emergency.getLatitude(), emergency.getLongitude()));
        holder.tvStatus.setText("Durum: " + emergency.getStatus().getFriendlyName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmergencyClick(emergency);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmergencyType;
        TextView tvHeaderStatus;
        TextView tvDescription;
        TextView tvReporter;
        TextView tvTime;
        TextView tvLocation;
        TextView tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvEmergencyType = itemView.findViewById(R.id.tvEmergencyType);
            tvHeaderStatus = itemView.findViewById(R.id.tvHeaderStatus);
            tvReporter = itemView.findViewById(R.id.tvReporter);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
} 