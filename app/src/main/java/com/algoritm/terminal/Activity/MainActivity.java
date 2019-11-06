package com.algoritm.terminal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.ConnectTo1c.SOAP_Dispatcher;
import com.algoritm.terminal.ConnectTo1c.UIManager;
import com.algoritm.terminal.R;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Adapters.ReceptionAdapter;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Reception> mReceptions = SharedData.RECEPTION;
    private ProgressBar progressBar;
    private ReceptionAdapter adapter;

    public static final int ACTION_RECEPTION_LIST = 12;
    public static final int ACTION_SECTORS_LIST = 13;

    public static final int ACTION_ConnectionError = 0;
    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        SharedData.app = this;

        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.listReception);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String login = preferences.getString("Login", "");
        String password = preferences.getString("Password", "");

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_RECEPTION_LIST, login, password);
        dispatcher.start();

        SOAP_Dispatcher dispatcher2 = new SOAP_Dispatcher(ACTION_SECTORS_LIST);
        dispatcher2.start();

        adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reception reception = mReceptions.get(position);

                //uiManager.showToast(reception.toString());

                Intent intent = new Intent(view.getContext(), DetailReception.class);
                intent.putExtra("Reception", reception.getID());
                startActivity(intent);
            }
        });

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
                case ACTION_RECEPTION_LIST:
                    target.checkReceptionListResult();
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

    public void checkReceptionListResult() {
        try {
//            mReceptions.clear();
//            mReceptions.addAll(SharedData.RECEPTION);
//            mReceptions = SharedData.RECEPTION;

            adapter = new ReceptionAdapter(this, R.layout.item_reception, mReceptions);
            mListView.setAdapter(adapter);

            progressBar.setVisibility(ProgressBar.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
