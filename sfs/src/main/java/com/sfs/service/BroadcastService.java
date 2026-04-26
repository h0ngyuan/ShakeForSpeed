package com.sfs.service;

import com.sfs.websocket.ShakeWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final ShakeWebSocketHandler webSocketHandler;

    public void broadcastRank(String activityId, String rankJson) {
        webSocketHandler.broadcastToActivity(activityId, rankJson);
    }

    public void broadcastSystemMsg(String activityId, String msg) {
        webSocketHandler.broadcastToActivity(activityId, msg);
    }
}
