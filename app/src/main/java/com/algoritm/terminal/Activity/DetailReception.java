package com.algoritm.terminal.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.algoritm.terminal.ConnectTo1c.SOAP_Dispatcher;
import com.algoritm.terminal.ConnectTo1c.SOAP_Objects;
import com.algoritm.terminal.ConnectTo1c.UIManager;
import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.R;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Adapters.RecyclerAdapterCarData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;

public class DetailReception extends AppCompatActivity {
    private Reception reception;
    private TextView description;
    private TextView driver;
    private TextView autoNumber;
    private RecyclerView recyclerView;
    private RecyclerAdapterCarData adapter;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_SCAN = 0x0000c0de;

    public static final int ACTION_SET_RECEPTION = 14;
    public static final int ACTION_ConnectionError = 0;

    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        String id = getIntent().getStringExtra("Reception");
        reception = SharedData.getReception(id);

        description = findViewById(R.id.description);
        description.setText(reception.getDescription());

        driver = findViewById(R.id.driver);
        driver.setText(reception.getDriver());

        autoNumber = findViewById(R.id.autoNumber);
        autoNumber.setText(reception.getAutoNumber());

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapterCarData(this, R.layout.item_activity_detail, reception.getCarData());
        recyclerView.setAdapter(adapter);
        adapter.setActionListener(new RecyclerAdapterCarData.ActionListener() {
            @Override
            public void onClick(CarData carData) {
                viewCarData(carData);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reception, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchBarCode:
                scanBarCode();
                break;

            case R.id.saveCB:
                setCB();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCB() {

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String login = preferences.getString("Login", "");
        String password = preferences.getString("Password", "");

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_SET_RECEPTION, login, password);
        String stringReception = SOAP_Objects.getReception(reception);
        dispatcher.string_Inquiry = stringReception;

        dispatcher.start();

    }

    private void scanBarCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(ScannerActivity.class);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt(getString(R.string.camera_to_the_barcode));
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLists();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SCAN) {
            IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (Result != null) {
                if (Result.getContents() == null) {
//                    Log.d("MainActivity", "cancelled scan");
//                    Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    String barCode = Result.getContents();
                    CarData carData = getCarDataByBarCode(barCode);
                    if (carData.getCarID() == null) {
                        String message = new Formatter().format(getString(R.string.no_car_by_barcode), barCode).toString();

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.cancelled_scan)).
                                setMessage(message).
                                setCancelable(false).
                                setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).
                                setPositiveButton(getString(R.string.scan_more), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        scanBarCode();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        viewCarData(carData);
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        //super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                //updateLists();
//            }
//        }

    }

    private CarData getCarDataByBarCode(String barCode) {
        ArrayList<CarData> carDataArrayList = reception.getCarData();
        CarData carData = new CarData();
        for (int i = 0; i < carDataArrayList.size(); i++) {
            CarData mCarData = carDataArrayList.get(i);
            if (barCode.equals(mCarData.getBarCode())) {
                carData = mCarData;
                break;
            }
        }

        return carData;
    }

    private void viewCarData(CarData carData) {
        Intent intent = new Intent(DetailReception.this, CarActivity.class);
        intent.putExtra("CarData", carData);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void updateLists() {
        if (adapter == null) {
            adapter = new RecyclerAdapterCarData(this, R.layout.item_activity_detail, reception.getCarData());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    class incomingHandler extends Handler {
        private final WeakReference<DetailReception> mTarget;

        public incomingHandler(DetailReception context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailReception target = mTarget.get();

            switch (msg.what) {
                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;
                case ACTION_SET_RECEPTION: {
                    target.checkSetReception();
                }
                break;
            }
        }
    }

    private void checkSetReception() {

        Boolean isSaveSuccess = Boolean.parseBoolean(soapParam_Response.getPropertyAsString("Result"));

        if (isSaveSuccess) {

            uiManager.showToast(getString(R.string.success));

            setResult(MainActivity.REQUEST_CODE_UPDATE_RECEPTION);
            finish();

        } else {
            uiManager.showToast(soapParam_Response.getPropertyAsString("Description"));
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

}
