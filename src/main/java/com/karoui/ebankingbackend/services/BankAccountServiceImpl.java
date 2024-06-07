package com.karoui.ebankingbackend.services;


import com.karoui.ebankingbackend.dtos.*;
import com.karoui.ebankingbackend.entities.*;
import com.karoui.ebankingbackend.enums.AccountStatus;
import com.karoui.ebankingbackend.enums.OperationType;
import com.karoui.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.karoui.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.karoui.ebankingbackend.exceptions.CustomerNotFoundException;
import com.karoui.ebankingbackend.mappers.BankAccountMapperImpl;
import com.karoui.ebankingbackend.repositories.AccountOperationRepository;
import com.karoui.ebankingbackend.repositories.BankAccountRepository;
import com.karoui.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
  //Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {

       Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found"));
              return    dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer savedCustomer = customerRepository.save( dtoMapper.fromCustomerDTO(customerDTO));
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrenAccount(double initialBalance, double overDraft, Long customerId) throws  CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreateAt(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);

        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingAccount(double initialBalance, double InterestRate, Long customerId) throws  CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreateAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setInterestRate(InterestRate);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);

        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }




    @Override
    public List<CustomerDTO> listCustomers() {

        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer ->
                dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());

       /* List<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer:customers){
            CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }*/

        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount instanceof  SavingAccount){
            SavingAccount savingAccount =(SavingAccount) bankAccount;
            return  dtoMapper.fromSavingBankAccount(savingAccount);
        } else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }


    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
            AccountOperation accountOperation = new AccountOperation();
            accountOperation.setType(OperationType.DEBIT);
            accountOperation.setAmount(amount);
            accountOperation.setOperationDate(new Date());
            accountOperation.setDescription(description);
            accountOperation.setBankAccount(bankAccount);
            accountOperationRepository.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance()-amount);
            bankAccountRepository.save(bankAccount);

           }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws  BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"transfer from "+ accountIdSource);
    }
    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream()
                .map(bankAccount ->{
        if (bankAccount instanceof  SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
             return  dtoMapper.fromSavingBankAccount(savingAccount);

        } else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
           return dtoMapper.fromCurrentBankAccount(currentAccount) ;}
               }).collect(Collectors.toList());
        return bankAccountDTOS;

        }





    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO){
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer updateCustomer =customerRepository.save(customer);
        return dtoMapper.fromCustomer(updateCustomer);
            }

    @Override
    public void deleteCustomer(Long customerId){

        customerRepository.deleteById(customerId);
    }

   @Override
    public List<AccountOperationDTO> accountHistory(@PathVariable String accountId){
        List<AccountOperation> accountOperations =accountOperationRepository.findByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream()
                .map(accountOperation ->
                        dtoMapper.fromAccountOperation(accountOperation))
                .collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount ==null) throw new BankAccountNotFoundException("Account not Found");

       Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page,size));
       AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return  accountHistoryDTO;


    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
      List<Customer> customers = customerRepository.findByNameContains(keyword);
      List<CustomerDTO> customerDTOS = customers.stream().map(customer ->
          dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
      return customerDTOS;

    }

}
