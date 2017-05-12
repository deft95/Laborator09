package ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

    private TextView registrationStatusTextView = null;

    public RegistrationBroadcastReceiver(TextView registrationStatusTextView) {
        this.registrationStatusTextView = registrationStatusTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {

            NgnRegistrationEventArgs arguments = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);

            if (arguments == null) {
                Log.d(Constants.TAG, "Invalid event arguments");
                return;
            }

            switch (arguments.getEventType()) {
                case REGISTRATION_NOK:
                    Toast.makeText(context, "Failed to register", Toast.LENGTH_SHORT).show();
                    registrationStatusTextView.setText("Failed to register");
                    Log.d(Constants.TAG, "Failed to register");
                    break;
                case REGISTRATION_OK:
                    registrationStatusTextView.setText("Registered: " + Constants.USERNAME + "@" + Constants.DOMAIN);
                    Log.d(Constants.TAG, "You are now registered");
                    break;
                case REGISTRATION_INPROGRESS:
                    registrationStatusTextView.setText("Registration in progress");
                    Log.d(Constants.TAG, "Trying to register...");
                    break;
                case UNREGISTRATION_NOK:
                    Toast.makeText(context, "Failed to unregister", Toast.LENGTH_SHORT).show();
                    registrationStatusTextView.setText("Failed to unregister");
                    Log.d(Constants.TAG, "Failed to unregister");
                    break;
                case UNREGISTRATION_OK:
                    registrationStatusTextView.setText("Unregistered");
                    Log.d(Constants.TAG, "You are now unregistered");
                    break;
                case UNREGISTRATION_INPROGRESS:
                    registrationStatusTextView.setText("Unregistration in progress");
                    Log.d(Constants.TAG, "Trying to unregister...");
                    break;
            }

        }
    }

}
