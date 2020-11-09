package com.bridgelabz.addressbookservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddressBookService {
    public  List<Contact> contacts;
    private AddressBookDBService addressBookDBService;

    public enum IOService {DB_IO, REST_IO}

    //Constructor
    public AddressBookService() {
        addressBookDBService = addressBookDBService.getInstance();
        contacts = new ArrayList<>();
    }

    //Parameterised Constructor
    public AddressBookService(List<Contact> contactList) {
        this();
        contacts = new ArrayList<>(contactList);
    }

    //Method to Call ReadDataDB From DB Layer
    public List<Contact> readDataFromService() throws SQLException {
        contacts = addressBookDBService.readDataFromDB();
        return contacts;
    }

    //Method to Call ReadDataDB  For Date Range From DB Layer
    public  List<Contact> readContactsBetweenDateRange(LocalDate startDate, LocalDate endDate) {
        this.contacts = addressBookDBService.readDataGivenDateRangeFromDB(startDate, endDate);
        return this.contacts;
    }

    //Method to Call ReadDataDB  For City From DB Layer
    public List<Contact> readContactsByCity(String city) {
        this.contacts = addressBookDBService.readContactsByCity(city);
        return this.contacts;

    }

    //Method to Call ReadDataDB  For State From DB Layer
    public List<Contact> readContactsByState(String city) {
        this.contacts = addressBookDBService.readContactsByState(city);
        return this.contacts;

    }

    //Method to Update Data To DB Layer And In List
    public void updateAddressToService(String firstName, String address, IOService ioService) {
        if (ioService == IOService.DB_IO) {
            int result = addressBookDBService.updateContactDataToDB(firstName, address);
            if (result == 0) return;
        }
        Contact contact = this.getContactData(firstName);
        if (contact != null) contact.address = address;
    }

    //Method to Insert Data To DB Layer And Add To List
    public void addContactToAddressBook(Contact contact, IOService ioService) {
        if (ioService == IOService.DB_IO)
            this.contacts.add(addressBookDBService.addContactToDB(contact.firstName, contact.lastName, contact.address,
                    contact.city, contact.state, contact.zip, contact.phoneNumber, contact.email, contact.registeredDate
            ));
        else if (ioService == IOService.REST_IO)
            this.contacts.add(contact);
    }

    //Method to Insert Multiple Data To DB Layer Using Threads And Add To List
    public synchronized void addMultiContactToAddressBookWithThreads(List<Contact> contactList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        contactList.forEach(contactData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(contactData.hashCode(), false);
                System.out.println("Contact Being Added Via Thread: " + Thread.currentThread().getName());
                addContactToAddressBook(contactData, IOService.DB_IO);
                employeeAdditionStatus.put(contactData.hashCode(), true);
                System.out.println("Employee Added Via Thread: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, contactData.firstName);
            thread.start();
        });
        System.out.println("AFTER THREADS OPERATION-------------------------\n" + contacts);
    }

    //Method To Find Contact Data From List
    public Contact getContactData(String name) {
        return this.contacts.stream()
                .filter(employeePayrollData -> employeePayrollData.firstName.equals(name))
                .findFirst()
                .orElse(null);

    }

    //Method To Check DB Is In Sync
    public boolean checkIfDataBaseIsInSync(String name) throws SQLException {
        List<Contact> employeePayrollDataList = addressBookDBService.getContactUsingPreparedStatementFromDB(name);
        return employeePayrollDataList.get(0).equals(getContactData(name));

    }

    //Method To Remove Contact From List
    public int removeContactData(String firstName) {
        contacts = contacts.stream().filter(contact -> !contact.firstName.equals(firstName)).collect(Collectors.toList());
        return contacts.size();
    }

    //Method To Count Entries In List
    public long countEntries() {
        return contacts.size();
    }
}
