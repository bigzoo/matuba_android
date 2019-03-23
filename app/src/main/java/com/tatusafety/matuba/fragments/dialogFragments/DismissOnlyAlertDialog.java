package com.tatusafety.matuba.fragments.dialogFragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tatusafety.matuba.R;

import java.util.Objects;

public class DismissOnlyAlertDialog extends Dialog {
    /**
     * Constructor
     *
     * @param context context
     */
    private DismissOnlyAlertDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * Method to display dialog when error is found
     */
    public static DismissOnlyAlertDialog showCustomDialog(Context context, Activity activity, String action, String title, String content) {

        final DismissOnlyAlertDialog mDialog = new DismissOnlyAlertDialog(context);

        // use custom background for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dismiss_only_custom_dialog);
        mDialog.setCanceledOnTouchOutside(false);

        // get title and content views
        TextView titleTextView = mDialog.findViewById(R.id.dialog_title);
        TextView contentTextView = mDialog.findViewById(R.id.dialog_content);
        Button mCancel = mDialog.findViewById(R.id.dialog_action_cancel);

        if (!TextUtils.isEmpty(content)) {
            titleTextView.setText(title);
            contentTextView.setText(content);
        }

        // dismiss dialog
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        if(!activity.isDestroyed() && !activity.isFinishing())
        mDialog.show();
        return mDialog;
    }
}
