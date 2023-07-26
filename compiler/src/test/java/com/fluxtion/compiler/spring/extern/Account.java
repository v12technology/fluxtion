package com.fluxtion.compiler.spring.extern;

public interface Account {
    void debit(double debitAmount);

    void credit(double creditAmount);
}
