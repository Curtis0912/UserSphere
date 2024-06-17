package com.curtis.userspherebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.curtis.userspherebackend.common.ErrorCode;
import com.curtis.userspherebackend.exception.BusinessException;
import com.curtis.userspherebackend.model.domain.User;
import com.curtis.userspherebackend.service.UserService;
import com.curtis.userspherebackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.curtis.userspherebackend.constant.Userconstant.USER_LOGIN_STATE;

/**
* @author ZhangJiaHao
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-06-06 17:14:08
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "curtis";




    @Override
    public long userRegister(String userAccount,String userPassword,String checkPassword) {
        //1. 校验
        //判断是否为空
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        //账户  密码   校验密码长度是否符合
        if(userAccount.length() < 4 || userPassword.length() < 8  || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号、密码或校验密码不符合");
        }
        //判断账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}【】‘；：”“’。，、？\\\\]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){//如果找到则返回-1
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //判断密码和校验密码是否相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一致");
        }
        //判断账号是否重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }

        //2. 加密
        String encryPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryPassword);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败");
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        //判断是否为空
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //账户  密码长度是否符合
        if(userAccount.length() < 4 || userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号、密码或校验密码不符合");
        }
        //判断账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}【】‘；：”“’。，、？\\\\]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){//如果找到则返回-1
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //2. 加密
        String encryPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        userQueryWrapper.eq("userPassword",encryPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        if(user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户不存在或密码错误");
        }

        //3. 脱敏
        User safeUser = getSafeUser(user);

        //记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
        return safeUser;

    }
    //用户脱敏
    @Override
    public User getSafeUser(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户不存在");
        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        return user;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




