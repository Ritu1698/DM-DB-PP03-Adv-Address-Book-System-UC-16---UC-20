package com.bridgelabz.addressbookservice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {
    private static AddressBookDBService addressBookDBService;
    private static PreparedStatement contactStatement;

    //Constructor
    private AddressBookDBService() {
    }

    //Method To Create Singleton Object
    public static AddressBookDBService getInstance() {
        if (addressBookDBService == null)
            addressBookDBService = new AddressBookDBService();
        return addressBookDBService;

    }

    //Method To Read All Data From DB
    public List<Contact> readDataFromDB() {
        String sql = "select * from contact;";
        return getContactsUsingStatementFromDB(sql);

    }

    //Method To Read Date Ranged Data From DB
    public List<Contact> readDataGivenDateRangeFromDB(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("select * from contact where registered_date between '%s' and '%s';", Date.valueOf(startDate), Date.valueOf(endDate));
        return getContactsUsingStatementFromDB(sql);
    }

    //Method To Read City Data From DB
    public List<Contact> readContactsByCity(String city) {
        String sql = String.format("select * from contact where city='%s'", city);
        return getContactsUsingStatementFromDB(sql);

    }

    //Method To Read State Data From DB
    public List<Contact> readContactsByState(String state) {
        String sql = String.format("select * from contact where state='%s'", state);
        return getContactsUsingStatementFromDB(sql);

    }

    //Method To Get Contacts As List Using Statement
    public List<Contact> getContactsUsingStatementFromDB(String sql) {
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

    //Method To Get Contact And Add To List
    private List<Contact> getContact(ResultSet resultSet) {
        List<Contact> contactDataList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String address = resultSet.getString(4);
                String city = resultSet.getString(5);
                String state = resultSet.getString(6);
                String zip = resultSet.getString(7);
                String phoneNumber = resultSet.getString(8);
                String email = resultSet.getString(9);
                LocalDate registeredDate = resultSet.getDate(10).toLocalDate();

                contactDataList.add(new Contact(id, firstName, lastName, address, city, state, zip, phoneNumber, email, registeredDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactDataList;
    }

    //Method to Update Data To DB
    public int updateContactDataToDB(String firstName, String address) {
        String sql = String.format("update contact set address='%s' where first_name='%s';", address, firstName);
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Method To Insert Data To DB
    public Contact addContactToDB(String firstName, String lastName, String address, String city, String state, String zip, String number, String email, LocalDate registeredDate) {

        int contact_id = -1;
        Connection connection = null;
        Contact contact = null;
        try {
            connection = getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement();) {
            String sql = String.format("insert into contact(first_name, last_name, address,city, state,zip," +
                            "phone_number,email, registered_date) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s')"
                    , firstName, lastName, address, city, state, zip, number, email, Date.valueOf(registeredDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    contact_id = resultSet.getInt(1);
                    contact = new Contact(contact_id, firstName, lastName, address, city, state, zip, number, email, registeredDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    //Method To Form Connection To DB
    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?userSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection Successful!!!!" + connection);
        return connection;
    }

    //Get Contacts Using Prepared Statement From List
    public List<Contact> getContactUsingPreparedStatementFromDB(String name) throws SQLException {
        List<Contact> contactList = null;
        if (contactStatement == null)
            prepareStatementForEmployeeData();
        try {
            contactStatement.setString(1, name);
            ResultSet resultSet = contactStatement.executeQuery();
            contactList = getContact(resultSet);
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactList;


    }

    //Method To Execute Prepared Statement
    private void prepareStatementForEmployeeData() throws SQLException {
        try {
            Connection connection = getConnection();
            String sql = "SELECT * FROM contact where first_name= ?";
            contactStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
