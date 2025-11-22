package com.bankingsystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountRulesTest {

    @Test
    public void savingsCannotWithdraw() {
        IndividualCustomer c = new IndividualCustomer("T1","Sam","Lee","NID1");
        SavingsAccount s = new SavingsAccount(c, "001", 0.0005, 0);
        s.deposit(1000);
        assertFalse(s.withdraw(100));
    }

    @Test
    public void investmentRequiresMinOpening() {
        IndividualCustomer c = new IndividualCustomer("T2","Ann","Doe","NID2");
        boolean ok = InvestmentAccount.meetsOpeningDeposit(400, 500);
        assertFalse(ok);
        ok = InvestmentAccount.meetsOpeningDeposit(500, 500);
        assertTrue(ok);
    }

    @Test
    public void interestAppliedCorrectly() {
        IndividualCustomer c = new IndividualCustomer("T3","Bob","Ray","NID3");
        SavingsAccount s = new SavingsAccount(c, "001", 0.0005, 0);
        s.deposit(1000);
        double interest = s.calculateInterest();
        assertEquals(1000 * 0.0005, interest, 1e-9);

        InvestmentAccount i = new InvestmentAccount(c, "001", 0.05, 500);
        i.deposit(1000);
        double iInterest = i.calculateInterest();
        assertEquals(1000 * 0.05, iInterest, 1e-9);
    }
}
