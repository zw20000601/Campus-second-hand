package com.campus.market.module.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.common.result.R;
import com.campus.market.module.school.entity.Campus;
import com.campus.market.module.school.entity.School;
import com.campus.market.module.school.mapper.CampusMapper;
import com.campus.market.module.school.mapper.SchoolMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校/校区接口（公开）
 */
@Tag(name = "学校/校区", description = "学校和校区列表（公开接口）")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolMapper schoolMapper;
    private final CampusMapper campusMapper;

    @Operation(summary = "获取所有学校列表")
    @GetMapping("/schools")
    public R<List<School>> listSchools() {
        List<School> list = schoolMapper.selectList(
                new LambdaQueryWrapper<School>()
                        .eq(School::getStatus, 1)
                        .orderByAsc(School::getSort));
        return R.ok(list);
    }

    @Operation(summary = "获取学校下的校区列表")
    @GetMapping("/campuses")
    public R<List<Campus>> listCampuses(@RequestParam Long schoolId) {
        List<Campus> list = campusMapper.selectList(
                new LambdaQueryWrapper<Campus>()
                        .eq(Campus::getSchoolId, schoolId)
                        .eq(Campus::getStatus, 1)
                        .orderByAsc(Campus::getSort));
        return R.ok(list);
    }
}
