package com.bridgelabz.addressbookservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookService {
    public static List<Contact> contacts;
    private AddressBookDBService addressBookDBService;

    public AddressBookService() {
        addressBookDBService = AddressBookDBService.getInstance();
        contacts = new ArrayList<>();
    }

    public static List<Contact> readContactData() throws SQLException {
        contacts = AddressBookDBService.readData();
        return contacts;

    }

    public static List<Contact> readContactsBetweenDateRange(LocalDate startDate, LocalDate endDate) {
        contacts = AddressBookDBService.readDataGivenDateRange(startDate, endDate);
        return contacts;
    }

    public void updateContactsAddress(String firstName, String address) {
        int result = AddressBookDBService.updateContactData(firstName, address);
        if (result == 0) return;
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
}
