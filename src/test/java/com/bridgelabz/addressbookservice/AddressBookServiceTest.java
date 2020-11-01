package com.bridgelabz.addressbookservice;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
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

    @Test
    public void givenDateRange_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        LocalDate startDate=LocalDate.of(2018,1,1);
        LocalDate endDate = LocalDate.now();
        List<Contact> contactList = AddressBookService.readContactsBetweenDateRange(startDate,endDate);
        Assert.assertEquals(4, contactList.size());

    }

}
