package com.algoritm.terminal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.algoritm.terminal.ConnectTo1c.SOAP_Dispatcher;
import com.algoritm.terminal.ConnectTo1c.UIManager;
import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.R;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Password extends AppCompatActivity {
    private EditText login;
    private EditText password;
    private Button ok;

    private ArrayList<String> loginList = new ArrayList<>();
    private SharedPreferences preferences;

    public static String mLogin;
    public static String mPassword;

    public static final int ACTION_VERIFY = 10;
    public static final int ACTION_LOGIN_LIST = 11;

    public static final int ACTION_ConnectionError = 0;
    public static UIManager uiManager;
    public static SoapFault responseFault;

    public static SoapObject soapParam_Response;
    public static Handler soapHandler;
    public static String wsParam_PassHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        uiManager = new UIManager(this);
        soapHandler = new incomingHandler(this);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    verify();
                    return true;
                }
                return false;

            }
        });

        ok = findViewById(R.id.butt_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_LOGIN_LIST);
        dispatcher.start();

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);

        login.setText(preferences.getString("Login", ""));
    }

    private void verify() {
        login.setEnabled(false);
        password.setEnabled(false);
        ok.setEnabled(false);

        mLogin = login.getText().toString();
        mPassword = password.getText().toString();

        SOAP_Dispatcher dispatcher = new SOAP_Dispatcher(ACTION_VERIFY);
        dispatcher.start();

    }

    class incomingHandler extends Handler {
        private final WeakReference<Password> mTarget;

        public incomingHandler(Password context) {
            mTarget = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            login.setEnabled(true);
            password.setEnabled(true);
            ok.setEnabled(true);

            Password target = mTarget.get();

            switch (msg.what) {
                case ACTION_ConnectionError:
                    uiManager.showToast(getString(R.string.errorConnection) + getSoapErrorMessage());
                    break;
                case ACTION_VERIFY: {
                    target.checkLoginResult();

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Login", mLogin);
                    editor.putString("Password", mPassword);
                    editor.apply();
                }
                break;

                case ACTION_LOGIN_LIST: {
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

    public void checkLoginResult() {

        Boolean isLoginSuccess = Boolean.parseBoolean(soapParam_Response.getPropertyAsString("Result"));

        if (isLoginSuccess) {

            uiManager.showToast(getString(R.string.passwordIncorrect) + soapParam_Response.getPropertyAsString("Name"));

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            uiManager.showToast(getString(R.string.passwordNotIncorrect));
        }

    }

    public void checkLoginListResult() {
        try {

            loginList.clear();

            for (int i = 0; i < SharedData.USERS.size(); i++) {
                loginList.add(SharedData.USERS.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, loginList);
            AutoCompleteTextView acTextView = (AutoCompleteTextView) login;
            acTextView.setThreshold(1);
            acTextView.setAdapter(adapter);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}