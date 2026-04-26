package com.sfs.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.service.RankService;
import com.sfs.util.Result;
import com.sfs.vo.RankItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranks")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/{activityId}/realtime")
    public Result<List<RankItemVO>> realtime(@PathVariable Long activityId,
                                              @RequestParam(defaultValue = "10") int limit) {
        return Result.success(rankService.getRealtimeRank(activityId, limit));
    }
}
