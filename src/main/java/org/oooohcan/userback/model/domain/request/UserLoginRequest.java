package org.oooohcan.userback.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author oooohcan
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5894280542314451634L;

    private String userAccount;
    private String userPassword;
}

