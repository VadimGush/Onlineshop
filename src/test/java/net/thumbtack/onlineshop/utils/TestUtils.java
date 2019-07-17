package net.thumbtack.onlineshop.utils;

import net.thumbtack.onlineshop.dto.AdminDto;
import net.thumbtack.onlineshop.dto.ClientDto;

public class TestUtils {

    public static AdminDto createAdminEditDto(
            String firstName,
            String lastName,
            String patronymic,
            String position,
            String oldPassword,
            String newPassword
    ) {
        AdminDto adminEdit = new AdminDto();
        adminEdit.setFirstName(firstName);
        adminEdit.setLastName(lastName);
        adminEdit.setPatronymic(patronymic);
        adminEdit.setPosition(position);
        adminEdit.setOldPassword(oldPassword);
        adminEdit.setNewPassword(newPassword);
        return adminEdit;
    }

    public static ClientDto createClientEditDto(
            String firstName,
            String lastName,
            String patronymic,
            String email,
            String address,
            String phone,
            String oldPassword,
            String newPassword
    ) {
        ClientDto client = new ClientDto();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setPatronymic(patronymic);
        client.setEmail(email);
        client.setAddress(address);
        client.setPhone(phone);
        client.setOldPassword(oldPassword);
        client.setNewPassword(newPassword);
        return client;
    }

}
