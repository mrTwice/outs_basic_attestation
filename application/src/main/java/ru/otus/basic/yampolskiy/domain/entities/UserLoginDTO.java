package ru.otus.basic.yampolskiy.domain.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class UserLoginDTO {
    private String login;
    private String password;

    public UserLoginDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserLoginDTO() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
