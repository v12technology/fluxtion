package com.fluxtion.compiler.spring.extern;

import com.fluxtion.runtime.annotations.ExportService;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountNode implements @ExportService Account {

    private double debitAmount;
    private double creditAmount;

    @Override
    public void debit(double debitAmount) {
        this.debitAmount = debitAmount;
    }

    @Override
    public void credit(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    void clearTransaction() {
        creditAmount = Double.NaN;
        debitAmount = Double.NaN;
    }
}
