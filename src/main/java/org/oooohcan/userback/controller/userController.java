package org.oooohcan.userback.controller;

import org.oooohcan.userback.common.BaseRespone;
import org.oooohcan.userback.common.ErrorCode;
import org.oooohcan.userback.common.ResultUtils;
import org.oooohcan.userback.exception.BusinessException;
import org.oooohcan.userback.model.domain.User;
import org.oooohcan.userback.model.domain.request.UserLoginRequest;
import org.oooohcan.userback.model.domain.request.UserRegisterRequest;
import org.oooohcan.userback.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.oooohcan.userback.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author oooohcan
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://47.115.230.131"}, allowCredentials = "true")
public class userController {
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public BaseRespone<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"注册请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String inviteCode = userRegisterRequest.getInviteCode();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,inviteCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"信息填入不全");
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword,inviteCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseRespone<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"登录请求为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseRespone<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"注销请求为空");
        }
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseRespone<User> getCurrentUser(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)userObject;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping ("/search")
    public BaseRespone<List<User>> userSearch(String username, HttpServletRequest request){
        List<User> userList = userService.searchUser(username,request);
        return ResultUtils.success(userList);
    }

    @GetMapping("/delete")
    public BaseRespone<Boolean> userDelete(@RequestBody long id, HttpServletRequest request){
        Boolean result = userService.deleteUser(id,request);
        return ResultUtils.success(result);
    }

}
