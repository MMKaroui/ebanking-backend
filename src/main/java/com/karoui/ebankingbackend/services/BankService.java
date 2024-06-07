package com.karoui.ebankingbackend.services;

import com.karoui.ebankingbackend.entities.BankAccount;
import com.karoui.ebankingbackend.entities.CurrentAccount;
import com.karoui.ebankingbackend.entities.SavingAccount;
import com.karoui.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void consulter(){
        BankAccount bankAccount=
                bankAccountRepository.findById("2941aeb8-b443-49d0-bf7d-d222b4cc9d9b").orElse(null);
        if (bankAccount!=null) {
            System.out.println("************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getCreateAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount) {

                System.out.println("Over Draft=>" + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("Rate=>" + ((SavingAccount) bankAccount).getInterestRate());
            }
            System.out.println("************************");
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("======================");
                System.out.println(op.getType() + "\t" + op.getAmount() + "\t" + op.getOperationDate());

            });
        }
    }

}
