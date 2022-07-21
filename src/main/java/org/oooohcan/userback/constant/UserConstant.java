package org.oooohcan.userback.constant;

public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    // -----权限------
    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;
    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 邀请码，用于校验用户注册
     */
    String [] INVITE_CODE_LIST = {"x123","ha002","p15d","n3cc"};
}
