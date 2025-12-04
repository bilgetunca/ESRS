package com.example.soscloud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SosNotificationsAdapter extends RecyclerView.Adapter<SosNotificationsAdapter.NotificationViewHolder> {

    private List<Emergency> emergencyList;
    private Context context;
    private FirebaseFirestore db;
    private OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Emergency emergency);
    }

    public SosNotificationsAdapter(List<Emergency> emergencyList, Context context) {
        this.emergencyList = emergencyList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.clickListener = listener;
    }

    public void updateData(List<Emergency> newData) {
        this.emergencyList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sos_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Emergency emergency = emergencyList.get(position);
        
        holder.tvNotificationType.setText(emergency.getEmergencyType() != null ? 
                emergency.getEmergencyType().getFriendlyName() : "Diğer");


        
        if (emergency.getStatus() != null) {
            switch (emergency.getStatus()) {
                case READ:
                    holder.btnAction.setText("Okundu");
                    break;
                case COMPLETED:
                    holder.btnAction.setText("Tamamlandı");
                    holder.btnAction.setEnabled(false);
                    break;
                default:
                    holder.btnAction.setText("Aktif");
            }
        } else {
            holder.btnAction.setText("Aktif");
        }
        
        holder.btnAction.setOnClickListener(v -> {
            if (emergency.getStatus() == EmergencyStatus.SENT) {
                updateEmergencyStatus(emergency.getId(), EmergencyStatus.READ);
                holder.btnAction.setText("Okundu");
            } else if (emergency.getStatus() == EmergencyStatus.READ) {
                updateEmergencyStatus(emergency.getId(), EmergencyStatus.COMPLETED);
                holder.btnAction.setText("Tamamlandı");
                holder.btnAction.setEnabled(false);
            }
        });

        // Tüm kart tıklanabilir
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(emergency);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    private void updateEmergencyStatus(String emergencyId, EmergencyStatus newStatus) {
        db.collection("emergencies").document(emergencyId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Durum güncellendi: " + newStatus.getFriendlyName(), Toast.LENGTH_SHORT).show();
                    
                    for (Emergency emergency : emergencyList) {
                        if (emergency.getId().equals(emergencyId)) {
                            emergency.setStatus(newStatus);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Durum güncellenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificationType;
        TextView tvNotificationDescription;
        Button btnAction;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationType = itemView.findViewById(R.id.tvNotificationType);
            tvNotificationDescription = itemView.findViewById(R.id.tvNotificationDescription);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
} 