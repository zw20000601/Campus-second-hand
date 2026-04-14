package com.campus.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.user.entity.User;
import com.campus.market.module.user.mapper.UserMapper;
import com.campus.market.module.user.service.UserService;
import com.campus.market.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台用户管理接口
 */
@Tag(name = "后台-用户管理", description = "用户封禁/查询")
@Validated
@RestController
@RequestMapping("/admin/api/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(summary = "用户列表")
    @GetMapping
    public R<PageVO<UserVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword);
        }
        if (status != null) wrapper.eq(User::getStatus, status);

        IPage<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<UserVO> voList = page.getRecords().stream()
                .map(userService::toVO)
                .collect(Collectors.toList());
        return R.ok(PageVO.of(page, voList));
    }

    @Operation(summary = "封禁/解封用户")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id,
                                @RequestParam @NotNull @Min(0) @Max(1) Integer status) {
        User update = new User();
        update.setId(id);
        update.setStatus(status);
        userMapper.updateById(update);
        return R.ok();
    }
}
