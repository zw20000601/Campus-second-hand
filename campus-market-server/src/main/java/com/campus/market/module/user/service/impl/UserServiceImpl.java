package com.campus.market.module.user.service.impl;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.result.ResultCode;
import com.campus.market.module.school.entity.Campus;
import com.campus.market.module.school.entity.School;
import com.campus.market.module.school.mapper.CampusMapper;
import com.campus.market.module.school.mapper.SchoolMapper;
import com.campus.market.module.user.dto.LoginDTO;
import com.campus.market.module.user.dto.RegisterDTO;
import com.campus.market.module.user.dto.UpdateProfileDTO;
import com.campus.market.module.user.entity.User;
import com.campus.market.module.user.mapper.UserMapper;
import com.campus.market.module.user.service.UserService;
import com.campus.market.module.user.vo.LoginVO;
import com.campus.market.module.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final SchoolMapper schoolMapper;
    private final CampusMapper campusMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto, String clientIp) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXIST);
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setSchoolId(dto.getSchoolId());
        user.setCampusId(dto.getCampusId());
        user.setVerified(0);
        user.setStatus(1);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(clientIp);
        save(user);

        // 执行登录
        StpUtil.login(user.getId(), SaLoginConfig.setExtra("type", "user"));

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUser(toVO(user));
        return vo;
    }

    @Override
    public LoginVO login(LoginDTO dto, String clientIp) {
        // 查找用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新登录信息
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginAt(LocalDateTime.now());
        updateUser.setLastLoginIp(clientIp);
        updateById(updateUser);

        // 执行登录
        StpUtil.login(user.getId(), SaLoginConfig.setExtra("type", "user"));

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUser(toVO(user));
        return vo;
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(UpdateProfileDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getSchoolId() != null) user.setSchoolId(dto.getSchoolId());
        if (dto.getCampusId() != null) user.setCampusId(dto.getCampusId());

        updateById(user);
        return toVO(getById(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateAvatar(String avatarUrl) {
        Long userId = StpUtil.getLoginIdAsLong();
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAvatar(avatarUrl);
        updateById(updateUser);
        return toVO(getById(userId));
    }

    @Override
    public UserVO toVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 填充学校名称
        if (user.getSchoolId() != null) {
            School school = schoolMapper.selectById(user.getSchoolId());
            if (school != null) {
                vo.setSchoolName(school.getName());
            }
        }

        // 填充校区名称
        if (user.getCampusId() != null) {
            Campus campus = campusMapper.selectById(user.getCampusId());
            if (campus != null) {
                vo.setCampusName(campus.getName());
            }
        }

        return vo;
    }
}
