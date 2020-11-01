package com.bridgelabz.addressbookservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {
    private static AddressBookDBService addressBookDBService;

    private AddressBookDBService(){}

    public static AddressBookDBService getInstance() {
        if (addressBookDBService == null)
            addressBookDBService = new AddressBookDBService();
        return addressBookDBService;

    }

    public static List<Contact> readData() {
        String sql = "select * from contact;";
        return getContactList(sql);

    }
    public static List<Contact> getContactList(String sql){
        List<Contact> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = getContact(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeePayrollDataList;
    }

    private static List<Contact> getContact(ResultSet resultSet) {
        List<Contact> contactDataList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String  lastName= resultSet.getString(3);
                String address= resultSet.getString(4);
                String city = resultSet.getString(5);
                String state = resultSet.getString(6);
                String zip = resultSet.getString(7);
                String phoneNumber= resultSet.getString(8);
                String email = resultSet.getString(9);

                contactDataList.add(new Contact(id, firstName, lastName, address,city, state,zip,phoneNumber,email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactDataList;
    }

    private static Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?userSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection Successful!!!!" + connection);
        return connection;
    }

}
