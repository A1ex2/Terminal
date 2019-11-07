package com.algoritm.terminal.ConnectTo1c;

import com.algoritm.terminal.Activity.Password;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Reception;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class SOAP_Objects {

    public static SoapObject getReception(Reception reception) {

        SoapObject soapObject = new SoapObject("Reception", "Reception");
        soapObject.addProperty("ID", reception.getID());
        soapObject.addProperty("Description", reception.getDescription());
        soapObject.addProperty("AutoNumber", reception.getAutoNumber());
        soapObject.addProperty("Driver", reception.getDriver());
        soapObject.addProperty("DriverPhone", reception.getDriverPhone());
        soapObject.addProperty("InvoiceNumber", reception.getInvoiceNumber());

        ArrayList<CarData> carDataArrayList = reception.getCarData();
        for (int i = 0; i < carDataArrayList.size(); i++) {
            CarData carData = carDataArrayList.get(i);

            SoapObject soapCarData = new SoapObject("CarData", "CarData");
            soapCarData.addProperty("CarID", carData.getCarID());
            soapCarData.addProperty("Car", carData.getCar());
            soapCarData.addProperty("BarCode", carData.getBarCode());
            soapCarData.addProperty("SectorID", carData.getSectorID());
            soapCarData.addProperty("Sector", carData.getSector());
            soapCarData.addProperty("Row", carData.getRow());
            soapCarData.addProperty("ProductionDate", carData.getProductionDateString());

            soapObject.addProperty("CarData", soapCarData);
        }

        return soapObject;
    }
}
