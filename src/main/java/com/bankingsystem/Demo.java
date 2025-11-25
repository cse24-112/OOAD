package com.bankingsystem;

public class Demo {
    public static void main(String[] args) {
        Bank bank = new Bank("Botswana Bank", "001");

        IndividualCustomer alice = new IndividualCustomer("CUST001", "Alice", "Smith", "NID123");
        alice.setEmployer("Acme Corp", "1 Main St", "FullTime");
        bank.registerCustomer(alice);

        CompanyCustomer acme = new CompanyCustomer("CUST002", "Acme Corp", "REG-987");
        bank.registerCustomer(acme);

        // Open accounts
        bank.openAccount(alice, "savings", 1000.0);
        bank.openAccount(alice, "investment", 1000.0);
        Account c = bank.openAccount(alice, "cheque", 200.0);

        bank.openAccount(acme, "savings", 5000.0);

        System.out.println("--- Before interest ---");
        for (Account a : bank.getAccounts()) {
            System.out.printf("%s : %s = %.2f\n", a.getOwner(), a.getClass().getSimpleName(), a.getBalance());
        }

        bank.applyInterest();

        System.out.println("--- After interest applied ---");
        for (Account a : bank.getAccounts()) {
            System.out.printf("%s : %s = %.2f\n", a.getOwner(), a.getClass().getSimpleName(), a.getBalance());
        }

        // Test deposit/withdraw
        if (c instanceof com.bankingsystem.Withdrawable) {
            System.out.println("Attempting withdraw 50 from cheque account");
            boolean ok = c.withdraw(50);
            System.out.println("Withdraw OK: " + ok + ", balance=" + c.getBalance());
        }

        // Find investment account and test withdrawal
        Account investmentAccount = null;
        for (Account a : bank.getAccounts()) {
            if (a instanceof InvestmentAccount) {
                investmentAccount = a;
                break;
            }
        }
        if (investmentAccount != null) {
            System.out.println("Attempting withdraw 100 from investment account");
            boolean ok = investmentAccount.withdraw(100);
            System.out.println("Withdraw OK: " + ok + ", balance=" + investmentAccount.getBalance());
        }

        System.out.println("Demo finished");
    }
}
