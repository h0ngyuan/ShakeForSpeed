package com.sfs.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.dto.JoinRoomDTO;
import com.sfs.service.RoomService;
import com.sfs.util.Result;
import com.sfs.vo.RoomStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/join")
    public Result<RoomStatusVO> join(@RequestBody JoinRoomDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(roomService.joinRoom(userId, dto));
    }

    @PostMapping("/leave")
    public Result<Void> leave(@RequestParam Long activityId) {
        Long userId = StpUtil.getLoginIdAsLong();
        roomService.leaveRoom(userId, activityId);
        return Result.success();
    }

    @GetMapping("/{code}/status")
    public Result<RoomStatusVO> status(@PathVariable String code) {
        return Result.success(roomService.getRoomStatus(code));
    }
}
