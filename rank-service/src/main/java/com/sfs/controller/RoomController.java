package com.sfs.controller;


import com.sfs.service.RoomService;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/room")
@ResponseBody
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/join/{id}")
    public Result joinRoom

}
