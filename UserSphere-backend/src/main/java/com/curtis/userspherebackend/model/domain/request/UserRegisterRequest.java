package com.curtis.userspherebackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191210229362410379L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
