package com.backend.stock.priceengine.config;

import com.backend.stock.priceengine.dto.GlobalProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <websocket通讯拦截器>
 * <建立websocket连接前后的业务处理>
 *
 * @author wzh
 * @version 2018-07-21 20:05
 * @see [相关类/方法] (可选)
 */
@Slf4j
@Component
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Autowired
    GlobalProperties globalProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler webSocketHandler, Map<String, Object> map)
            throws Exception{
        // websocket握手建立前调用，获取httpsession
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            if (request instanceof ServletServerHttpRequest) {
                HttpSession httpSession = servletRequest.getServletRequest().getSession();
                // 这里打印一下session id 方便等下对比和springMVC获取到httpsession是不是同一个
                log.info("httpSession key：" + httpSession.getId());
                // 获取到httpsession后，可以根据自身业务，操作其中的信息，这里只是单纯的和websocket进行关联
                map.put("HTTP_SESSION", httpSession);
            }
            return true;

//
//            // 这里从request中获取session,获取不到不创建，可以根据业务处理此段
//            HttpSession httpSession = servletRequset.getServletRequest().getSession(false);
//            if (httpSession != null)
//            {
//                // 这里打印一下session id 方便等下对比和springMVC获取到httpsession是不是同一个
//                log.info("httpSession key：" + httpSession.getId());
//                globalProperties.addTransactionAndSession(httpSession.getId(), httpSession.getId());
//                // 获取到httpsession后，可以根据自身业务，操作其中的信息，这里只是单纯的和websocket进行关联
//                map.put("HTTP_SESSION",httpSession);
//
//            }
//            else
//            {
//                log.warn("httpSession is null");
//            }
        }

        // 调用父类方法
        return super.beforeHandshake(request, response, webSocketHandler, map);
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler, Exception e){
        // websocket握手建立后调用
        log.info("websocket连接握手成功");
    }
}