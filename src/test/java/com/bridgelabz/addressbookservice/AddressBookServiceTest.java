package com.bridgelabz.addressbookservice;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddressBookServiceTest {

    Contact contactToAddToJsonServer = null;
    Contact newContact = null;
    Contact[] contactsArrayWithThreads = {
            new Contact(0, "Prince", "Jain", "xyz apartments", "Raipur", "Chhattisgarh", "493661", "9871116542", "pj@gmail.com", LocalDate.now()),
            new Contact(0, "Neeraj", "Jain", "abc building", "Raipur", "Chhattisgarh", "493661", "1111222200", "nj@gmail.com", LocalDate.now())
    };

    //Initializing
    @Before
    public void setUP() {
        newContact = new Contact(0, "Priyanka", "Kalena", "Orchid", "Mumbai", "Maharastra", "400096", "998855975", "pk@gmail.com", LocalDate.now());
        contactToAddToJsonServer = new Contact(4, "Prek", "Japtap", "Neelkant", "Mumbai", "Maharashtra", "400096", "669855975", "pj@gmail.com", LocalDate.now());
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    //Data Retrieval Testcase
    @Test
    public void myTestA_givenContactData_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = addressBookService.readDataFromService();
        Assert.assertEquals(4, contactList.size());

    }

    //Data Updating Testcase
    @Test
    public void myTestB_givenNewAddress_whenUpdated_shouldSyncWithDB() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contacts = addressBookService.readDataFromService();
        addressBookService.updateAddressToService("Ritu", "Orchid", AddressBookService.IOService.DB_IO);
        boolean result = addressBookService.checkIfDataBaseIsInSync("Ritu");
        Assert.assertTrue(result);
    }

    //Data Retrieval For Date Range Testcase
    @Test
    public void myTestC_givenDateRange_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.now();
        List<Contact> contactList =addressBookService.readContactsBetweenDateRange(startDate, endDate);
        Assert.assertEquals(4, contactList.size());

    }

    //Data Retrieval For City Testcase
    @Test
    public void myTestD_givenCity_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = addressBookService.readContactsByCity("Mumbai");
        Assert.assertEquals(2, contactList.size());

    }

    //Data Retrieval For State Testcase
    @Test
    public void myTestE_givenState_whenRetrieved_shouldMatchSize() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        List<Contact> contactList = addressBookService.readContactsByState("West Bengal");
        Assert.assertEquals(2, contactList.size());

    }

    //Data Insertion Testcase
    @Test
    public void myTestF_givenNewContact_whenAdded_shouldSyncWithDB() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        addressBookService.readDataFromService();
        addressBookService.addContactToAddressBook(newContact, AddressBookService.IOService.DB_IO);
        boolean result = addressBookService.checkIfDataBaseIsInSync("Priyanka");
        Assert.assertTrue(result);
    }

    //Multiple Data Insertion Using Threads Testcase
    @Test
    public void myTestG_givenMultipleContacts_whenAddedUsingThreads_shouldSyncWithDB() throws SQLException, InterruptedException {
        AddressBookService addressBookService = new AddressBookService();
        addressBookService.readDataFromService();
        Instant threadStart = Instant.now();
        addressBookService.addMultiContactToAddressBookWithThreads(Arrays.asList(contactsArrayWithThreads));
        Thread.sleep(1600);
        Instant threadEnd = Instant.now();
        System.out.println("Duration With Thread: " + Duration.between(threadStart, threadEnd));
    }

    //Retrieve Data From Json Server
    @Test
    public void myTestH_givenContactDataInJsonServer_whenRetrieved_shouldMatchTheCount() {
        Contact[] arrayOfContacts = getContactList();
        AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        long entries = addressBookService.countEntries();
        Assert.assertEquals(3, entries);
    }

    //Insert Data To Json Server
    @Test
    public void myTestI_givenContactData_whenAddedToJsonServer_ShouldMatchResponse201AndCount() {
        AddressBookService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        Response response = addContactDataToJsonServer(contactToAddToJsonServer);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);
        contactToAddToJsonServer = new Gson().fromJson(response.asString(), Contact.class);
        addressBookService.addContactToAddressBook(contactToAddToJsonServer, AddressBookService.IOService.REST_IO);
        long entries = addressBookService.countEntries();
        Assert.assertEquals(4, entries);
    }

    //Update Data To Json Server
    @Test
    public void myTestJ_givenNewAddressForContact_whenUpdatedToJsonServer_shouldMatch200Response() throws SQLException {
        AddressBookService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        addressBookService.updateAddressToService("Dyo", "XYZ Society", AddressBookService.IOService.DB_IO);
        Contact contact = addressBookService.getContactData("Dyo");
        System.out.println("UPDATED ADDRESS-----------------" + contact.address + contact.contactID);
        String contactJson = new Gson().toJson(contact);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(contactJson);
        Response response = requestSpecification.put("/contacts/" + contact.contactID);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);


    }

    //Delete Data From Json Server
    @Test
    public void myTestK_givenEmployeeToDelete_whenDeleted_shouldMatch200ResponseAndCount() {
        AddressBookService addressBookService;
        Contact[] arrayOfEmployees = getContactList();
        addressBookService = new AddressBookService(Arrays.asList(arrayOfEmployees));
        Contact contact = addressBookService.getContactData("Prek");
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        System.out.println("UPDATED ADDRESS-----------------" + contact.address + contact.contactID);
        Response response = requestSpecification.delete("/contacts/" + contact.contactID);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);
        addressBookService.removeContactData(contact.firstName);
        long entries = addressBookService.countEntries();
        Assert.assertEquals(3, entries);
    }

    //Method To Add Data To Json Server
    private Response addContactDataToJsonServer(Contact contact) {
        String contactJson = new Gson().toJson(contact);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(contactJson);
        return requestSpecification.post("/contacts");
    }

    //Method To Get Contact Data From Json Server
    private Contact[] getContactList() {
        Response response = RestAssured.get("/contacts");
        System.out.println("CONTACT ENTRIES IN JSON Server:\n" + response.asString());
        Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
        return arrayOfContacts;
    }
}
