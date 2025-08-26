package com.sfs.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RewardVO {
    private Long id;
    private String name;
    private Integer rankStart;
    private Integer rankEnd;
    private String imageUrl;
}
