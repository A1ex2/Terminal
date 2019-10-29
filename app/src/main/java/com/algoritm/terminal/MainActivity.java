package com.algoritm.terminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.algoritm.terminal.ConnectTo1c.SOAP_Dispatcher;
import com.algoritm.terminal.ConnectTo1c.UIManager;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Reception> mReceptions = new ArrayList<>();
    private ProgressBar progressBar;
    private ReceptionAdapter adapter;

    public static final int ACTION_RECEPTION_LIST = 12;

    public static final int ACTION_ConnectionError = 0;
    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.listReception);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String login = preferences.getString("Login", "");
        String password = preferences.getString("Password", "");


        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_RECEPTION_LIST, login, password);
        dispatcher.start();

        adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
        mListView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
//                adapter.notifyDataSetChanged();
                mListView.invalidateViews();
                return false;
            }
        });

        return true;
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

            adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
            //adapter.notifyDataSetChanged();
            mListView.setAdapter(adapter);

            progressBar.setVisibility(ProgressBar.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
