package com.sfs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@TableName("reward")
@Builder
public class Reward {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String rewardName;
    //考虑到现阶段没必要也没财力保存至阿里云OSS，图片先存放到MinIO了，然后这个IMG实际上是URL，保存的是图片存放地址
    private String rewardImg;
    //绑上的活动ID
    private Long activityId;
    //排名区间
    private Integer rankRangeFront;
    private Integer rankRangeEnd;

}
