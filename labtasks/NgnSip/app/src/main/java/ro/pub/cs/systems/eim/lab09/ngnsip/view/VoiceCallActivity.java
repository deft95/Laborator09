package ro.pub.cs.systems.eim.lab09.ngnsip.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

import ro.pub.cs.systems.eim.lab09.ngnsip.R;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.RegistrationBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.VoiceCallBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class VoiceCallActivity extends AppCompatActivity {

    private NgnEngine ngnEngine = null;
    private INgnSipService ngnSipService = null;

    private NgnAVSession ngnAVSession = null;

    private IntentFilter registrationIntentFilter;
    private RegistrationBroadcastReceiver registrationBroadcastReceiver;

    private IntentFilter voiceCallIntentFilter;
    private VoiceCallBroadcastReceiver voiceCallBroadcastReceiver;

    private Button registerButton = null;
    private Button unregisterButton = null;
    private TextView registrationStatusTextView = null;

    private EditText SIPAddressEditText = null;
    private Button makeCallButton = null;
    private Button hangUpCallButton = null;
    private TextView callStatusTextView = null;

    private Button dtmfButton = null;
    private EditText dtmfEditText = null;

    private Button chatButton = null;

    private static VoiceCallActivity instance;

    public static VoiceCallActivity getInstance() {
        return instance;
    }

    private RegistrationButtonClickListener registrationButtonClickListener = new RegistrationButtonClickListener();
    private class RegistrationButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO exercise 5a
            // - set the NGN engine parameters via the configureStack() method
            // - start the NGN engine and register the activity to the SIP service
            // invoke the startNgnEngine() and registerSipService() methods respectively
        }

    }

    private UnregisterButtonClickListener unregisterButtonClickListener = new UnregisterButtonClickListener();
    private class UnregisterButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO exercise 5b
            // unregister the SIP service by invoking the unregisterSipService() method
        }

    }

    private MakeCallButtonClickListener makeCallButtonClickListener = new MakeCallButtonClickListener();
    private class MakeCallButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String validUri = NgnUriUtils.makeValidSipUri(SIPAddressEditText.getText().toString());
            if (validUri == null) {
                Log.e(Constants.TAG, "Invalid SIP Address");
                return;
            }
            if (!ngnEngine.isStarted() || !ngnSipService.isRegistered()) {
                Log.e(Constants.TAG, "NGN Engine is not started or NGN Sip Service is not registered");
                return;
            }

            // TODO exercise 7a
            // - create a NgnAVSession by invoking the static method createOutgoingSession
            // passing as arguments the SipStack and the media type (NgnMediaType.Audio)
            // - if the call can be made, set the callStatusTextView to "calling" and log the information
            // - if the call cannot be made, log the information accordingly
            // hint: use the makeCall() method of the NgnAVSession instance

        }
    }

    private HangupCallButtonClickListener hangupCallButtonClickListener = new HangupCallButtonClickListener();
    private class HangupCallButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View viw) {

            // TODO exercise 7b
            // this method should check whether the NgnAVSession was previously created
            // hint: use the hangUpCall() method of the NgnAVSession instance

        }

    }

    private DTMFButtonClickListener dtmfButtonClickListener = new DTMFButtonClickListener();
    private class DTMFButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (ngnAVSession != null) {

                // TODO exercise 10
                // - get the character from the DTMF edit text
                // - compute the character code (0-9 for digits, 10 for '*', 11 for '#')
                // - use the sendDTMF() method of the NgnAVSession instance
                // - log the result using Logcat

            }
        }

    }

    private ChatButtonClickListener chatButtonClickListener = new ChatButtonClickListener();
    private class ChatButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), InstantMessagingActivity.class);
            intent.putExtra(Constants.SIP_ADDRESS, SIPAddressEditText.getText().toString());
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "onCreate() callback method was invoked");
        setContentView(R.layout.activity_voice_call);
        instance = this;

        ngnEngine = NgnEngine.getInstance();
        if (ngnEngine == null) {
            Log.e(Constants.TAG, "Failed to obtain the NGN engine");
        }
        ngnSipService = ngnEngine.getSipService();

        registerButton = (Button)findViewById(R.id.register_button);
        registerButton.setOnClickListener(registrationButtonClickListener);
        unregisterButton = (Button)findViewById(R.id.unregister_button);
        unregisterButton.setOnClickListener(unregisterButtonClickListener);
        registrationStatusTextView = (TextView)findViewById(R.id.registration_status_text_view);

        SIPAddressEditText = (EditText)findViewById(R.id.SIP_address_edit_text);
        makeCallButton = (Button)findViewById(R.id.make_call_button);
        makeCallButton.setOnClickListener(makeCallButtonClickListener);
        hangUpCallButton = (Button)findViewById(R.id.hang_up_call_button);
        hangUpCallButton.setOnClickListener(hangupCallButtonClickListener);
        callStatusTextView = (TextView)findViewById(R.id.call_status_text_view);

        dtmfButton = (Button)findViewById(R.id.dtmf_button);
        dtmfButton.setOnClickListener(dtmfButtonClickListener);
        dtmfEditText = (EditText)findViewById(R.id.dtmf_edit_text);

        chatButton = (Button)findViewById(R.id.chat_button);
        chatButton.setOnClickListener(chatButtonClickListener);

        enableRegistrationBroadcastReceiver();
        enableVoiceCallBroadcastReceiver();
    }

    public void configureStack() {
        NgnEngine ngnEngine = NgnEngine.getInstance();
        INgnConfigurationService ngnConfigurationService = ngnEngine.getConfigurationService();

        ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, Constants.IDENTITY_IMPI);
        ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", Constants.USERNAME, Constants.DOMAIN));
        ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, Constants.IDENTITY_PASSWORD);
        ngnConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, Constants.NETWORK_PCSCF_HOST);
        ngnConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, Constants.NETWORK_PCSCF_PORT);
        ngnConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, Constants.NETWORK_REALM);

        ngnConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, Constants.NETWORK_USE_3G);
        ngnConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, Constants.NETWORK_REGISTRATION_TIMEOUT);

        ngnConfigurationService.commit();
    }

    public boolean startNgnEngine() {
        if (!ngnEngine.isStarted()) {
            if (!ngnEngine.start()) {
                Log.e(Constants.TAG, "Failed to start the NGN engine");
                return false;
            }
        }
        return true;
    }

    public boolean stopNgnEngine() {
        if (ngnEngine.isStarted()) {
            if (!ngnEngine.stop()) {
                Log.e(Constants.TAG, "Failed to stop the NGN engine");
                return false;
            }
        }
        return true;
    }

    public void registerSipService() {
        if (!ngnSipService.isRegistered()) {
            ngnSipService.register(this);
        }
    }

    public void unregisterSipService() {
        if (ngnSipService.isRegistered()) {
            ngnSipService.unRegister();
        }
    }

    public void enableRegistrationBroadcastReceiver() {

        // TODO exercise 6a
        // - create a RegistrationBroadcastReceiver instance
        // - create an IntentFilter instance for NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT action
        // - register the broadcast intent with the intent filter

    }

    public void disableRegistrationStateBroadcastReceiver() {

        // TODO exercise 6b
        // unregister the RegistrationBroadcastReceiver instance

    }

    public void enableVoiceCallBroadcastReceiver() {

        // TODO exercise 8a
        // - create a VoiceCallBroadcastReceiver instance
        // - create an IntentFilter instance for NgnInviteEventArgs.ACTION_INVITE_EVENT action
        // - register the broadcast receiver with the intent filter

    }

    public void disableVoiceCallBroadcastReceiver() {

        // TODO exercise 8b
        // unregister the VoiceCallBroadcastReceiver instance

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume() callback method was invoked");
    }

    @Override
    protected void onPause() {
        Log.i(Constants.TAG, "onPause() callback method was invoked");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "onDestroy() callback method was invoked");
        stopNgnEngine();
        disableRegistrationStateBroadcastReceiver();
        disableVoiceCallBroadcastReceiver();
        super.onDestroy();
    }

    public INgnSipService getNgnSipService() {
        return ngnSipService;
    }
}
