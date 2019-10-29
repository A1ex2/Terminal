package com.algoritm.terminal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.algoritm.terminal.ConnectTo1c.SOAP_Dispatcher;
import com.algoritm.terminal.ConnectTo1c.UIManager;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Reception> mReceptions = new ArrayList<>();

    public static final int ACTION_RECEPTION_LIST = 12;

    public static final int ACTION_ConnectionError = 0;
    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.listReception);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String login = preferences.getString("Login", "");
        String password = preferences.getString("Password", "");


        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_RECEPTION_LIST, login, password);
        dispatcher.start();

        ReceptionAdapter adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
        mListView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    class incomingHandler extends Handler {
        private final WeakReference<MainActivity> mTarget;

        public incomingHandler(MainActivity context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity target = mTarget.get();

            switch (msg.what) {
                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;

                case ACTION_RECEPTION_LIST: {
                    target.checkLoginListResult();
                }
                break;
            }
        }
    }

    private String getSoapErrorMessage() {

        String errorMessage;

        if (responseFault == null)
            errorMessage = getString(R.string.textNoInternet);
        else {
            try {
                errorMessage = responseFault.faultstring;
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = getString(R.string.unknownError);
            }
        }
        return errorMessage;
    }

    public void checkLoginListResult() {
        try {
            int count = soapParam_Response.getPropertyCount();

            for (int i = 0; i < count; i++) {
                SoapObject receptionList = (SoapObject) soapParam_Response.getProperty(i);

                Reception reception = new Reception();
                reception.setID(receptionList.getPropertyAsString("ID"));
                reception.setDescription(receptionList.getPropertyAsString("Description"));
                reception.setAutoNumber(receptionList.getPropertyAsString("AutoNumber"));
                reception.setDriver(receptionList.getPropertyAsString("Driver"));
                reception.setDriverPhone(receptionList.getPropertyAsString("DriverPhone"));
                reception.setInvoiceNumber(receptionList.getPropertyAsString("InvoiceNumber"));

                mReceptions.add(reception);
            }

            ReceptionAdapter adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
            mListView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
