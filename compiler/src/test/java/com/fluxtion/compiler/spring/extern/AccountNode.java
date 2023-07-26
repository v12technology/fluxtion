package com.fluxtion.compiler.spring.extern;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.callback.ExportFunctionNode;
import lombok.ToString;

@ToString
public class AccountNode extends ExportFunctionNode implements @ExportService Account {

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

    public double getDebitAmount() {
        return debitAmount;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    void clearTransaction() {
        creditAmount = Double.NaN;
        debitAmount = Double.NaN;
    }
}
