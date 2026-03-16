package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoleConstant;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Customer;
import com.mycompany.hotelmanagementsystem.dao.AccountRepository;
import com.mycompany.hotelmanagementsystem.dao.CustomerRepository;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

public class AdminCustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AdminCustomerService() {
        this.customerRepository = new CustomerRepository();
        this.accountRepository = new AccountRepository();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAllWithAccount();
    }

    public Customer getCustomerById(int accountId) {
        return customerRepository.findByIdWithAccount(accountId);
    }

    public int createCustomer(String email, String password, String fullName, String phone, String address) {
        if (accountRepository.existsByEmail(email)) {
            return -1; // Email already exists
        }

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        account.setFullName(fullName);
        account.setPhone(phone);
        account.setAddress(address);
        account.setRoleId(RoleConstant.CUSTOMER);
        account.setActive(true);

        int accountId = accountRepository.insert(account);
        if (accountId > 0) {
            customerRepository.insert(accountId);
        }
        return accountId;
    }

   
}
