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
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SpamActivity extends _BaseActivity implements View.OnClickListener {
    private static final int RESULT_PICK_CONTACT = 100;
    private final String TAG = getClass().getSimpleName();
    EditText mMessageEt;
    EditText mPhoneNumberEt;
    EditText mNumberOfMessagesEt;
    TextView pickFromContacts;
    TextView sendMessageTv;
    TextView sim1Tv;
    TextView sim2Tv;
    TextView mSendInIntervalsBtn;
    EditText mIntervalsEt;
    private Timer spammingTimer;
    private boolean sendWithSim1 = true;
    private boolean spamming;
    private int messagesSentCount, mMessageIntevals, number;
    private boolean sendingIntervals;
    private String mPhoneNumber, mMessage;
    private String noOfMessagesString;
    private String sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);
        setupToolBar(true, "Spam");

        sendMessageTv = findViewById(R.id.send_button);
        mSendInIntervalsBtn = findViewById(R.id.send_interval);
        pickFromContacts = findViewById(R.id.select_from_contacts);
        sim1Tv = findViewById(R.id.sim_1);
        sim2Tv = findViewById(R.id.sim_2);
        mMessageEt = findViewById(R.id.messageEt);
        mPhoneNumberEt = findViewById(R.id.phoneNumberEt);
        mNumberOfMessagesEt = findViewById(R.id.messageCountEt);
        mIntervalsEt = findViewById(R.id.messageIntervalEt);

        sendMessageTv.setOnClickListener(this);
        pickFromContacts.setOnClickListener(this);
        mSendInIntervalsBtn.setOnClickListener(this);
        sim1Tv.setOnClickListener(this);
        sim2Tv.setOnClickListener(this);
        getDefaultSim(null, false);

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
            mPhoneNumberEt.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles sending the message
     */
    public void sendMessage() {
        int numberOfMessages = 0;
        mPhoneNumber = mPhoneNumberEt.getText().toString();
        mMessage = mMessageEt.getText().toString();
        noOfMessagesString = mNumberOfMessagesEt.getText().toString();

        if (!TextUtils.isEmpty(noOfMessagesString)) {

            // get the number of messages
            numberOfMessages = Integer.parseInt(noOfMessagesString);
        }

        // check if the phone number and message box and number of messages field are not empty
        if (!TextUtils.isEmpty(mPhoneNumber) && !TextUtils.isEmpty(mMessage) && numberOfMessages > 0)
            // init a for loop to send the messages
            sent = "SMS_SENT";
        final PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(sent), 0);
        if (!sendingIntervals) {
            for (int i = 0; i < numberOfMessages; i++) {
                try {
                    //---when the SMS has been sent---
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            if (getResultCode() == Activity.RESULT_OK) {
                                Toast.makeText(getBaseContext(), "Your message was sent successfully",
                                        Toast.LENGTH_SHORT).show();
                                mIntervalsEt.setEnabled(true);
                            } else {
                                Toast.makeText(getBaseContext(), "SMS could not sent",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new IntentFilter(sent));

                    // if phone has 2 sim-cards, check then send with sim 1
                    // if not send anyway
                    getDefaultSim(sentIntent, true);

                } catch (Exception e) {
                    DismissOnlyAlertDialog.showCustomDialog(this,
                            SpamActivity.this, "dismiss",
                            getResources().getString(R.string.information_title),
                            e.getLocalizedMessage());
                }

            }
        } else {
            if (number > 0)
                sendWithIntervals(sentIntent);
        }
    }

    private void sendWithSim(int simCard, PendingIntent sentIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && simCard > 0 && !TextUtils.isEmpty(mPhoneNumber) && !TextUtils.isEmpty(mMessage)) {
            SmsManager.getSmsManagerForSubscriptionId(simCard).sendTextMessage(mPhoneNumber, null, mMessage, sentIntent, null);
        }
    }

    private void getDefaultSim(PendingIntent sentIntent, boolean sendNow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.e(TAG, "getting default sim");
            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                    // User has a dual sim phone
                    // show options for sending
                    sim2Tv.setVisibility(View.VISIBLE);
                    sim1Tv.setVisibility(View.VISIBLE);

                    List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                    SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                    SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                    int sim2 = simInfo2.getSubscriptionId();
                    int sim1 = simInfo1.getSubscriptionId();
                    sim1Tv.setText(simInfo1.getDisplayName());
                    sim2Tv.setText(simInfo2.getDisplayName());

                    // If the user picked sim1 send with sim 1
                    if (sendWithSim1) {
                        sendWithSim(sim1, sentIntent);
                    } else {
                        sendWithSim(sim2, sentIntent);
                    }
                } else {
                    // permissions were not granted
                    getPermissions();
                }
            } else {
                // user does not have a dual sim . Send the message
                if (sendNow)
                    SmsManager.getDefault().sendTextMessage(mPhoneNumber, null, mMessage, sentIntent, null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                if (ContextCompat.checkSelfPermission(SpamActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // permissions were not granted
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
            case R.id.sim_1:
                // if user selects sim 1 then send message with sim 1
                sendWithSim1 = true;
                sim1Tv.setBackgroundColor(getResources().getColor(R.color.orange));
                sim2Tv.setBackgroundColor(getResources().getColor(R.color.lighBlue));
                break;
            case R.id.sim_2:
                sendWithSim1 = false;
                sim2Tv.setBackgroundColor(getResources().getColor(R.color.orange));
                sim1Tv.setBackgroundColor(getResources().getColor(R.color.lighBlue));
                break;
            case R.id.send_interval:
                if (!TextUtils.isEmpty(mIntervalsEt.getText().toString())) {
                    number = Integer.parseInt(mNumberOfMessagesEt.getText().toString());
                }
                sendMessageTv.setEnabled(false);
                sendingIntervals = true;
                sendMessage();
        }
    }

    private void sendWithIntervals(final PendingIntent pendingIntent) {
        if (spammingTimer == null && !spamming) {
            messagesSentCount = 0;
            // get the interval
            if (!TextUtils.isEmpty(mIntervalsEt.getText().toString())) {
                mMessageIntevals = Integer.parseInt(mIntervalsEt.getText().toString());
            } else {
                mIntervalsEt.setError("Please fill this field");
            }

            spamming = true;
            spammingTimer = new Timer();
            spammingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG, "doing runnable");
                    getDefaultSim(pendingIntent, true);
                    messagesSentCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageTv.setText(getString(R.string.message_sent_count, messagesSentCount));

                            // all messages have been sent, cancel timer
                            if (messagesSentCount == number) {
                                if (spammingTimer != null && spamming) {
                                    spammingTimer.cancel();
                                    spammingTimer = null;
                                    spamming = false;
                                }
                            }
                        }
                    });

                }
            }, 300, mMessageIntevals);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * onOptionsItemSelected method
     *
     * @param item item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
