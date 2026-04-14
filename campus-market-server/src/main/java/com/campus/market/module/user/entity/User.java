package com.campus.market.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（登录账号）*/
    private String username;

    /** 密码（bcrypt加密）*/
    @TableField(select = false)
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 性别：0=未知 1=男 2=女 */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 所在学校ID */
    private Long schoolId;

    /** 所在校区ID */
    private Long campusId;

    /** 学号（预留校园认证）*/
    private String studentId;

    /** 真实姓名（预留校园认证）*/
    private String realName;

    /** 是否校园认证：0=未认证 1=已认证 */
    private Integer verified;

    /** 状态：0=封禁 1=正常 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 个人简介 */
    private String bio;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
