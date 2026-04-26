package com.sfs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinRoomDTO {
    @NotBlank(message = "房间码不能为空")
    private String roomCode;
    private String roomPwd;
}
