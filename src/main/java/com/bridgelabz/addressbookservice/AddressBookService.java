package com.bridgelabz.addressbookservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressBookService {
    public static List<Contact> contacts;
    private  AddressBookDBService addressBookDBService;
    public AddressBookService(){
        addressBookDBService = AddressBookDBService.getInstance();
        this.contacts=new ArrayList<>();
    }

    public List<Contact> readContactData() throws SQLException {
        this.contacts= AddressBookDBService.readData();
        return this.contacts;

    }
}
