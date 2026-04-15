package com.campus.market.module.message.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageDTO;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.message.entity.ProductMessage;
import com.campus.market.module.message.mapper.ProductMessageMapper;
import com.campus.market.module.product.mapper.ProductMapper;
import com.campus.market.module.product.entity.Product;
import com.campus.market.module.user.mapper.UserMapper;
import com.campus.market.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品留言接口
 */
@Tag(name = "留言", description = "商品留言接口")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class MessageController {

    private final ProductMessageMapper messageMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Operation(summary = "获取商品留言列表（公开）")
    @GetMapping("/{productId}/messages")
    public R<PageVO<MessageVO>> list(@PathVariable Long productId, PageDTO pageDTO) {
        IPage<ProductMessage> page = messageMapper.selectPage(
                new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()),
                new LambdaQueryWrapper<ProductMessage>()
                        .eq(ProductMessage::getProductId, productId)
                        .eq(ProductMessage::getParentId, 0L)
                        .eq(ProductMessage::getStatus, 1)
                        .orderByDesc(ProductMessage::getCreatedAt));

        List<MessageVO> voList = page.getRecords().stream()
                .map(msg -> toVO(msg))
                .collect(Collectors.toList());

        // 批量查询当前商品所有二级回复，按 parentId 分组后挂载
        List<ProductMessage> replyRecords = messageMapper.selectList(
                new LambdaQueryWrapper<ProductMessage>()
                        .eq(ProductMessage::getProductId, productId)
                        .ne(ProductMessage::getParentId, 0L)
                        .eq(ProductMessage::getStatus, 1)
                        .orderByAsc(ProductMessage::getCreatedAt));

        Map<Long, List<MessageVO>> replyMap = replyRecords.stream()
                .map(this::toVO)
                .collect(Collectors.groupingBy(MessageVO::getParentId));

        voList.forEach(vo -> vo.setReplies(replyMap.getOrDefault(vo.getId(), Collections.emptyList())));

        return R.ok(PageVO.of(page, voList));
    }

    @Operation(summary = "发布留言（需登录）")
    @PostMapping("/{productId}/messages")
    public R<MessageVO> publish(@PathVariable Long productId,
                                 @RequestBody @Valid MessageRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();

        ProductMessage message = new ProductMessage();
        message.setProductId(productId);
        message.setUserId(userId);
        message.setParentId(req.getParentId() != null ? req.getParentId() : 0L);
        message.setContent(req.getContent());
        message.setStatus(1);
        messageMapper.insert(message);

        // 更新留言计数
        Product product = productMapper.selectById(productId);
        if (product != null) {
            Product update = new Product();
            update.setId(productId);
            update.setMessageCount(product.getMessageCount() + 1);
            productMapper.updateById(update);
        }

        return R.ok(toVO(message));
    }

    private MessageVO toVO(ProductMessage msg) {
        MessageVO vo = new MessageVO();
        vo.setId(msg.getId());
        vo.setProductId(msg.getProductId());
        vo.setParentId(msg.getParentId());
        vo.setContent(msg.getContent());
        vo.setCreatedAt(msg.getCreatedAt());

        User user = userMapper.selectById(msg.getUserId());
        if (user != null) {
            vo.setUserId(user.getId());
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        return vo;
    }

    @Data
    public static class MessageRequest {
        @NotBlank(message = "留言内容不能为空")
        @Size(max = 500, message = "留言最多500字")
        private String content;
        private Long parentId;
    }

    @Data
    public static class MessageVO {
        private Long id;
        private Long productId;
        private Long parentId;
        private Long userId;
        private String userNickname;
        private String userAvatar;
        private String content;
        private java.time.LocalDateTime createdAt;
        private List<MessageVO> replies = new ArrayList<>();
    }
}
