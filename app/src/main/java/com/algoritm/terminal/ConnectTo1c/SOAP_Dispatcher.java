package com.algoritm.terminal.ConnectTo1c;

import android.util.Log;

import com.algoritm.terminal.MainActivity;
import com.algoritm.terminal.Password;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class SOAP_Dispatcher extends Thread {

    public static final Integer soapParam_timeout = 100;
    public static String soapParam_pass = "31415926";
    public static String soapParam_user = "Администратор";
    public static String soapParam_URL = "http://192.168.1.4:8090/blg_log/ws/terminal.1cws";

    int timeout;
    String URL;
    String user;
    String pass;
    int ACTION;
    SoapObject soap_Response;
    final String NAMESPACE = "www.URI.com";//"ReturnPhones_XDTO";
    String mSoapParam_URL;

    public SOAP_Dispatcher(int SOAP_ACTION, String sParam_user, String sParam_pass) {
        timeout = soapParam_timeout;
        URL = soapParam_URL;
        user = sParam_user;
        pass = sParam_pass;
        ACTION = SOAP_ACTION;
        mSoapParam_URL = soapParam_URL;
    }

    public SOAP_Dispatcher(int SOAP_ACTION) {
        timeout = soapParam_timeout;
        URL = soapParam_URL;
        user = soapParam_user;
        pass = soapParam_pass;
        ACTION = SOAP_ACTION;
        mSoapParam_URL = soapParam_URL;
    }

    @Override
    public void run() {

        switch (ACTION) {
            case Password.ACTION_VERIFY:
                login();
                break;
            case Password.ACTION_LOGIN_LIST:
                getLoginList();
                break;
            case MainActivity.ACTION_RECEPTION_LIST:
                getReceptionList();
                break;
        }

        if (ACTION == Password.ACTION_VERIFY | ACTION == Password.ACTION_LOGIN_LIST) {
            if (soap_Response != null) {
                Password.soapParam_Response = soap_Response;
                Password.soapHandler.sendEmptyMessage(ACTION);
            } else {
                Password.soapHandler.sendEmptyMessage(Password.ACTION_ConnectionError);
            }
        } else if (ACTION == MainActivity.ACTION_RECEPTION_LIST) {
            if (soap_Response != null) {
                MainActivity.soapParam_Response = soap_Response;
                MainActivity.soapHandler.sendEmptyMessage(ACTION);
            } else {
                MainActivity.soapHandler.sendEmptyMessage(MainActivity.ACTION_ConnectionError);
            }
        }
    }

    void getReceptionList() {

        String method = "GetReceptionList";
        String action = NAMESPACE + "#returnReceptionList:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);
    }

    void getLoginList() {

        String method = "GetLoginList";
        String action = NAMESPACE + "#returnLoginList:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);
    }

    void login() {

        String method = "Login";
        String action = NAMESPACE + "#Login:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);

        request.addProperty("Login", Password.mLogin);

        String wsParam_PassHash = AeSimpleSHA1.getPassHash(Password.mPassword);
        request.addProperty("Password", wsParam_PassHash);
        soap_Response = callWebService(request, action);
    }

    void getCells() {

//        String method = "GetCellList";
//        String action = NAMESPACE + "#returnCells:" + method;
//        SoapObject request = new SoapObject(NAMESPACE, method);
//        soap_Response = callWebService(request, action);
    }

    void getProducts() {

//        String method = "GetProductList";
//        String action = NAMESPACE + "#returnProducts:" + method;
//        SoapObject request = new SoapObject(NAMESPACE, method);
//        soap_Response = callWebService(request, action);
    }

    private SoapObject callWebService(SoapObject request, String action) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        HttpTransportSE androidHttpTransport = new HttpTransportBasicAuthSE(URL, user, pass);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(action, envelope);
            return (SoapObject) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

