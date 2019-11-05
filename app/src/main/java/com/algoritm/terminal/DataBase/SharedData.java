package com.algoritm.terminal.DataBase;

import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Objects.Sector;
import com.algoritm.terminal.Objects.User;

import java.util.ArrayList;

public class SharedData {
    public static ArrayList<Sector> SECTORS = new ArrayList<>();
    public static ArrayList<User> USERS = new ArrayList<>();
    public static ArrayList<Reception> RECEPTION = new ArrayList<>();

    public static void updateReception(CarData carData) {
        boolean mFinish = false;
        for (int i = 0; i < RECEPTION.size(); i++) {
            Reception mReception = RECEPTION.get(i);
            if (mReception.getID().equals(carData.getReceptionID())) {
                ArrayList<CarData> carDataArrayList = mReception.getCarData();
                for (int j = 0; j < carDataArrayList.size(); j++) {
                    CarData mCarData = carDataArrayList.get(j);
                    if (mCarData.getCarID().equals(carData.getCarID())) {
                        mCarData.setProductionDate(carData.getProductionDate());
                        mCarData.setSectorID(carData.getSector());
                        mCarData.setSectorID(carData.getSectorID());
                        mCarData.setRow(carData.getRow());

                        mFinish = true;
                        break;
                    }
                }
            }

            if (mFinish) {
                break;
            }
        }
    }

    public static Reception getReception(String id) {
        Reception reception = new Reception();
        for (int i = 0; i < RECEPTION.size(); i++) {
            Reception mReception = RECEPTION.get(i);
            if (mReception.getID().equals(id)) {
                reception = mReception;
                break;
            }

        }
        return reception;
    }
}