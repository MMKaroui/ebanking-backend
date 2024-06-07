package com.karoui.ebankingbackend.services;


import com.karoui.ebankingbackend.dtos.*;
import com.karoui.ebankingbackend.entities.BankAccount;
import com.karoui.ebankingbackend.entities.CurrentAccount;
import com.karoui.ebankingbackend.entities.Customer;
import com.karoui.ebankingbackend.entities.SavingAccount;
import com.karoui.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.karoui.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.karoui.ebankingbackend.exceptions.CustomerNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BankAccountService {
      CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
      List<CustomerDTO> listCustomers();
      CustomerDTO saveCustomer (CustomerDTO customerDTO);
      CustomerDTO updateCustomer(CustomerDTO customerDTO);
      void deleteCustomer(Long customerId);

      BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
      List<BankAccountDTO> bankAccountList();
      CurrentBankAccountDTO saveCurrenAccount (double initialBalance, double  overDraft, Long customerId) throws ClassNotFoundException, CustomerNotFoundException;
      SavingBankAccountDTO saveSavingAccount (double initialBalance, double  InterestRate, Long customerId) throws ClassNotFoundException, CustomerNotFoundException;

      void debit (String accountId,double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
      void credit (String accountId, double amount, String description) throws BankAccountNotFoundException;
      void transfer (String accountIdSource, String accountIdDestination, double amount ) throws BankAccountNotFoundException, BalanceNotSufficientException;


      List<AccountOperationDTO> accountHistory(@PathVariable String accountId);

      AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

      List<CustomerDTO> searchCustomers(String keyword);
}
