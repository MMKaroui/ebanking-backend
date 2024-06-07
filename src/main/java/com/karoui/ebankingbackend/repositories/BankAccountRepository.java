package com.karoui.ebankingbackend.repositories;

import com.karoui.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
    List<BankAccount> findBankAccountByCustomer_Id( String customerId);
}
