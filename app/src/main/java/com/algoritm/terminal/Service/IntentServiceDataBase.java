package com.algoritm.terminal.Service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import com.algoritm.terminal.DataBase.DataBaseHelper;
import com.algoritm.terminal.Objects.Sector;

import java.util.ArrayList;

public class IntentServiceDataBase extends IntentService {

    private static final String EXTRA_PENDING_INTENT = "android.a1ex.com.sklad_tsd.extra.PENDING_INTENT";

    private static final String ACTION_ADD_SECTORS = "com.algoritm.terminal.Service.action.ADD_SECTORS";

    private static final String EXTRA_ADD_SECTORS = "com.algoritm.terminal.Service.extra.ADD_SECTORS";

    public IntentServiceDataBase() {
        super("IntentServiceDataBase");
    }

    public static void startInsertSectors(Context context, ArrayList<Sector> sectors) {
        Intent intent = new Intent(context, IntentServiceDataBase.class);
        intent.setAction(ACTION_ADD_SECTORS);
        intent.putParcelableArrayListExtra(EXTRA_ADD_SECTORS, sectors);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            DataBaseHelper helper = new DataBaseHelper(this);
            PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_PENDING_INTENT);
            Intent result = new Intent();


            if (ACTION_ADD_SECTORS.equals(action)) {
                ArrayList<Sector> sectors = intent.getParcelableArrayListExtra(EXTRA_ADD_SECTORS);
                helper.insertSectors(sectors);
                //result.putExtra(EXTRA_ID_DOCUMENT);
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }
}
