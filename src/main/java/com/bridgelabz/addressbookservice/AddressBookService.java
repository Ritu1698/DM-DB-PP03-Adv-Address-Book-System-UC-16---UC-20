package com.bridgelabz.addressbookservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookService {
    public static List<Contact> contacts;
    private AddressBookDBService addressBookDBService;

    public AddressBookService() {
        addressBookDBService = AddressBookDBService.getInstance();
        contacts = new ArrayList<>();
    }
    public AddressBookService(List<Contact>contactList) {
        this();
        contacts = new ArrayList<>(contactList);
    }

    public static List<Contact> readContactData() throws SQLException {
        contacts = AddressBookDBService.readData();
        return contacts;

    }

    public static List<Contact> readContactsBetweenDateRange(LocalDate startDate, LocalDate endDate) {
        contacts = AddressBookDBService.readDataGivenDateRange(startDate, endDate);
        return contacts;
    }

    public void updateContactsAddress(String firstName, String address,int restFlag) {
        if(restFlag == 0){
            int result = AddressBookDBService.updateContactData(firstName, address);
            if (result == 0) return;
        }
        Contact contact = this.getContactData(firstName);
        if (contact != null) contact.address = address;
    }

    public Contact getContactData(String name) {
        return this.contacts.stream()
                .filter(employeePayrollData -> employeePayrollData.firstName.equals(name))
                .findFirst()
                .orElse(null);

    }

    public boolean checkIfDataBaseIsInSync(String name) throws SQLException {
        List<Contact> employeePayrollDataList = AddressBookDBService.getContactFromDB(name);
        return employeePayrollDataList.get(0).equals(getContactData(name));

    }

    public static List<Contact> readContactsByCity(String city) {
        contacts = AddressBookDBService.readContactsByCity(city);
        return contacts;

    }

    public static List<Contact> readContactsByState(String city) {
        contacts = AddressBookDBService.readContactsByState(city);
        return contacts;

    }

    public static void addContactToAddressBook(String firstName, String lastName, String address, String city, String state, String zip, String number, String email, LocalDate start) {
        contacts.add(AddressBookDBService.addContactToDB(firstName, lastName, address, city, state, zip, number, email, start));
    }

    public void addMultiContactToAddressBook(List<Contact> contactList) {
        contactList.forEach(contactData -> {
            System.out.println("Employee Being Added: " + contactData.firstName);
            addContactToAddressBook(contactData.firstName, contactData.lastName, contactData.address, contactData.city, contactData.state, contactData.zip, contactData.phoneNumber, contactData.email, contactData.registeredDate);
            System.out.println("Employee Added: " + contactData.firstName);
        });
        System.out.println("AFTER PROCESS OPERATION-------------------------\n" + contacts);
    }

    public void addMultiContactToAddressBookWithThreads(List<Contact> contactList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        contactList.forEach(contactData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(contactData.hashCode(), false);
                System.out.println("Contact Being Added Via Thread: " + Thread.currentThread().getName());
                addContactToAddressBook(contactData.firstName, contactData.lastName, contactData.address, contactData.city, contactData.state, contactData.zip, contactData.phoneNumber, contactData.email, contactData.registeredDate);
                employeeAdditionStatus.put(contactData.hashCode(), true);
                System.out.println("Employee Added Via Thread: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, contactData.firstName);
            thread.start();
        });
        System.out.println("AFTER THREADS OPERATION-------------------------\n" + contacts);
    }

    public long countEntries() {
        return contacts.size();
    }

    public void addEmployeeDataForREST(Contact contact) {
        contacts.add(contact);
    }
}
