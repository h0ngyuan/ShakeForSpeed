package com.sfs.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RewardDTO {
    private Long id;
    private String name;
    private Integer rankStart;
    private Integer rankEnd;
    private MultipartFile image;
}
