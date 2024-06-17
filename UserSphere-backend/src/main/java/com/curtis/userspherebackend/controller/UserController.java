package com.curtis.userspherebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.curtis.userspherebackend.common.BaseResponse;
import com.curtis.userspherebackend.common.ErrorCode;
import com.curtis.userspherebackend.common.ResultUtils;
import com.curtis.userspherebackend.exception.BusinessException;
import com.curtis.userspherebackend.model.domain.User;
import com.curtis.userspherebackend.model.domain.request.UserLoginRequest;
import com.curtis.userspherebackend.model.domain.request.UserRegisterRequest;
import com.curtis.userspherebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
//            return ResultUtils.error(ErrorCode.NULL_ERROR);
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户信息不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    //登录
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户信息不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }

    //注销
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest){
        if(httpServletRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(httpServletRequest);
        return ResultUtils.success(result);
    }

    //获取用户登录态
    @GetMapping("/current")
    public BaseResponse<User> getUserCurrent(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long id = currentUser.getId();
        //todo 判断用户是否合法  如有无封号
        User user = userService.getById(id);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }

    //根据用户查询
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username,HttpServletRequest request){
        if(!isAdmin(request)){
//            return new ArrayList<>();
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
        List<User> list = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    //根据id删除用户
    @PostMapping("/id")
    public BaseResponse<Boolean> deleteUserById(@RequestBody long id, HttpServletRequest request){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    //判断是否为管理员
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
