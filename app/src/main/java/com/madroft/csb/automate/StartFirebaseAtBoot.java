package com.madroft.csb.automate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chan on 07-01-2017.
 */

public class StartFirebaseAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(FirebaseBackgroundService.class.getName()));
    }
}
