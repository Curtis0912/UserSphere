package com.curtis.userspherebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.curtis.userspherebackend.model.domain.User;
import com.curtis.userspherebackend.model.domain.request.UserLoginRequest;
import com.curtis.userspherebackend.model.domain.request.UserRegisterRequest;
import com.curtis.userspherebackend.service.UserService;
import com.curtis.userspherebackend.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.curtis.userspherebackend.constant.Userconstant.ADMIN_ROLE;
import static com.curtis.userspherebackend.constant.Userconstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    //注册
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    //登录
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        return userService.userLogin(userAccount, userPassword,httpServletRequest);
    }

    //获取用户登录态
    @GetMapping("/current")
    public User getUserCurrent(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            return null;
        }
        long id = currentUser.getId();
        //todo 判断用户是否合法  如有无封号
        User user = userService.getById(id);
        return userService.getSafeUser(user);
    }

    //根据用户查询
    @GetMapping("/search")
    public List<User> searchUser(String username,HttpServletRequest request){
        if(!isAdmin(request)){
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            userQueryWrapper.like("username",username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        //设置返回的password为空   用java8的语法 流式处理将password设置为空，然后设置成列表返回
//        return userList.stream().map(user -> {
//            user.setUserPassword(null);
//            return user;
//        }).collect(Collectors.toList());
        //代码优化
        return userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
    }

    //根据id删除用户
    @PostMapping("/id")
    public boolean deleteUserById(@RequestBody long id,HttpServletRequest request){
        if(!isAdmin(request)){
            return false;
        }
        if(id <= 0){
            return false;
        }
        return userService.removeById(id);
    }

    //判断是否为管理员
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
