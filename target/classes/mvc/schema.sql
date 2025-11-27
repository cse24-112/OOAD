-- Schema for Banking System (H2)
-- Production-ready schema with constraints, indexes, and audit fields

CREATE TABLE IF NOT EXISTS customers (
  customer_id VARCHAR(64) PRIMARY KEY,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  national_id VARCHAR(100) UNIQUE,
  customer_type VARCHAR(32) NOT NULL,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(128),
  email VARCHAR(150),
  phone VARCHAR(50),
  company_name VARCHAR(255),
  registration_number VARCHAR(100) UNIQUE,
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
  account_number VARCHAR(64) PRIMARY KEY,
  customer_id VARCHAR(64) NOT NULL,
  account_type VARCHAR(32) NOT NULL,
  balance DOUBLE NOT NULL DEFAULT 0,
  branch VARCHAR(64),
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  date_opened DATE NOT NULL,
  interest_rate DOUBLE,
  min_balance DOUBLE,
  min_opening_deposit DOUBLE,
  employer_name VARCHAR(255),
  employer_address VARCHAR(255),
  overdraft_allowed BOOLEAN DEFAULT FALSE,
  approval_timestamp TIMESTAMP,
  approval_staff_username VARCHAR(100),
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
  transaction_id VARCHAR(64) PRIMARY KEY,
  account_number VARCHAR(64) NOT NULL,
  transaction_type VARCHAR(32) NOT NULL,
  amount DOUBLE NOT NULL,
  balance_after DOUBLE NOT NULL,
  description VARCHAR(255),
  transaction_timestamp TIMESTAMP NOT NULL,
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS staff (
  staff_id VARCHAR(64) PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  role VARCHAR(50),
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_customers_username ON customers(username);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_customer_type ON customers(customer_type);

CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX IF NOT EXISTS idx_accounts_status ON accounts(status);
CREATE INDEX IF NOT EXISTS idx_accounts_date_opened ON accounts(date_opened);

CREATE INDEX IF NOT EXISTS idx_transactions_account_number ON transactions(account_number);
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions(transaction_timestamp);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(transaction_type);

CREATE INDEX IF NOT EXISTS idx_staff_username ON staff(username);