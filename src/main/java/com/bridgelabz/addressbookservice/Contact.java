package com.bridgelabz.addressbookservice;

import java.time.LocalDate;
import java.util.Objects;

public class Contact {
    public int contactID;
    public String firstName;
    public String lastName;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String phoneNumber;
    public String email;

    public Contact(int contactID, String firstName, String lastName, String address, String city, String state, String zip, String phoneNumber, String email) {
        this.contactID = contactID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return contactID == contact.contactID &&
                Objects.equals(firstName, contact.firstName) &&
                Objects.equals(lastName, contact.lastName) &&
                Objects.equals(address, contact.address) &&
                Objects.equals(city, contact.city) &&
                Objects.equals(state, contact.state) &&
                Objects.equals(zip, contact.zip) &&
                Objects.equals(phoneNumber, contact.phoneNumber) &&
                Objects.equals(email, contact.email);
    }
}
