package net.thumbtack.onlineshop.database.models;

public class AccountFactory {

    public static Account createAdmin(
            String firstName,
            String secondName,
            String thirdName,
            String profession,
            String login,
            String password
    ) {
        return new Account(firstName, secondName, thirdName, profession, login, password);
    }

    public static Account createAdmin(
            String firstName,
            String secondName,
            String profession,
            String login,
            String password
    ) {
        return new Account(firstName, secondName, profession, login, password);
    }

    public static Account createClient(
            String firstName,
            String secondName,
            String thirdName,
            String email,
            String postAddress,
            String phone,
            String login,
            String password
    ) {
        return new Account(firstName, secondName, thirdName, email, postAddress, phone, login, password);
    }

    public static Account createClient(
            String firstName,
            String secondName,
            String email,
            String postAddress,
            String phone,
            String login,
            String password
    ) {
        return new Account(firstName, secondName, email, postAddress, phone, login, password);
    }

}
