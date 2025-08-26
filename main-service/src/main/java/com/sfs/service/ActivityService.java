package com.sfs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfs.model.dto.ActivityInfoDTO;
import com.sfs.model.dto.CreateActivityDTO;
import com.sfs.model.dto.QueryActivityDTO;
import com.sfs.model.entity.Activity;
import com.sfs.utils.result.Result;

public interface ActivityService extends IService<Activity> {


    Result queryActivities(QueryActivityDTO queryActivityDTO);

    void createActivity(CreateActivityDTO createActivityDTO) throws Exception;

    Result deleteById(Long id);
    
    /**
     * 根据ID获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    Result getActivityById(Long id);

    Result joinActivity(ActivityInfoDTO activityInfoDTO) throws Exception;

    void exitActivity(ActivityInfoDTO activityInfoDTO);
}
