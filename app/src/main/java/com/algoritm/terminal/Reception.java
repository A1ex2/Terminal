package com.algoritm.terminal;

public class Reception {
    private String ID;
    private String Description;
    private String AutoNumber;
    private String Driver;
    private String DriverPhone;
    private String InvoiceNumber;

    public Reception() {
    }

    public String getID() {
        return ID;
    }

    public String getDescription() {
        return Description;
    }

    public String getAutoNumber() {
        return AutoNumber;
    }

    public String getDriver() {
        return Driver;
    }

    public String getDriverPhone() {
        return DriverPhone;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setAutoNumber(String autoNumber) {
        AutoNumber = autoNumber;
    }

    public void setDriver(String driver) {
        Driver = driver;
    }

    public void setDriverPhone(String driverPhone) {
        DriverPhone = driverPhone;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }
}
