package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 重置密码
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/21 23:13
 */
@Data
public class UserResetRequest {

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
