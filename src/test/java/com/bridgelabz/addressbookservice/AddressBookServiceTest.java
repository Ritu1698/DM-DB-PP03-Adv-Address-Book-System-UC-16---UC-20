package com.bridgelabz.addressbookservice;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class AddressBookServiceTest {
    @Test
    public void givenContactData_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = addressBookService.readContactData();
        Assert.assertEquals(4, contactList.size());

    }

    @Test
    public void givenNewAddress_whenUpdated_shouldSyncWithDB() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contacts = AddressBookService.readContactData();
        addressBookService.updateContactsAddress("Ritu", "Anushaktinagar");
        boolean result = addressBookService.checkIfDataBaseIsInSync("Ritu");
        Assert.assertTrue(result);
    }

}
