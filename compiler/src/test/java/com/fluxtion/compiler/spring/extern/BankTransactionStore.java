package com.fluxtion.compiler.spring.extern;

import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;

@Data
public class BankTransactionStore {
    private AccountNode accountNode;

    @OnTrigger
    public boolean updateAccounts() {
        System.out.println("updating account:" + accountNode);
        return false;
    }
}
