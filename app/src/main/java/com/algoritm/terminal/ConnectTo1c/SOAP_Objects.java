package com.algoritm.terminal.ConnectTo1c;


import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Reception;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SOAP_Objects {

    public static String getReception(Reception reception) {
        JSONReception jsonReception = new JSONReception(reception);

        Gson gson = new GsonBuilder().create();

        String stringReception = gson.toJson(jsonReception);

        return stringReception;
    }

    private static class JSONReception {
        @SerializedName("ID")
        String ID;

        @SerializedName("CarData")
        ArrayList<JSONCarData> mJSONCarData = new ArrayList<>();

        JSONReception(Reception reception) {
            ID = reception.getID();

            ArrayList<CarData> carDataArrayList = reception.getCarData();
            for (int i = 0; i < carDataArrayList.size(); i++) {
                CarData carData = carDataArrayList.get(i);
                JSONCarData jsonCarData = new JSONCarData(carData);
                mJSONCarData.add(jsonCarData);
            }
        }

        private static class JSONCarData {
            @SerializedName("ReceptionID")
            String ReceptionID;

            @SerializedName("carID")
            String carID;

            @SerializedName("sectorID")
            String sectorID;

            @SerializedName("row")
            String row;

            @SerializedName("productionDate")
            String productionDate;

            JSONCarData(CarData carData) {
                ReceptionID = carData.getReceptionID();
                carID = carData.getCarID();
                sectorID = carData.getSectorID();
                row = carData.getRow();
                productionDate = carData.getProductionDateString();

            }
        }
    }
}
