package com.example.acadMate;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

//import com.example.profile_page.R;

public class AnnouncementDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    public static AnnouncementDialogFragment newInstance(String title, String content) {
        AnnouncementDialogFragment fragment = new AnnouncementDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_announcement, container, false);
        String title = getArguments().getString(ARG_TITLE);
        String content = getArguments().getString(ARG_CONTENT);

        TextView titleView = view.findViewById(R.id.dialog_title);
        TextView contentView = view.findViewById(R.id.dialog_content);
        titleView.setText(title);
        contentView.setText(content);

        ImageView closeButton = view.findViewById(R.id.close_dialog);
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;

            // Set the width to 200dp and the height to wrap_content
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_width); // Use 200dp from resources
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setAttributes(params);
        }
    }

}

