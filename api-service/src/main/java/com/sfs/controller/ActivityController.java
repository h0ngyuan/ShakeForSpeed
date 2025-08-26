package com.sfs.controller;

import com.sfs.model.dto.CreateActivityDTO;
import com.sfs.model.dto.QueryActivityDTO;
import com.sfs.model.dto.RewardDTO;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private RewardService rewardService;

    @PostMapping("/queryActivities")
    public Result queryActivities(@RequestBody QueryActivityDTO queryActivityDTO){
        return activityService.queryActivities(queryActivityDTO);
    }

    @PostMapping(path = "/createActivity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createActivity(MultipartHttpServletRequest request) throws Exception {
        CreateActivityDTO createActivityDTO = getCreateActivityDTO(request);
        activityService.createActivity(createActivityDTO);
    }

    @PostMapping("/deleteActivity")
    public Result deleteActivity(@RequestParam("id") Long id){

        return activityService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Result getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }











    private static CreateActivityDTO getCreateActivityDTO(MultipartHttpServletRequest request) {
        // 创建DTO对象
        CreateActivityDTO createActivityDTO = new CreateActivityDTO();

        // 设置基本字段
        createActivityDTO.setActivityName(request.getParameter("activityName"));

        String beginTimeStr = request.getParameter("beginTime");

        if (beginTimeStr != null && !beginTimeStr.isEmpty()) {
            if (beginTimeStr.endsWith("Z")) {
                beginTimeStr = beginTimeStr.substring(0, beginTimeStr.length() - 1);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime beginTime = LocalDateTime.parse(beginTimeStr, formatter);
            createActivityDTO.setBeginTime(beginTime);
        }

        String durTimeStr = request.getParameter("durTime");
        if (durTimeStr != null && !durTimeStr.isEmpty()) {
            createActivityDTO.setDurTime(Long.parseLong(durTimeStr));
        }

        String longitudeStr = request.getParameter("longitude");
        if (longitudeStr != null && !longitudeStr.isEmpty()) {
            createActivityDTO.setLongitude(Double.parseDouble(longitudeStr));
        }

        String latitudeStr = request.getParameter("latitude");
        if (latitudeStr != null && !latitudeStr.isEmpty()) {
            createActivityDTO.setLatitude(Double.parseDouble(latitudeStr));
        }

        // 处理奖励列表
        List<RewardDTO> rewards = new ArrayList<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        // 找出最大的奖励索引
        int maxRewardIndex = -1;
        for (String paramName : parameterMap.keySet()) {
            if (paramName.startsWith("rewards[") && paramName.contains("]")) {
                try {
                    int index = Integer.parseInt(paramName.substring(7, paramName.indexOf(']')));
                    maxRewardIndex = Math.max(maxRewardIndex, index);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // 构建每个奖励对象
        for (int i = 0; i <= maxRewardIndex; i++) {
            RewardDTO rewardDTO = new RewardDTO();
            rewardDTO.setName(request.getParameter("rewards[" + i + "].name"));

            String rankStartStr = request.getParameter("rewards[" + i + "].rankStart");
            if (rankStartStr != null && !rankStartStr.isEmpty()) {
                rewardDTO.setRankStart(Integer.parseInt(rankStartStr));
            }

            String rankEndStr = request.getParameter("rewards[" + i + "].rankEnd");
            if (rankEndStr != null && !rankEndStr.isEmpty()) {
                rewardDTO.setRankEnd(Integer.parseInt(rankEndStr));
            }

            // 处理奖励图片
            MultipartFile image = request.getFile("rewards[" + i + "].image");
            if (image != null && !image.isEmpty()) {
                rewardDTO.setImage(image);
            }

            rewards.add(rewardDTO);
        }

        createActivityDTO.setRewards(rewards);
        return createActivityDTO;
    }

}
