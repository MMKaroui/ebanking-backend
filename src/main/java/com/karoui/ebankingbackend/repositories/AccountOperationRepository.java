package com.karoui.ebankingbackend.repositories;

import com.karoui.ebankingbackend.entities.AccountOperation;
import com.karoui.ebankingbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {
     List<AccountOperation> findByBankAccountId(String accountId );
     Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
}
