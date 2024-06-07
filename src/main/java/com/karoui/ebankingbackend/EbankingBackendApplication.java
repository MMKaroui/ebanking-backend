package com.karoui.ebankingbackend;

import com.karoui.ebankingbackend.dtos.BankAccountDTO;
import com.karoui.ebankingbackend.dtos.CustomerDTO;
import com.karoui.ebankingbackend.entities.*;
import com.karoui.ebankingbackend.enums.AccountStatus;
import com.karoui.ebankingbackend.enums.OperationType;
import com.karoui.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.karoui.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.karoui.ebankingbackend.exceptions.CustomerNotFoundException;
import com.karoui.ebankingbackend.repositories.AccountOperationRepository;
import com.karoui.ebankingbackend.repositories.BankAccountRepository;
import com.karoui.ebankingbackend.repositories.CustomerRepository;
import com.karoui.ebankingbackend.services.BankAccountService;
import com.karoui.ebankingbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }
   @Bean
    CommandLineRunner commandLineRunner (BankAccountService bankAccountService){
        return args->{
Stream.of("hassan", "Imane","Mohamed").forEach(name->{
    CustomerDTO customerDTO = new CustomerDTO ();
    customerDTO.setName(name);
    customerDTO.setEmail(name+"@gmail.com");
    bankAccountService.saveCustomer(customerDTO);
});
    bankAccountService.listCustomers().forEach(customer -> {
        try {
            bankAccountService.saveCurrenAccount(Math.random()*90000,9000,customer.getId());
            bankAccountService.saveSavingAccount(Math.random()*90000,4.5,customer.getId());

            } catch ( CustomerNotFoundException |ClassNotFoundException e) {
            e.printStackTrace();
        }

    });
            List<BankAccountDTO> bankAccounts =bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {

                    bankAccountService.credit(bankAccount.getId(), 10000 + Math.random() * 120000, "Credit");
                    bankAccountService.debit(bankAccount.getId(), 1000+Math.random()*9000,"Debit");

                }

            }
        };
    }
   // @Bean
    CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust->{
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreateAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreateAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);


            });
            bankAccountRepository.findAll().forEach(acc->{
                for (int i = 0; i < 10 ; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()> 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }


            });
        };

    }

}
