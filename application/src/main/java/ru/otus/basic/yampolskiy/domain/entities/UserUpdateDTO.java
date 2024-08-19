package ru.otus.basic.yampolskiy.domain.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class UserUpdateDTO {
    private String password;

    public UserUpdateDTO(String password) {
        this.password = password;
    }

    public UserUpdateDTO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
