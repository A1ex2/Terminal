package com.algoritm.terminal.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.Objects.Sector;
import com.algoritm.terminal.Objects.User;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "MyBD.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CarData ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ReceptionID TEXT NOT NULL,"
                + "carID TEXT NOT NULL,"
                + "barCode TEXT NOT NULL,"
                + "sectorID TEXT NOT NULL,"
                + "mRow INTEGER NOT NULL,"
                + "productionDate TEXT NOT NULL)");

    }

    public void insertCarData(CarData carData) {
        SQLiteDatabase db = getReadableDatabase();
        long id = 0;

        try {
            CarData dbCarData = getCarData(carData.getReceptionID(), carData.getCarID());
            if ((dbCarData.getCarID() != null) && (dbCarData.getCarID().equals(carData.getCarID()) & dbCarData.getReceptionID().equals(carData.getReceptionID()))) {
                updateCarData(carData);
            } else {

                ContentValues values = new ContentValues();
                values.put("ReceptionID", carData.getReceptionID());
                values.put("carID", carData.getCarID());
                values.put("barCode", carData.getBarCode());
                values.put("sectorID", carData.getSectorID());
                values.put("mRow", carData.getRow());
                values.put("productionDate", carData.getProductionDateString());

                id = db.insert("CarData", null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCarData(CarData carData) {
        SQLiteDatabase db = getReadableDatabase();
        int id = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("ReceptionID", carData.getReceptionID());
            values.put("carID", carData.getCarID());
            values.put("barCode", carData.getBarCode());
            values.put("sectorID", carData.getSectorID());
            values.put("mRow", carData.getRow());
            values.put("productionDate", carData.getProductionDateString());


//            String receptionID = "'" + carData.getReceptionID() + "'";
//            String carID = "'" + carData.getCarID() + "'";
            String receptionID = carData.getReceptionID();
            String carID = carData.getCarID();

            id = db.update("CarData", values, "ReceptionID=? and carID=?", new String[]{receptionID, carID});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CarData getCarData(String receptionID, String carID) {
        CarData carData = new CarData();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            String select = "ReceptionID = '" + receptionID + "' and carID = '" + carID + "'";
            cursor = db.query("CarData", null, select, null, null, null, null);

            if (cursor.moveToNext()) {

                carData.setReceptionID(cursor.getString(cursor.getColumnIndex("ReceptionID")));
                carData.setCarID(cursor.getString(cursor.getColumnIndex("carID")));
                carData.setBarCode(cursor.getString(cursor.getColumnIndex("barCode")));
                carData.setSectorID(cursor.getString(cursor.getColumnIndex("sectorID")));
                carData.setRow(cursor.getString(cursor.getColumnIndex("mRow")));
                carData.setProductionDate(cursor.getString(cursor.getColumnIndex("productionDate")));
                carData.setBarCode(cursor.getString(cursor.getColumnIndex("barCode")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return carData;
    }

        public ArrayList<CarData> getcarDataList() {
        ArrayList<CarData> carDataArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("CarData", null, null, null, null, null, null);

            if (cursor.moveToNext()) {
                while (!cursor.isAfterLast()) {
                    CarData carData = new CarData();

                    carData.setReceptionID(cursor.getString(cursor.getColumnIndex("ReceptionID")));
                    carData.setCarID(cursor.getString(cursor.getColumnIndex("carID")));
                    carData.setBarCode(cursor.getString(cursor.getColumnIndex("barCode")));
                    carData.setSectorID(cursor.getString(cursor.getColumnIndex("sectorID")));
                    carData.setRow(cursor.getString(cursor.getColumnIndex("mRow")));
                    carData.setProductionDate(cursor.getString(cursor.getColumnIndex("productionDate")));
                    carData.setBarCode(cursor.getString(cursor.getColumnIndex("barCode")));

                    carDataArrayList.add(carData);

                    cursor.moveToNext();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return carDataArrayList;
    }

    public void insertSectors(ArrayList<Sector> sectors) {
        SQLiteDatabase db = getReadableDatabase();
        long id = 0;

        try {
            for (int i = 0; i < sectors.size(); i++) {
                Sector sector = sectors.get(i);

                ContentValues values = new ContentValues();
                values.put(Sector.COLUM_NAME, sector.getName());
//                values.put(Sector.COLUM_GUID, sector.getGuid());

                id = db.insert(Sector.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertUser(User user) {
        SQLiteDatabase db = getReadableDatabase();
        long id = 0;

        try {

            ContentValues values = new ContentValues();
            values.put(User.COLUM_NAME, user.getName());

            id = db.insert(User.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(User.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToNext()) {
                while (!cursor.isAfterLast()) {
                    User user = new User();

                    user.setId(cursor.getLong(cursor.getColumnIndex(User.COLUM_ID)));
                    user.setName(cursor.getString(cursor.getColumnIndex(User.COLUM_NAME)));
                    users.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return users;
    }


//    public ArrayList<Document> getDocuments(String typeDoc) {
//        ArrayList<Document> documents = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            String select = Document.TABLE_NAME + "." + Document.COLUM_TYPE + "='" + typeDoc + "'";
//            cursor = db.query(Document.TABLE_NAME, null, select, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                while (!cursor.isAfterLast()) {
//                    Document document = new Document();
//
//                    //cursor.getColumnNames();
//
//                    document.id = cursor.getLong(cursor.getColumnIndex(Document.COLUM_ID));
//                    document.type = cursor.getString(cursor.getColumnIndex(Document.COLUM_TYPE));
//                    document.dateCreate = stringToDate(cursor.getString(cursor.getColumnIndex(Document.COLUM_DATE_CREATE)));
//                    documents.add(document);
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return documents;
//    }

//    public long insertCell(Cell cell) {
//        SQLiteDatabase db = getReadableDatabase();
//        long id = 0;
//
//        try {
//            ContentValues values = new ContentValues();
//
//            values.put(Cell.COLUM_NAME, cell.name);
//            values.put(Cell.COLUM_ADDRESS, cell.address);
//
//            id = db.insert(Cell.TABLE_NAME, null, values);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return id;
//    }
//
//    public ArrayList<Cell> getCells() {
//        ArrayList<Cell> cells = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            cursor = db.query(Cell.TABLE_NAME, null, null, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                while (!cursor.isAfterLast()) {
//                    Cell cell = new Cell();
//                    cell.setId(cursor.getLong(cursor.getColumnIndex(Cell.COLUM_ID)));
//                    cell.setName(cursor.getString(cursor.getColumnIndex(Cell.COLUM_NAME)));
//                    cell.setAddress(cursor.getString(cursor.getColumnIndex(Cell.COLUM_ADDRESS)));
//
//                    cells.add(cell);
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return cells;
//    }
//
//    public Cell getCell(long id) {
//        Cell cell = new Cell();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            cursor = db.query(Cell.TABLE_NAME, null, Cell.COLUM_ID + " = " + id, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                cell.setId(cursor.getLong(cursor.getColumnIndex(Cell.COLUM_ID)));
//                cell.setName(cursor.getString(cursor.getColumnIndex(Cell.COLUM_NAME)));
//                cell.setAddress(cursor.getString(cursor.getColumnIndex(Cell.COLUM_NAME)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return cell;
//    }
//
//    public Cell getCellToBarCode(String barCode) {
//        Cell cell = new Cell();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            String select = Cell.TABLE_NAME + "." + Cell.COLUM_ADDRESS + " = '" + barCode + "'";
//            cursor = db.query(Cell.TABLE_NAME, null, select, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                cell.setId(cursor.getLong(cursor.getColumnIndex(Cell.COLUM_ID)));
//                cell.setName(cursor.getString(cursor.getColumnIndex(Cell.COLUM_NAME)));
//                cell.setAddress(cursor.getString(cursor.getColumnIndex(Cell.COLUM_NAME)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return cell;
//    }
//
//    public ArrayList<Cell> getCellsDocument(long id) {
//        ArrayList<Cell> cells = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            String select = ProductsOfDocument.TABLE_NAME + "." + ProductsOfDocument.COLUM_ID_DOCUMENT + "=" + id;
//            String group = ProductsOfDocument.COLUM_ID_CELL;
//
//            cursor = db.query(ProductsOfDocument.TABLE_NAME, null, select, null, group, null, null);
//
//            if (cursor.moveToNext()) {
//                while (!cursor.isAfterLast()) {
//
//                    long idCell = cursor.getLong(cursor.getColumnIndex(ProductsOfDocument.COLUM_ID_CELL));
//                    Cell cell = getCell(idCell);
//                    cells.add(cell);
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        return cells;
//    }
//
//    public long insertProduct(Product product) {
//        SQLiteDatabase db = getReadableDatabase();
//        long id = 0;
//
//        try {
//            ContentValues values = new ContentValues();
//
//            values.put(Product.COLUM_NAME, product.getName());
//            values.put(Product.COLUM_VENDOR_CODE, product.getVendorCode());
//
//            id = db.insert(Product.TABLE_NAME, null, values);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return id;
//    }
//
//    public long insertProductCell(long idDocument, long idCell, long idProduct, long quantity) {
//        SQLiteDatabase db = getReadableDatabase();
//        long id = 0;
//
//        try {
//            ContentValues values = new ContentValues();
//
//            values.put(ProductsOfDocument.COLUM_ID_DOCUMENT, idDocument);
//            values.put(ProductsOfDocument.COLUM_ID_CELL, idCell);
//            values.put(ProductsOfDocument.COLUM_ID_PRODUCT, idProduct);
//            values.put(ProductsOfDocument.COLUM_GUANTITY, quantity);
//
//            id = db.insert(ProductsOfDocument.TABLE_NAME, null, values);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return id;
//    }
//
//    public ArrayList<ProductsOfDocument> getProductsCellDocument(long idDocument, long idCell) {
//        ArrayList<ProductsOfDocument> products = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            String select = ProductsOfDocument.TABLE_NAME + "."
//                    + ProductsOfDocument.COLUM_ID_DOCUMENT + " = " + idDocument + " AND "
//                    + ProductsOfDocument.COLUM_ID_CELL + " = " + idCell;
//            ;
//            cursor = db.query(ProductsOfDocument.TABLE_NAME, null, select, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                while (!cursor.isAfterLast()) {
//                    ProductsOfDocument product = new ProductsOfDocument();
//
//                    Product mProduct = getProduct(cursor.getLong(cursor.getColumnIndex(ProductsOfDocument.COLUM_ID_PRODUCT)));
//                    product.setProduct(mProduct);
//
//                    Cell mCell = getCell(cursor.getLong(cursor.getColumnIndex(ProductsOfDocument.COLUM_ID_CELL)));
//                    product.setCell(mCell);
//
//                    product.setId(cursor.getLong(cursor.getColumnIndex(ProductsOfDocument.COLUM_ID)));
//                    product.setQuantity(cursor.getLong(cursor.getColumnIndex(ProductsOfDocument.COLUM_GUANTITY)));
//
//                    products.add(product);
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return products;
//    }
//
//    public ArrayList<Product> getProducts() {
//        ArrayList<Product> products = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            cursor = db.query(Product.TABLE_NAME, null, null, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                while (!cursor.isAfterLast()) {
//                    Product product = new Product();
//                    product.setId(cursor.getLong(cursor.getColumnIndex(Product.COLUM_ID)));
//                    product.setName(cursor.getString(cursor.getColumnIndex(Product.COLUM_NAME)));
//                    product.setVendorCode(cursor.getString(cursor.getColumnIndex(Product.COLUM_VENDOR_CODE)));
//
//                    products.add(product);
//                    cursor.moveToNext();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return products;
//    }
//
//    public Product getProductToBarCode(String barCode) {
//        Product product = new Product();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            String select = Product.TABLE_NAME + "." + Product.COLUM_VENDOR_CODE + " = '" + barCode + "'";
//            cursor = db.query(Product.TABLE_NAME, null, select, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                product.setId(cursor.getLong(cursor.getColumnIndex(Product.COLUM_ID)));
//                product.setName(cursor.getString(cursor.getColumnIndex(Product.COLUM_NAME)));
//                product.setVendorCode(cursor.getString(cursor.getColumnIndex(Product.COLUM_VENDOR_CODE)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return product;
//    }
//
//    public Product getProduct(long id) {
//        Product product = new Product();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = null;
//
//        try {
//            cursor = db.query(Product.TABLE_NAME, null, Product.COLUM_ID + " = " + id, null, null, null, null);
//
//            if (cursor.moveToNext()) {
//                product.setId(cursor.getLong(cursor.getColumnIndex(Product.COLUM_ID)));
//                product.setName(cursor.getString(cursor.getColumnIndex(Product.COLUM_NAME)));
//                product.setVendorCode(cursor.getString(cursor.getColumnIndex(Product.COLUM_VENDOR_CODE)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        return product;
//    }
//
//    private String dateToFormat(Date date) {
//        String retval = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        if (date == null) {
//            return retval;
//        }
//        retval = sdf.format(date);
//        return retval;
//    }
//
//    private Date stringToDate(String text) {
//        Date mDate = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//
//        if (!text.isEmpty()) {
//            try {
//                mDate = sdf.parse(text);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return mDate;
//    }
//
//    public void deleteProductOfDocument(ProductsOfDocument productsOfDocument) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(ProductsOfDocument.TABLE_NAME, ProductsOfDocument.COLUM_ID + "=" + productsOfDocument.getId(), null);
//    }
//
//    public void deleteCell(Cell cell) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(Cell.TABLE_NAME, cell.COLUM_ID + "=" + cell.getId(), null);
//    }
//
//    public void deleteCellsAll() {
//        ArrayList<Cell> mCells = getCells();
//        for (Cell cell : mCells) {
//            deleteCell(cell);
//        }
//    }
//
//    public void deleteProduct(Product product) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(Product.TABLE_NAME, Product.COLUM_ID + "=" + product.getId(), null);
//    }
//
//    public void deleteProductAll() {
//        ArrayList<Product> mProducts = getProducts();
//        for (Product product : mProducts) {
//            deleteProduct(product);
//        }
//    }
//
//    public ArrayList<Cell> initialiseCellData() {
//        ArrayList<Cell> cellArrayList = new ArrayList<>();
//
//        int count = soapParam_Response.getPropertyCount();
//
//        for (int i = 0; i < count; i++) {
//            SoapObject property = (SoapObject) soapParam_Response.getProperty(i);
//            //Log.i("initialiseEmploy", ""+property.toString());
//            if (property instanceof SoapObject) {
//                SoapObject info = (SoapObject) property;
//                String name = info.getProperty("name").toString();
//                String address = info.getProperty("address").toString();
//                Cell cell = new Cell(name, address);
//                cellArrayList.add(cell);
//            }
//        }
//        return cellArrayList;
//    }
//
//    public ArrayList<Product> initialiseProductData() {
//        ArrayList<Product> productArrayList = new ArrayList<>();
//
//        int count = soapParam_Response.getPropertyCount();
//
//        for (int i = 0; i < count; i++) {
//            SoapObject property = (SoapObject) soapParam_Response.getProperty(i);
//            //Log.i("initialiseEmploy", ""+property.toString());
//            if (property instanceof SoapObject) {
//                SoapObject info = (SoapObject) property;
//                String name = info.getProperty("name").toString();
//                String vendorCode = info.getProperty("vendorCode").toString();
//                String barcode = info.getProperty("barcode").toString();
//
//                Product product = new Product();
//                product.setName(name);
//                product.setVendorCode(vendorCode);
//                product.setBarcode(barcode);
//
//                productArrayList.add(product);
//            }
//        }
//        return productArrayList;
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
