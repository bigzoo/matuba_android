package com.tatusafety.matuba.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;

import java.util.List;

public class SpamActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_PICK_CONTACT = 100;
    private EditText mPhoneNumber, mMessage, mNumberOfMessages;
    private int sim2, sim1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);

        mPhoneNumber = findViewById(R.id.editTextNumber);
        mMessage = findViewById(R.id.editTextMessage);
        mNumberOfMessages = findViewById(R.id.editTextTimes);
        TextView sendMessage = findViewById(R.id.send_button);
        TextView pickFromContacts = findViewById(R.id.select_from_contacts);

        sendMessage.setOnClickListener(this);
        pickFromContacts.setOnClickListener(this);

    }

    private void getPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS)) {

            new AlertDialog.Builder(SpamActivity.this)
                    .setTitle(R.string.permission_dialog_title)

                    .setMessage(R.string.permission_message_dialog)

                    .setPositiveButton(R.string.give_permission_button_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(SpamActivity.this,
                                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},
                                    RESULT_PICK_CONTACT);
                        }
                    })
                    .create()
                    .show();
        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},
                    RESULT_PICK_CONTACT);
        }
    }


    public void pickContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be using multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicker(data);
                    break;
            }
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     *
     * @param data
     */
    private void contactPicker(Intent data) {

        Cursor cursor = null;

        try {
            String phoneNo;
            // getData() method will have the Content Uri of the selected contact

            Uri uri = data.getData();
            //Query the content uri

            if (uri != null) {
                cursor = getContentResolver().query(uri, null, null, null, null);
            }
            if (cursor != null) {
                cursor.moveToFirst();
            }

            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString(phoneIndex);

            // Set the value to the textviews
            mPhoneNumber.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        int numberOfMessages = 0;
        final String phoneNum = mPhoneNumber.getText().toString();
        final String message = mMessage.getText().toString();
        String time = mNumberOfMessages.getText().toString();

        if (!TextUtils.isEmpty(time)) {
            numberOfMessages = Integer.parseInt(time);
        }

        if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(message) && numberOfMessages > 0)
            for (int i = 0; i < numberOfMessages; i++) {
                try {
                    String sent = "SMS_SENT";
                    final PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0,
                            new Intent(sent), 0);

                    //---when the SMS has been sent---
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            if (getResultCode() == Activity.RESULT_OK) {
                                Toast.makeText(getBaseContext(), "SMS sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "SMS could not sent. Trying with sim 2",
                                        Toast.LENGTH_SHORT).show();
                                sendWithSim(sim2, phoneNum, message, sentIntent);
                            }
                        }
                    }, new IntentFilter(sent));

                    // if phone has 2 simcards, check then send with sim 1
                    // if not send anyway
                    getDefaultSim(phoneNum, message, sentIntent);

                } catch (Exception e) {
                    DismissOnlyAlertDialog.showCustomDialog(this,
                            SpamActivity.this, "dismiss",
                            getResources().getString(R.string.information_title),
                            e.getLocalizedMessage());
                }

                Toast.makeText(this, "Your message was sent successfully", Toast.LENGTH_SHORT).show();
            }
    }

    private void sendWithSim(int simCard, String phoneNum, String message, PendingIntent sentIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && simCard > 0) {
            SmsManager.getSmsManagerForSubscriptionId(simCard).sendTextMessage(phoneNum, null, message, sentIntent, null);
        }
    }

    private void getDefaultSim(String phoneNum, String message, PendingIntent sentIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                    List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                    SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                    SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                    sim2 = simInfo2.getSubscriptionId();
                    sim1 = simInfo1.getSubscriptionId();
                    sendWithSim(sim1, phoneNum, message, sentIntent);
                } else {
                    getPermissions();
                }
            } else {
                SmsManager.getDefault().sendTextMessage(phoneNum, null, message, sentIntent, null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                if (ContextCompat.checkSelfPermission(SpamActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    getPermissions();
                } else {
                    sendMessage();
                }
                break;
            case R.id.select_from_contacts:
                if (ContextCompat.checkSelfPermission(SpamActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    getPermissions();
                } else {
                    pickContact();
                }
                break;
        }
    }
}
