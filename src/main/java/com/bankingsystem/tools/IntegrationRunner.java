package com.bankingsystem.tools;

import com.bankingsystem.*;
import com.bankingsystem.persistence.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * IntegrationRunner - programmatically performs the full user flow and writes a report.
 */
public class IntegrationRunner {
    public static void main(String[] args) {
        String reportPath = "INTEGRATION_TEST_RESULTS.md";
        StringBuilder log = new StringBuilder();
        log.append("Integration Test Run\n");
        log.append("Started: ").append(LocalDateTime.now()).append("\n\n");

        try (PrintWriter out = new PrintWriter(new FileWriter(reportPath))) {
            // Setup
            log.append("Step 1: Create Bank instance (seeded staff user 'staff1')\n");
            Bank bank = new Bank("AutoTestBank", "AT01");
            log.append("  -> Bank created\n\n");

            // Submit registration
            List<String> empty = new ArrayList<>();
            CustomerRegistrationRequest req = new CustomerRegistrationRequest(
                    "TMPREQ", "individual",
                    "Alice", "Tester", "NID-99999",
                    null, null, null, null, null, null, null,
                    empty, empty
            );
            CustomerRegistrationRequest stored = bank.submitCustomerRegistration(req);
            log.append("Step 2: Submit customer registration -> Request ID: ").append(stored.getId()).append("\n");

            // Approve registration (this should persist via CustomerDAOImpl)
            log.append("Step 3: Approve registration via Bank.approveCustomerRegistration(...)\n");
            boolean approved = bank.approveCustomerRegistration(stored.getId(), "staff1", "alice_user", "alice_pass", "Approved by auto-run");
            log.append("  -> Approval result: ").append(approved).append("\n");

            // Verify persisted customer
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            Optional<Customer> dbCustomer = customerDAO.findCustomerByUsername("alice_user");
            log.append("Step 4: Verify customer persisted via CustomerDAOImpl.findCustomerByUsername('alice_user') -> present: ")
                    .append(dbCustomer.isPresent()).append("\n");

            // Find in-memory customer in bank
            Customer inBank = bank.getCustomers().stream().filter(c -> c instanceof IndividualCustomer)
                    .filter(c -> "alice_user".equals(((IndividualCustomer) c).getUsername()))
                    .findFirst().orElse(null);
            log.append("  -> In-memory customer found in Bank: ").append(inBank != null).append("\n");

            // Open account
            log.append("Step 5: Open savings account for customer with initial deposit 500.0\n");
            Account account = bank.openAccount(inBank, "savings", 500.0);
            log.append("  -> Account created (object present): ").append(account != null).append("; status: ").append(account != null ? account.getStatus() : "N/A").append("\n");

            // Approve account (assign account number)
            log.append("Step 6: Approve account (assign number)\n");
            if (account != null) {
                account.approve("staff1");
                log.append("  -> Account approved; number: ").append(account.getAccountNumber()).append("\n");
            }

            // Persist account via DAO
            AccountDAOImpl accountDAO = new AccountDAOImpl();
            boolean savedAccount = account != null && accountDAO.saveAccount(account);
            log.append("Step 7: Persist approved account via AccountDAOImpl.saveAccount(...) -> result: ").append(savedAccount).append("\n");

            // Persist transaction
            log.append("Step 8: Create and persist a deposit transaction of 100.0\n");
            Transaction deposit = new Transaction(Transaction.Type.DEPOSIT, 100.0, account != null ? account.getBalance() : 0.0, "Auto deposit");
            TransactionDAOImpl txnDAO = new TransactionDAOImpl();
            boolean txSaved = txnDAO.saveTransaction(account.getAccountNumber(), deposit);
            log.append("  -> Transaction saved: ").append(txSaved).append("\n");

            // Verify transaction retrieval
            List<Transaction> txns = txnDAO.getTransactionsByAccountNumber(account.getAccountNumber());
            log.append("Step 9: Retrieve transactions for account ").append(account.getAccountNumber()).append(" -> count: ").append(txns.size()).append("\n");

            // Summarize DB counts using DAOs
            List<Customer> allCustomers = customerDAO.getAllCustomers();
            List<Account> allAccounts = accountDAO.getAllAccounts();
            List<Transaction> allTx = txnDAO.getAllTransactions();
            log.append("\nSummary of persistence state:\n");
            log.append("  - customers table rows: ").append(allCustomers.size()).append("\n");
            log.append("  - accounts table rows: ").append(allAccounts.size()).append("\n");
            log.append("  - transactions table rows: ").append(allTx.size()).append("\n");

            log.append("\nIntegration run completed: ").append(LocalDateTime.now()).append("\n");

            // Write report
            out.println(log.toString());
            System.out.println("Integration run complete. Report written to: " + reportPath);
        } catch (Exception e) {
            System.err.println("Integration run failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                com.bankingsystem.persistence.DatabaseConnection.getInstance().closeConnection();
            } catch (Exception ignore) {
            }
        }
    }
}
