package org.oooohcan.userback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.oooohcan.userback.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author oooohcan
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-07-04 21:19:45
*/


public interface UserService extends IService<User> {
    /**
     *  用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param inviteCode    邀请码
     * @return   新用户的id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String inviteCode);

    /**
     *  用户登录
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param request
     * @return  脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     */
    int userLogout(HttpServletRequest request);

    /**
     * 搜索用户
     *
     * @param username  用户昵称
     * @param request
     * @return  搜索后的用户列表
     */
    List<User> searchUser(String username, HttpServletRequest request);

    /**
     * 删除用户
     *
     * @param id    用户id
     * @param request
     * @return  删除结果
     */
    boolean deleteUser(long id,HttpServletRequest request);



    /**
     * 用户脱敏
     *
     * @param originUser
     * @return  脱敏后的用户信息
     */
    User getSafetyUser(User originUser);

    /**
     * 是否为管理员
     *
     * @param request
     * @return  判断结果
     */
    boolean isAdmin(HttpServletRequest request);

}
