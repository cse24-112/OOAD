package com.bankingsystem.mvc.controller;

import com.bankingsystem.mvc.dao.CustomerDAO;
import com.bankingsystem.mvc.dao.StaffDAO;
import com.bankingsystem.mvc.model.Customer;
import com.bankingsystem.mvc.model.Staff;

/**
 * Handles login for Customer, Company and Staff (staff handled separately in this simplified example)
 */
public class LoginController {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    public LoginController() {}

    public LoginResult login(String username, String password, String userType) {
        try {
            if ("Staff".equalsIgnoreCase(userType)) {
                Staff s = staffDAO.findByUsernameAndPassword(username, password);
                if (s != null) {
                    LoginResult lr = new LoginResult(LoginResult.Role.STAFF);
                    lr.setStaff(s);
                    return lr;
                }
                return null;
            } else {
                // treat companies and individuals the same at the moment (login via email)
                Customer c = customerDAO.findByEmailAndPassword(username, password);
                if (c != null) {
                    LoginResult lr = new LoginResult("Company".equalsIgnoreCase(userType) ? LoginResult.Role.COMPANY : LoginResult.Role.CUSTOMER);
                    lr.setCustomer(c);
                    return lr;
                }
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
