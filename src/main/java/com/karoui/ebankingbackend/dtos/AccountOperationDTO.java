package com.karoui.ebankingbackend.dtos;

import com.karoui.ebankingbackend.entities.BankAccount;
import com.karoui.ebankingbackend.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
public class AccountOperationDTO {

    private long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;

}
