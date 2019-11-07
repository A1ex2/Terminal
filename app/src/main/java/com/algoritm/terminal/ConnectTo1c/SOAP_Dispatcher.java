package com.algoritm.terminal.ConnectTo1c;

import com.algoritm.terminal.Activity.DetailReception;
import com.algoritm.terminal.Activity.MainActivity;
import com.algoritm.terminal.Activity.Password;
import com.algoritm.terminal.DataBase.DataBaseHelper;
import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Objects.Sector;
import com.algoritm.terminal.Objects.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.util.ArrayList;


public class SOAP_Dispatcher extends Thread {

    public static final Integer soapParam_timeout = 100;
    public static String soapParam_pass = "31415926";
    public static String soapParam_user = "Администратор";
//    public static String soapParam_URL = "http://gate.algoritm.org.ua:8091/blg_log_test/ws/terminal.1cws";
    public static String soapParam_URL = "http://192.168.1.4:8090/blg_log/ws/terminal.1cws";
    public SoapObject soap_Inquiry;

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

            case MainActivity.ACTION_SECTORS_LIST:
                getSectors();
                break;

            case DetailReception.ACTION_SET_RECEPTION:
                setCB();
                break;
        }

        if (ACTION == Password.ACTION_VERIFY | ACTION == Password.ACTION_LOGIN_LIST) {
            if (soap_Response != null) {
                Password.soapParam_Response = soap_Response;
                Password.soapHandler.sendEmptyMessage(ACTION);
            } else {
                Password.soapHandler.sendEmptyMessage(Password.ACTION_ConnectionError);
            }
        } else if (ACTION == MainActivity.ACTION_RECEPTION_LIST
                | ACTION == MainActivity.ACTION_SECTORS_LIST) {
            if (soap_Response != null) {
                MainActivity.soapParam_Response = soap_Response;
                MainActivity.soapHandler.sendEmptyMessage(ACTION);
            } else {
                MainActivity.soapHandler.sendEmptyMessage(MainActivity.ACTION_ConnectionError);
            }
        }
    }

    private void setCB() {
        String method = "setReception";
        String action = NAMESPACE + "#setReception:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("Reception", soap_Inquiry);
        soap_Response = callWebService(request, action);
    }

    void getReceptionList() {

        String method = "GetReceptionList";
        String action = NAMESPACE + "#returnReceptionList:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

        int count = soap_Response.getPropertyCount();
        ArrayList<Reception> mReceptions = SharedData.RECEPTION;
        mReceptions.clear();

        for (int i = 0; i < count; i++) {
            SoapObject receptionList = (SoapObject) soap_Response.getProperty(i);

            Reception reception = new Reception();
            reception.setID(receptionList.getPropertyAsString("ID"));
            reception.setDescription(receptionList.getPropertyAsString("Description"));
            reception.setAutoNumber(receptionList.getPropertyAsString("AutoNumber"));
            reception.setDriver(receptionList.getPropertyAsString("Driver"));
            reception.setDriverPhone(receptionList.getPropertyAsString("DriverPhone"));
            reception.setInvoiceNumber(receptionList.getPropertyAsString("InvoiceNumber"));

            ArrayList<CarData> carDataList = new ArrayList<>();

            for (int j = 0; j < receptionList.getPropertyCount(); j++) {
                PropertyInfo pi = new PropertyInfo();
                receptionList.getPropertyInfo(j, pi);
                Object property = receptionList.getProperty(j);
                if (pi.name.equals("CarData") && property instanceof SoapObject) {
                    SoapObject carDetail = (SoapObject) property;

                    CarData carData = new CarData();
                    carData.setReceptionID(reception.getID());
                    carData.setCarID(carDetail.getPrimitivePropertyAsString("CarID"));
                    carData.setCar(carDetail.getPrimitivePropertyAsString("Car"));
                    carData.setBarCode(carDetail.getPrimitivePropertyAsString("BarCode"));
                    carData.setSectorID(carDetail.getPrimitivePropertyAsString("SectorID"));
                    carData.setSector(carDetail.getPrimitivePropertyAsString("Sector"));
                    carData.setRow(carDetail.getPrimitivePropertyAsString("Row"));
                    try {
                        carData.setProductionDate(carDetail.getPrimitivePropertyAsString("ProductionDate"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    carDataList.add(carData);
                }
            }
            reception.setCarData(carDataList);

            mReceptions.add(reception);
        }

        SharedData.updateReceptionsDB();
    }

    void getLoginList() {

        String method = "GetLoginList";
        String action = NAMESPACE + "#returnLoginList:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

        int count = soap_Response.getPropertyCount();
        ArrayList<User> users = SharedData.USERS;
        users.clear();

        for (int i = 0; i < count; i++) {
            SoapObject login = (SoapObject) soap_Response.getProperty(i);

            User user = new User();
            user.setName(login.getPropertyAsString("Description"));
            users.add(user);
        }
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

    void getSectors() {

        String method = "GetSectorList";
        String action = NAMESPACE + "#returnSectors:" + method;
        SoapObject request = new SoapObject(NAMESPACE, method);
        soap_Response = callWebService(request, action);

        int count = soap_Response.getPropertyCount();
        ArrayList<Sector> sectors = SharedData.SECTORS;
        sectors.clear();

        Sector mSector = new Sector();
        mSector.setID("0");
        mSector.setName("");
        sectors.add(mSector);

        for (int i = 0; i < count; i++) {
            SoapObject sectorList = (SoapObject) soap_Response.getProperty(i);

            Sector sector = new Sector();
            sector.setID(sectorList.getPrimitivePropertyAsString("ID"));
            sector.setName(sectorList.getPrimitivePropertyAsString("Name"));

            sectors.add(sector);
        }
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

