package ro.pub.cs.systems.eim.lab09.ngnsip.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.events.NgnMessagingEventArgs;

import ro.pub.cs.systems.eim.lab09.ngnsip.R;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.InstantMessagingBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class InstantMessagingActivity extends AppCompatActivity {

    private IntentFilter instantMessagingIntentFilter;
    private InstantMessagingBroadcastReceiver instantMessagingBroadcastReceiver;

    private String SIPAddress = null;

    private EditText messageEditText = null;
    private Button sendButton = null;
    private TextView conversationTextView = null;

    private SendButtonClickListener sendButtonClickListener = new SendButtonClickListener();
    private class SendButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (VoiceCallActivity.getInstance().getNgnSipService() != null) {
                String remotePartyUri = SIPAddress;

                // TODO exercise 11a
                // - create an NgnMessagingSession instance for each message being transmitted
                // passing as arguments the SIP stack and the URI of the remote party
                // hint: use the static method createOutgoingSession() in the NgnMessagingSession class
                // - send the message from the messageEditText using the sendTextMessage() method
                // of the NgnMessagingSession instance and display it
                // in the graphic user interface (conversationTextView)
                // !!! do not forget to release the session using the static method releaseSession()
                // in the NgnMessagingSession class

            } else {
                Toast.makeText(InstantMessagingActivity.this, "The SIP Service instance is null", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "onCreate() callback method was invoked");
        setContentView(R.layout.activity_instant_messaging);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras().containsKey(Constants.SIP_ADDRESS)) {
            SIPAddress = intent.getStringExtra(Constants.SIP_ADDRESS);
        }

        messageEditText = (EditText)findViewById(R.id.message_edit_text);

        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(sendButtonClickListener);

        conversationTextView = (TextView)findViewById(R.id.conversation_text_view);
        conversationTextView.setMovementMethod(new ScrollingMovementMethod());

        enableInstantMessagingBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "onDestroy() callback method was invoked");
        super.onDestroy();
    }

    public void enableInstantMessagingBroadcastReceiver() {
        instantMessagingBroadcastReceiver = new InstantMessagingBroadcastReceiver(conversationTextView);
        instantMessagingIntentFilter = new IntentFilter();
        instantMessagingIntentFilter.addAction(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
        registerReceiver(instantMessagingBroadcastReceiver, instantMessagingIntentFilter);
    }

    public void disableInstantMessagingBroadcastReceiver() {
        if (instantMessagingBroadcastReceiver != null) {
            unregisterReceiver(instantMessagingBroadcastReceiver);
            instantMessagingBroadcastReceiver = null;
        }
    }

}
