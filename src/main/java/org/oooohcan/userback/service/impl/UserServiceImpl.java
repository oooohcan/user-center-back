package org.oooohcan.userback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.oooohcan.userback.common.ErrorCode;
import org.oooohcan.userback.exception.BusinessException;
import org.oooohcan.userback.model.domain.User;
import org.oooohcan.userback.service.UserService;
import org.oooohcan.userback.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.oooohcan.userback.constant.UserConstant.*;

/**
* @author oooohcan
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-07-04 21:19:45
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆原密码
     */
    private static final String SALT = "SydZh";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String inviteCode) {
        //1、校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,inviteCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(!ArrayUtils.contains(INVITE_CODE_LIST,inviteCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无效邀请码");
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userPassword);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户包含特殊字符");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }
        //密码与校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不同");
        }
        //2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setInviteCode(inviteCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"数据插入失败");
        }
        return 1;
    }

    @Override
    public User userLogin(String userAccount, String userPassword,HttpServletRequest request) {
        //1、校验
        if(StringUtils.isAnyBlank(userAccount,userPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码为空");
        if(userAccount.length() < 4)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4位");
        if(userPassword.length() < 8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于8位");
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userPassword);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户包含特殊字符");
        }
        //2、密码明文加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
            //查询用户是否存，密码是否准确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
            //用户不存在
        if(user == null){
            log.info("user login failed, userAccount can not match userPassword ");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        //3、用户信息脱敏
        User saftyUser = getSafetyUser(user);

        //4、保存用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,saftyUser);

        return saftyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUser(String username, HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(long id,HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        if(id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"Id小于0");
        }
        return this.removeById(id);
    }


    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            return null;
        }
        User saftyUser = new User();
        saftyUser.setId(originUser.getId());
        saftyUser.setUsername(originUser.getUsername());
        saftyUser.setUserAccount(originUser.getUserAccount());
        saftyUser.setAvatarUrl(originUser.getAvatarUrl());
        saftyUser.setGender(originUser.getGender());
        saftyUser.setPhone(originUser.getPhone());
        saftyUser.setEmail(originUser.getEmail());
        saftyUser.setUserStatus(originUser.getUserStatus());
        saftyUser.setUserRole(originUser.getUserRole());
        saftyUser.setCreateTime(originUser.getCreateTime());
        saftyUser.setInviteCode(originUser.getInviteCode());
        return saftyUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //用户鉴权，仅管理员可操作
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}




