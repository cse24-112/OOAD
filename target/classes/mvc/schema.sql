-- Schema for Banking System (H2)
CREATE TABLE IF NOT EXISTS customers (
  customer_id VARCHAR(64) PRIMARY KEY,
  firstname VARCHAR(100),
  surname VARCHAR(100),
  address VARCHAR(255),
  email VARCHAR(150),
  phone VARCHAR(50),
  customer_type VARCHAR(32),
  password VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS accounts (
  account_id VARCHAR(64) PRIMARY KEY,
  account_number VARCHAR(128) UNIQUE,
  customer_id VARCHAR(64),
  account_type VARCHAR(32),
  balance DOUBLE,
  branch VARCHAR(64),
  employer_name VARCHAR(255),
  employer_address VARCHAR(255),
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS transactions (
  transaction_id VARCHAR(64) PRIMARY KEY,
  account_id VARCHAR(64),
  type VARCHAR(32),
  amount DOUBLE,
  timestamp TIMESTAMP,
  FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

CREATE TABLE IF NOT EXISTS staff (
  staff_id VARCHAR(64) PRIMARY KEY,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(128)
);
