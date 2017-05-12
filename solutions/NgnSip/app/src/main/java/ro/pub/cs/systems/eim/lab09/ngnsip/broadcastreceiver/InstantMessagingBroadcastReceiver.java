package ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnStringUtils;

import java.io.UnsupportedEncodingException;

import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class InstantMessagingBroadcastReceiver extends BroadcastReceiver {

    private TextView conversationTextView;

    public InstantMessagingBroadcastReceiver(TextView conversationTextView) {
        this.conversationTextView = conversationTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (NgnMessagingEventArgs.ACTION_MESSAGING_EVENT.equals(action)) {
            NgnMessagingEventArgs arguments = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
            if (arguments == null) {
                Log.e(Constants.TAG, "Invalid messaging event arguments");
                return;
            }

            switch (arguments.getEventType()) {
                case INCOMING:
                    if (!NgnStringUtils.equals(arguments.getContentType(), NgnContentType.T140COMMAND, true)) {
                        byte[] contentBytes = arguments.getPayload();
                        if (contentBytes != null && contentBytes.length > 0) {
                            try {
                                String content = new String(contentBytes, "UTF-8");
                                String conversation = conversationTextView.getText().toString();
                                conversationTextView.setText(conversation + "Others: " + content + "\n");
                            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                                Log.e(Constants.TAG, unsupportedEncodingException.toString());
                                if (Constants.DEBUG) {
                                    unsupportedEncodingException.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
