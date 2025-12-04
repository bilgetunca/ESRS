package com.example.soscloud.ui.makenotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.soscloud.R;
import com.example.soscloud.databinding.FragmentMakeNotificationBinding;
import com.example.soscloud.ui.common.FirstAidInfoBottomSheet;

public class MakeNotificationFragment extends Fragment {
    private FragmentMakeNotificationBinding makeNotificationBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MakeNotificationViewModel makeNotificationViewModel =
                new ViewModelProvider(this).get(MakeNotificationViewModel.class);

        makeNotificationBinding = FragmentMakeNotificationBinding.inflate(inflater, container, false);
        View root = makeNotificationBinding.getRoot();

        final TextView textView = makeNotificationBinding.textMakeNotification;
        makeNotificationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Acil Durum Butonu
        Button emergencyButton = makeNotificationBinding.buttonEmergency;
        Animation pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_animation);
        emergencyButton.startAnimation(pulseAnimation);
        emergencyButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_makeNotification_to_emergencyFormFragment);
        });

        // İlk Yardım Bilgileri Butonu
        Button btnFirstAidInfo = makeNotificationBinding.buttonFirstAidInfo;
        btnFirstAidInfo.setOnClickListener(v -> {
            FirstAidInfoBottomSheet bottomSheet = FirstAidInfoBottomSheet.newInstance();
            bottomSheet.show(getChildFragmentManager(), "FirstAidInfoBottomSheet");
        });

        // Telefon ile Ulaşmak İçin Butonu
        Button buttonPhoneAccess = makeNotificationBinding.buttonPhoneAccess;
        buttonPhoneAccess.setOnClickListener(v -> showPhoneDialog());

        return root;
    }
    private void showPhoneDialog() {
        String[] options = {
                "📞 ŞİLE: 444 0 799 //n '5609'",
                "📞 ŞİLE: 0531 342 44 12",
                "📞 MASLAK: 444 0 799 //n '6090'",
                "📞 MASLAK: 0531 342 44 21"
        };

        String[] numbers = {
                "44407995609",
                "05313424412",
                "44407996090",
                "05313424421"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("İletişim Numaraları - 7/24 Ulaşabilirsiniz");
        builder.setItems(options, (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + numbers[which]));
            startActivity(intent);
        });
        builder.setNegativeButton("İptal", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        makeNotificationBinding = null;
    }
}