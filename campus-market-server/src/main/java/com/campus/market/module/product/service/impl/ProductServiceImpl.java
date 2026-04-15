package com.campus.market.module.product.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.ResultCode;
import com.campus.market.module.category.entity.ProductCategory;
import com.campus.market.module.category.mapper.ProductCategoryMapper;
import com.campus.market.module.product.dto.ProductPublishDTO;
import com.campus.market.module.product.dto.ProductQueryDTO;
import com.campus.market.module.product.entity.Product;
import com.campus.market.module.product.entity.ProductImage;
import com.campus.market.module.product.mapper.ProductImageMapper;
import com.campus.market.module.product.mapper.ProductMapper;
import com.campus.market.module.product.service.ProductService;
import com.campus.market.module.product.vo.ProductListVO;
import com.campus.market.module.product.vo.ProductVO;
import com.campus.market.module.school.entity.Campus;
import com.campus.market.module.school.entity.School;
import com.campus.market.module.school.mapper.CampusMapper;
import com.campus.market.module.school.mapper.SchoolMapper;
import com.campus.market.module.favorite.entity.Favorite;
import com.campus.market.module.favorite.mapper.FavoriteMapper;
import com.campus.market.module.user.entity.User;
import com.campus.market.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductImageMapper productImageMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final SchoolMapper schoolMapper;
    private final CampusMapper campusMapper;
    private final ProductCategoryMapper categoryMapper;

    private static final String[] CONDITION_DESCS = {"", "全新", "几乎全新", "轻微使用", "明显使用", "有瑕疵"};
    private static final String[] TRADE_TYPE_DESCS = {"", "线下交易", "快递邮寄", "均可"};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO publish(ProductPublishDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setUserId(userId);
        product.setStatus(0);       // 待审核
        product.setAuditStatus(0);  // 待审核
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setMessageCount(0);
        product.setIsTop(0);
        save(product);

        // 保存商品图片
        saveImages(product.getId(), dto.getImages());

        return toDetailVO(getById(product.getId()));
    }

    @Override
    public PageVO<ProductListVO> pageList(ProductQueryDTO dto) {
        LambdaQueryWrapper<Product> wrapper = buildQueryWrapper(dto);
        // 公开列表只显示已上架
        wrapper.eq(Product::getStatus, 1);

        IPage<Product> page = page(
                new Page<>(dto.getPageNum(), dto.getPageSize()),
                wrapper);

        List<ProductListVO> voList = page.getRecords().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        return PageVO.of(page, voList);
    }

    @Override
    public ProductVO getDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 增加浏览次数
        Product update = new Product();
        update.setId(id);
        update.setViewCount(product.getViewCount() + 1);
        updateById(update);
        product.setViewCount(product.getViewCount() + 1);

        return toDetailVO(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO update(Long id, ProductPublishDTO dto) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        checkOwner(product);

        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        // 重新提交审核
        product.setStatus(0);
        product.setAuditStatus(0);
        product.setAuditRemark(null);
        updateById(product);
        // updateById 默认跳过 null 字段，需显式将 auditRemark 清空
        update(new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, id)
                .set(Product::getAuditRemark, null));

        // 重新保存图片
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, id));
        saveImages(id, dto.getImages());

        return toDetailVO(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        checkOwner(product);
        removeById(id);
    }

    @Override
    public void offShelf(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        checkOwner(product);
        Product update = new Product();
        update.setId(id);
        update.setStatus(2); // 已下架
        updateById(update);
    }

    @Override
    public void markSold(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        checkOwner(product);
        Product update = new Product();
        update.setId(id);
        update.setStatus(3); // 已售出
        updateById(update);
    }

    @Override
    public PageVO<ProductListVO> myProducts(ProductQueryDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getUserId, userId)
                .orderByDesc(Product::getCreatedAt);

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(Product::getTitle, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Product::getStatus, dto.getStatus());
        }

        IPage<Product> page = page(
                new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        List<ProductListVO> voList = page.getRecords().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        return PageVO.of(page, voList);
    }

    @Override
    public ProductListVO toListVO(Product product) {
        ProductListVO vo = new ProductListVO();
        BeanUtils.copyProperties(product, vo);
        vo.setConditionDesc(getConditionDesc(product.getConditionLevel()));

        // 填充学校/校区名称
        if (product.getSchoolId() != null) {
            School school = schoolMapper.selectById(product.getSchoolId());
            if (school != null) vo.setSchoolName(school.getName());
        }
        if (product.getCampusId() != null) {
            Campus campus = campusMapper.selectById(product.getCampusId());
            if (campus != null) vo.setCampusName(campus.getName());
        }

        // 填充发布者信息
        User user = userMapper.selectById(product.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }

    @Override
    public ProductVO toDetailVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        vo.setConditionDesc(getConditionDesc(product.getConditionLevel()));
        vo.setTradeTypeDesc(getTradeTypeDesc(product.getTradeType()));

        // 商品图片
        List<ProductImage> images = productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, product.getId())
                        .orderByAsc(ProductImage::getSort));
        vo.setImages(images.stream().map(ProductImage::getUrl).collect(Collectors.toList()));

        // 分类名称
        if (product.getCategoryId() != null) {
            ProductCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) vo.setCategoryName(category.getName());
        }

        // 学校/校区名称
        if (product.getSchoolId() != null) {
            School school = schoolMapper.selectById(product.getSchoolId());
            if (school != null) vo.setSchoolName(school.getName());
        }
        if (product.getCampusId() != null) {
            Campus campus = campusMapper.selectById(product.getCampusId());
            if (campus != null) vo.setCampusName(campus.getName());
        }

        // 发布者信息
        User user = userMapper.selectById(product.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        // 是否已收藏（查询当前登录用户的收藏记录）
        vo.setFavorited(false);
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            long favCount = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, product.getId()));
            vo.setFavorited(favCount > 0);
        }

        return vo;
    }

    // ===== 私有方法 =====

    private void saveImages(Long productId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage img = new ProductImage();
            img.setProductId(productId);
            img.setUrl(imageUrls.get(i));
            img.setSort(i);
            images.add(img);
        }
        for (ProductImage image : images) {
            productImageMapper.insert(image);
        }
    }

    private void checkOwner(Product product) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_OWNER);
        }
    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(ProductQueryDTO dto) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(Product::getTitle, dto.getKeyword());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, dto.getCategoryId());
        }
        if (dto.getSchoolId() != null) {
            wrapper.eq(Product::getSchoolId, dto.getSchoolId());
        }
        if (dto.getCampusId() != null) {
            wrapper.eq(Product::getCampusId, dto.getCampusId());
        }
        if (dto.getMinPrice() != null) {
            wrapper.ge(Product::getPrice, dto.getMinPrice());
        }
        if (dto.getMaxPrice() != null) {
            wrapper.le(Product::getPrice, dto.getMaxPrice());
        }
        if (dto.getConditionLevel() != null) {
            wrapper.eq(Product::getConditionLevel, dto.getConditionLevel());
        }
        if (dto.getTradeType() != null) {
            wrapper.eq(Product::getTradeType, dto.getTradeType());
        }

        // 排序
        if ("price_asc".equals(dto.getSortBy())) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(dto.getSortBy())) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getIsTop).orderByDesc(Product::getCreatedAt);
        }

        return wrapper;
    }

    private String getConditionDesc(Integer level) {
        if (level == null || level < 1 || level >= CONDITION_DESCS.length) return "";
        return CONDITION_DESCS[level];
    }

    private String getTradeTypeDesc(Integer type) {
        if (type == null || type < 1 || type >= TRADE_TYPE_DESCS.length) return "";
        return TRADE_TYPE_DESCS[type];
    }
}
