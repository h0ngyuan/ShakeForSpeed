package com.sfs.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.dto.ActivityQueryDTO;
import com.sfs.dto.CreateActivityDTO;
import com.sfs.service.ActivityService;
import com.sfs.util.Result;
import com.sfs.vo.ActivityDetailVO;
import com.sfs.vo.ActivityVO;
import com.sfs.entity.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public Result<ActivityVO> create(@Valid @RequestBody CreateActivityDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(activityService.createActivity(userId, dto));
    }

    @GetMapping("/{id}")
    public Result<ActivityDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(activityService.getDetail(id));
    }

    @GetMapping
    public Result<PageResult<ActivityVO>> list(ActivityQueryDTO query) {
        return Result.success(activityService.queryActivities(query));
    }

    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        activityService.publishActivity(id);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    public Result<Void> finish(@PathVariable Long id) {
        activityService.finishActivity(id);
        return Result.success();
    }
}
