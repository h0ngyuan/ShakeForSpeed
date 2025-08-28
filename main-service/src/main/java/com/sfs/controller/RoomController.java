package com.sfs.controller;


import com.sfs.service.RoomService;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("/room")
@ResponseBody
public class RoomController {

    @Autowired
    private RoomService roomService;


    @PostMapping("/start")
    public Result startActivity(@RequestBody Long activityId) throws Exception {
        return roomService.startActivity(activityId);
    }

}
