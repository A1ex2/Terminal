package com.algoritm.terminal.DataBase;

import com.algoritm.terminal.Activity.MainActivity;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Objects.Sector;
import com.algoritm.terminal.Objects.User;

import java.util.ArrayList;

public class SharedData {
    public static ArrayList<Sector> SECTORS = new ArrayList<>();
    public static ArrayList<User> USERS = new ArrayList<>();
    public static ArrayList<Reception> RECEPTION = new ArrayList<>();
    public static MainActivity app;

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
                        mCarData.setSector(carData.getSector());
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

    public static void updateReceptionsDB() {
        DataBaseHelper helper = new DataBaseHelper(app);
        ArrayList<CarData> carDataArrayList = helper.getcarDataList();

        for (int i = 0; i < carDataArrayList.size(); i++) {
            CarData carData = carDataArrayList.get(i);
            if (!updateReceptionDB(carData)) {
                // delete
            }

        }
    }

    public static boolean updateReceptionDB(CarData carData) {
        boolean mFinish = false;
        for (int i = 0; i < RECEPTION.size(); i++) {
            Reception mReception = RECEPTION.get(i);
            if (mReception.getID().equals(carData.getReceptionID())) {
                ArrayList<CarData> carDataArrayList = mReception.getCarData();
                for (int j = 0; j < carDataArrayList.size(); j++) {
                    CarData mCarData = carDataArrayList.get(j);
                    if (mCarData.getCarID().equals(carData.getCarID())) {
                        mCarData.setProductionDate(carData.getProductionDate());
                        mCarData.setSector(getSector(carData.getSectorID()).getName());
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

        return mFinish;
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

    public static Sector getSector(String id) {
        Sector sector = new Sector();
        for (int i = 0; i < SECTORS.size(); i++) {
            Sector mSector = SECTORS.get(i);
            if (mSector.getID().equals(id)) {
                sector = mSector;
                break;
            }

        }
        return sector;
    }


}