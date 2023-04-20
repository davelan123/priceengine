package com.backend.stock.priceengine.config;

import com.backend.stock.priceengine.dto.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
public class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {


    public static final String HTTP_SESSION_ID_ATTR_NAME = "HTTP.SESSION.ID";
    private final Collection<String> attributeNames;
    private boolean copyAllAttributes;
    private boolean copyHttpSessionId = true;
    private boolean createSession;

    public HttpSessionHandshakeInterceptor() {
        this.attributeNames = Collections.emptyList();
        this.copyAllAttributes = true;
    }

    public HttpSessionHandshakeInterceptor(Collection<String> attributeNames) {
        this.attributeNames = Collections.unmodifiableCollection(attributeNames);
        this.copyAllAttributes = false;
    }

    public Collection<String> getAttributeNames() {
        return this.attributeNames;
    }

    public void setCopyAllAttributes(boolean copyAllAttributes) {
        this.copyAllAttributes = copyAllAttributes;
    }

    public boolean isCopyAllAttributes() {
        return this.copyAllAttributes;
    }

    public void setCopyHttpSessionId(boolean copyHttpSessionId) {
        this.copyHttpSessionId = copyHttpSessionId;
    }

    public boolean isCopyHttpSessionId() {
        return this.copyHttpSessionId;
    }

    public void setCreateSession(boolean createSession) {
        this.createSession = createSession;
    }

    public boolean isCreateSession() {
        return this.createSession;
    }

    // 获取HttpSession
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)request;
            return serverRequest.getServletRequest().getSession(this.isCreateSession());
        } else {
            return null;
        }
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map attributes) throws Exception{
        if (request instanceof ServletServerHttpRequest) {
            HttpSession session = this.getSession(request);
            if (session != null) {
                if (this.isCopyHttpSessionId()) {
                    // 保存 sessionid
                    attributes.put("HTTP.SESSION.ID", session.getId());
                }

                Enumeration names = session.getAttributeNames();

                while(true) {
                    String name;
                    do {
                        if (!names.hasMoreElements()) {
                            return true;
                        }

                        name = (String)names.nextElement();
                    } while(!this.isCopyAllAttributes() && !this.getAttributeNames().contains(name));
                    // 保存HttpSession中的信息
                    attributes.put(name, session.getAttribute(name));
                }
            } else {
                return true;
            }

        }
        return true;
    }

    /**
     * Invoked after the handshake is done. The response status and headers indicate
     * the results of the handshake, i.e. whether it was successful or not.
     *
     * @param request   the current request
     * @param response  the current response
     * @param wsHandler the target WebSocket handler
     * @param exception an exception raised during the handshake, or {@code null} if none
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){
        log.info("finish handshake");
    }
}