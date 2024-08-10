package ru.otus.basic.yampolskiy.domain.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class UserRegistrationDTO {
    private String login;
    private String password;

    public UserRegistrationDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserRegistrationDTO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
