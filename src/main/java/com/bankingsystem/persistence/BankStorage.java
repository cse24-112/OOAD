package com.bankingsystem.persistence;

import com.google.gson.Gson;
import com.bankingsystem.Bank;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class BankStorage {
    private final Gson gson;
    private final File file;

    public BankStorage(File file) {
        this.file = file;
        this.gson = GsonFactory.create();
    }

    public void save(Bank bank) throws Exception {
        try (FileWriter w = new FileWriter(file)) {
            gson.toJson(bank, w);
        }
    }

    public Bank load() throws Exception {
        if (!file.exists()) return null;
        try (FileReader r = new FileReader(file)) {
            return gson.fromJson(r, Bank.class);
        }
    }
}
