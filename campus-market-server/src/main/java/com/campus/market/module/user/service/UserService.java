package com.campus.market.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.module.user.dto.LoginDTO;
import com.campus.market.module.user.dto.RegisterDTO;
import com.campus.market.module.user.dto.UpdateProfileDTO;
import com.campus.market.module.user.entity.User;
import com.campus.market.module.user.vo.LoginVO;
import com.campus.market.module.user.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /** 注册 */
    LoginVO register(RegisterDTO dto, String clientIp);

    /** 登录 */
    LoginVO login(LoginDTO dto, String clientIp);

    /** 获取当前登录用户信息 */
    UserVO getCurrentUser();

    /** 更新用户资料 */
    UserVO updateProfile(UpdateProfileDTO dto);

    /** 更新头像 */
    UserVO updateAvatar(String avatarUrl);

    /** 将 User 转为 VO */
    UserVO toVO(User user);
}
