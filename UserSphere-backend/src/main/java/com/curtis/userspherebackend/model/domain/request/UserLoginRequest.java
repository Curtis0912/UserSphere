package com.curtis.userspherebackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3191210229362410379L;

    private String userAccount;
    private String userPassword;

}
