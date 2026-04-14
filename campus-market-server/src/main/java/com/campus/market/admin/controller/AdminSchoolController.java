package com.campus.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.school.entity.Campus;
import com.campus.market.module.school.entity.School;
import com.campus.market.module.school.mapper.CampusMapper;
import com.campus.market.module.school.mapper.SchoolMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台学校/校区管理
 */
@Tag(name = "后台-学校管理", description = "学校和校区 CRUD")
@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminSchoolController {

    private final SchoolMapper schoolMapper;
    private final CampusMapper campusMapper;

    // ===== 学校 =====

    @Operation(summary = "学校列表")
    @GetMapping("/schools")
    public R<PageVO<School>> listSchools(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize) {
        IPage<School> page = schoolMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<School>().orderByAsc(School::getSort));
        return R.ok(PageVO.of(page));
    }

    @Operation(summary = "新增学校")
    @PostMapping("/schools")
    public R<School> addSchool(@RequestBody @Valid SchoolRequest req) {
        School school = new School();
        school.setName(req.getName());
        school.setCity(req.getCity());
        school.setProvince(req.getProvince());
        school.setSort(req.getSort() != null ? req.getSort() : 0);
        school.setStatus(1);
        schoolMapper.insert(school);
        return R.ok(school);
    }

    @Operation(summary = "更新学校")
    @PutMapping("/schools/{id}")
    public R<Void> updateSchool(@PathVariable Long id, @RequestBody SchoolRequest req) {
        School school = schoolMapper.selectById(id);
        if (school == null) return R.fail("学校不存在");
        if (req.getName() != null) school.setName(req.getName());
        if (req.getCity() != null) school.setCity(req.getCity());
        if (req.getProvince() != null) school.setProvince(req.getProvince());
        if (req.getSort() != null) school.setSort(req.getSort());
        if (req.getStatus() != null) school.setStatus(req.getStatus());
        schoolMapper.updateById(school);
        return R.ok();
    }

    @Operation(summary = "删除学校")
    @DeleteMapping("/schools/{id}")
    public R<Void> deleteSchool(@PathVariable Long id) {
        schoolMapper.deleteById(id);
        return R.ok();
    }

    // ===== 校区 =====

    @Operation(summary = "校区列表（按学校）")
    @GetMapping("/campuses")
    public R<List<Campus>> listCampuses(@RequestParam Long schoolId) {
        return R.ok(campusMapper.selectList(
                new LambdaQueryWrapper<Campus>()
                        .eq(Campus::getSchoolId, schoolId)
                        .orderByAsc(Campus::getSort)));
    }

    @Operation(summary = "新增校区")
    @PostMapping("/campuses")
    public R<Campus> addCampus(@RequestBody @Valid CampusRequest req) {
        Campus campus = new Campus();
        campus.setSchoolId(req.getSchoolId());
        campus.setName(req.getName());
        campus.setAddress(req.getAddress());
        campus.setSort(req.getSort() != null ? req.getSort() : 0);
        campus.setStatus(1);
        campusMapper.insert(campus);
        return R.ok(campus);
    }

    @Operation(summary = "更新校区")
    @PutMapping("/campuses/{id}")
    public R<Void> updateCampus(@PathVariable Long id, @RequestBody CampusRequest req) {
        Campus campus = campusMapper.selectById(id);
        if (campus == null) return R.fail("校区不存在");
        if (req.getName() != null) campus.setName(req.getName());
        if (req.getAddress() != null) campus.setAddress(req.getAddress());
        if (req.getSort() != null) campus.setSort(req.getSort());
        if (req.getStatus() != null) campus.setStatus(req.getStatus());
        campusMapper.updateById(campus);
        return R.ok();
    }

    @Operation(summary = "删除校区")
    @DeleteMapping("/campuses/{id}")
    public R<Void> deleteCampus(@PathVariable Long id) {
        campusMapper.deleteById(id);
        return R.ok();
    }

    @Data
    public static class SchoolRequest {
        @NotBlank private String name;
        private String city;
        private String province;
        private Integer sort;
        private Integer status;
    }

    @Data
    public static class CampusRequest {
        private Long schoolId;
        @NotBlank private String name;
        private String address;
        private Integer sort;
        private Integer status;
    }
}
