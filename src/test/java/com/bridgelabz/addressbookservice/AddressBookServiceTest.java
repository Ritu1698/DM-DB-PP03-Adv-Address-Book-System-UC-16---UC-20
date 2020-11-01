package com.bridgelabz.addressbookservice;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class AddressBookServiceTest {
    @Test
    public void readData() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = addressBookService.readContactData();
        Assert.assertEquals(4, contactList.size());

    }

}
