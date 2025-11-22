package com.bankingsystem.persistence;

import com.google.gson.*;
import com.bankingsystem.Account;
import com.bankingsystem.Customer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonFactory {
    public static Gson create() {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        b.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDate.parse(json.getAsString());
            }
        });
        b.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        b.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDateTime.parse(json.getAsString());
            }
        });

        // Polymorphic adapter for Account and Customer
        JsonSerializer<Object> ser = (src, typeOfSrc, context) -> {
            JsonObject obj = context.serialize(src).getAsJsonObject();
            obj.addProperty("_class", src.getClass().getName());
            return obj;
        };
        JsonDeserializer<Object> deser = (json, typeOfT, context) -> {
            JsonObject obj = json.getAsJsonObject();
            JsonElement clsEl = obj.remove("_class");
            if (clsEl == null) return null;
            String className = clsEl.getAsString();
            try {
                Class<?> cls = Class.forName(className);
                return context.deserialize(obj, cls);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        };

        b.registerTypeHierarchyAdapter(Account.class, new JsonSerializer<Account>() {
            public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
                return ser.serialize(src, typeOfSrc, context);
            }
        });
        b.registerTypeHierarchyAdapter(Account.class, new JsonDeserializer<Account>() {
            public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return (Account) deser.deserialize(json, typeOfT, context);
            }
        });

        b.registerTypeHierarchyAdapter(Customer.class, new JsonSerializer<Customer>() {
            public JsonElement serialize(Customer src, Type typeOfSrc, JsonSerializationContext context) {
                return ser.serialize(src, typeOfSrc, context);
            }
        });
        b.registerTypeHierarchyAdapter(Customer.class, new JsonDeserializer<Customer>() {
            public Customer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return (Customer) deser.deserialize(json, typeOfT, context);
            }
        });

        return b.setPrettyPrinting().create();
    }
}
