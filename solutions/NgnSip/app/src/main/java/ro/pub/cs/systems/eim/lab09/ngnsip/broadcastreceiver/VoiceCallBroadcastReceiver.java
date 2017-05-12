package ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;

import ro.pub.cs.systems.eim.lab09.ngnsip.R;
import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class VoiceCallBroadcastReceiver extends BroadcastReceiver {

    private EditText SIPAddressEditText = null;
    private TextView callStatusTextView = null;

    public VoiceCallBroadcastReceiver(EditText SIPAddressEditText, TextView callStatusTextView) {
        this.SIPAddressEditText = SIPAddressEditText;
        this.callStatusTextView = callStatusTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
            NgnInviteEventArgs arguments = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
            if (arguments == null) {
                Log.e(Constants.TAG, "Invalid event arguments");
                return;
            }

            final NgnAVSession ngnAVSession = NgnAVSession.getSession(arguments.getSessionId());
            if (ngnAVSession == null) {
                Log.e(Constants.TAG, "NgnAVSession could not be fetched for this session");
                return;
            }

            NgnInviteSession.InviteState inviteState = ngnAVSession.getState();
            NgnEngine ngnEngine = NgnEngine.getInstance();

            switch(inviteState) {
                case NONE:
                default:
                    Log.i(Constants.TAG, "Call state: " + inviteState);
                    break;
                case INCOMING:
                    Log.i(Constants.TAG, "Incoming call");
                    ngnEngine.getSoundService().startRingTone();
                    SIPAddressEditText.setText(ngnAVSession.getRemotePartyUri());
                    callStatusTextView.setText(context.getResources().getString(R.string.incoming_call));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ngnAVSession.acceptCall();
                        }
                    }, Constants.ACCEPT_CALL_DELAY_TIME);
                    break;
                case INCALL:
                    Log.i(Constants.TAG, "Call started");
                    callStatusTextView.setText(context.getResources().getString(R.string.in_call));
                    Toast.makeText(context, "Call connected", Toast.LENGTH_SHORT).show();
                    ngnEngine.getSoundService().stopRingTone();
                    break;
                case TERMINATED:
                case TERMINATING:
                    Log.i(Constants.TAG, "Call ended");
                    callStatusTextView.setText(context.getResources().getString(R.string.no_call));
                    Toast.makeText(context, "Call disconnected", Toast.LENGTH_SHORT).show();
                    ngnEngine.getSoundService().stopRingTone();
                    ngnEngine.getSoundService().stopRingBackTone();
                    break;
            }
        }

    }

}
