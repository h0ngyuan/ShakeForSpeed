package com.sfs.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sfs.model.enums.NettyConstants;
import com.sfs.websocket.service.BroadcastService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private BroadcastService broadcastService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            JSONObject json = JSON.parseObject(msg.text());
            String type = json.getString("type");
            if (NettyConstants.SUBSCRIBE.equals(type)){
                String activityId = json.getString(NettyConstants.ACTIVITYID);
                broadcastService.subscribe(activityId,ctx.channel());
            }

        }catch (Exception e){
            ctx.writeAndFlush(new TextWebSocketFrame("{\"error\":\"invalid_request\"}"));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        broadcastService.unsubscribeAll(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
