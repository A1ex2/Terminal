package com.algoritm.terminal.Service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import com.algoritm.terminal.DataBase.DataBaseHelper;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Sector;

import java.util.ArrayList;

public class IntentServiceDataBase extends IntentService {

    private static final String EXTRA_PENDING_INTENT = "android.a1ex.com.sklad_tsd.extra.PENDING_INTENT";

    private static final String ACTION_INSERT_CAR_DATA = "com.algoritm.terminal.Service.action.INSERT_CAR_DATA";

    private static final String EXTRA_INSERT_CAR_DATA = "com.algoritm.terminal.Service.extra.INSERT_CAR_DATA";

    public IntentServiceDataBase() {
        super("IntentServiceDataBase");
    }

    public static void startInsertCarData(Context context, CarData carData) {
        Intent intent = new Intent(context, IntentServiceDataBase.class);
        intent.setAction(ACTION_INSERT_CAR_DATA);
        intent.putExtra(EXTRA_INSERT_CAR_DATA, carData);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            DataBaseHelper helper = new DataBaseHelper(this);
            PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_PENDING_INTENT);
            Intent result = new Intent();

            if (ACTION_INSERT_CAR_DATA.equals(action)) {
                CarData carData = intent.getParcelableExtra(EXTRA_INSERT_CAR_DATA);
                helper.insertCarData(carData);
                //result.putExtra(EXTRA_ID_DOCUMENT);
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }
}
