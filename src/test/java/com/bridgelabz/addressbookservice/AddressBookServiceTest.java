package com.bridgelabz.addressbookservice;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.now();
        List<Contact> contactList = AddressBookService.readContactsBetweenDateRange(startDate, endDate);
        Assert.assertEquals(4, contactList.size());

    }

    @Test
    public void givenCity_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = AddressBookService.readContactsByCity("Mumbai");
        Assert.assertEquals(2, contactList.size());

    }

    @Test
    public void givenState_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = AddressBookService.readContactsByState("West Bengal");
        Assert.assertEquals(2, contactList.size());

    }

    @Test
    public void givenNewEmployee_whenAdded_shouldSyncWithDB() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        addressBookService.readContactData();
        addressBookService.addContactToAddressBook("Priyanka", "Kalena", "Orchid", "Mumbai", "Maharastra", "400096", "998855975", "pk@gmail.com", LocalDate.now());
        boolean result = addressBookService.checkIfDataBaseIsInSync("Priyanka");
        Assert.assertTrue(result);

    }

    @Test
    public void givenMultipleEmployees_whenAdded_shouldSyncWithDB() throws SQLException, InterruptedException {
        Contact[] contactsArrayWithoutThreads = {
                new Contact(0, "Prek", "Japtap", "Neelkant", "Mumbai", "Maharashtra", "400096", "669855975", "pj@gmail.com", LocalDate.now()),
                new Contact(0, "Rini", "Kumar", "Raheja", "Mumbai", "Maharashtra", "400096", "9757654329", "rk@gmail.com", LocalDate.now()),

        };
        Contact[] contactsArrayWithThreads = {
                new Contact(0, "Prince", "Jain", "xyz apartments", "Raipur", "Chhattisgarh", "493661", "9871116542", "pj@gmail.com", LocalDate.now()),
                new Contact(0, "Neeraj", "Jain", "abc building", "Raipur", "Chhattisgarh", "493661", "1111222200", "nj@gmail.com", LocalDate.now())
        };
        AddressBookService addressBookService = new AddressBookService();
        addressBookService.readContactData();
        Instant threadStart = Instant.now();
        addressBookService.addMultiContactToAddressBookWithThreads(Arrays.asList(contactsArrayWithThreads));
        Thread.sleep(1600);
        Instant threadEnd = Instant.now();
        System.out.println("Duration With Thread: " + Duration.between(threadStart, threadEnd));
    }

}
