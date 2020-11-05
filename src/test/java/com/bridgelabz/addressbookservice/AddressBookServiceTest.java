package com.bridgelabz.addressbookservice;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AddressBookServiceTest {

    @Before
    public void setUP(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

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
        addressBookService.updateContactsAddress("Ritu", "Anushaktinagar",0);
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
    public void givenNewContact_whenAdded_shouldSyncWithDB() throws SQLException {
        AddressBookService addressBookService = new AddressBookService();
        addressBookService.readContactData();
        addressBookService.addContactToAddressBook("Priyanka", "Kalena", "Orchid", "Mumbai", "Maharastra", "400096", "998855975", "pk@gmail.com", LocalDate.now());
        boolean result = addressBookService.checkIfDataBaseIsInSync("Priyanka");
        Assert.assertTrue(result);

    }

    @Test
    public void givenMultipleContacts_whenAdded_shouldSyncWithDB() throws SQLException, InterruptedException {
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

    @Test
    public void givenContactDataInJsonServer_whenRetrieved_shouldMatchTheCount() {
        Contact[] arrayOfContacts = getContactList();
        AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        long entries = addressBookService.countEntries();
        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenContactData_whenAddedToJsonServer_ShouldMatchResponse201AndCount() {
        AddressBookService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        Contact contact = new Contact(4, "Prek", "Japtap", "Neelkant", "Mumbai", "Maharashtra", "400096", "669855975", "pj@gmail.com", LocalDate.now());
        Response response = addContactDataToJsonServer(contact);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);
        contact = new Gson().fromJson(response.asString(), Contact.class);
        addressBookService.addEmployeeDataForREST(contact);
        long entries = addressBookService.countEntries();
        Assert.assertEquals(4, entries);
    }

    @Test
    public void givenNewAddressForContact_whenUpdatedToJsonServer_shouldMatch200Response() throws SQLException {
        AddressBookService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
        addressBookService.updateContactsAddress("Dyo", "XYZ Society",1);
        Contact contact = addressBookService.getContactData("Dyo");
        System.out.println("UPDATED ADDRESS-----------------"+contact.address+contact.contactID);
        String contactJson = new Gson().toJson(contact);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(contactJson);
        Response response = requestSpecification.put("/contacts/" + contact.contactID);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);


    }

    private Response addContactDataToJsonServer(Contact contact) {
        String contactJson = new Gson().toJson(contact);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(contactJson);
        return requestSpecification.post("/contacts");
    }

    private Contact[] getContactList() {
        Response response = RestAssured.get("/contacts");
        System.out.println("CONTACT ENTRIES IN JSON Server:\n" + response.asString());
        Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
        return arrayOfContacts;
    }
}
