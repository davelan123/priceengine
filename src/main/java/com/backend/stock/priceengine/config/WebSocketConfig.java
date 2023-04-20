package com.backend.stock.priceengine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static com.backend.stock.priceengine.constant.Constant.TOPIC_WEBSOCKET_PRICE_FEED;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	@Autowired
	WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/secured/user/topic");
		config.setUserDestinationPrefix("/secured/user");
		config.setApplicationDestinationPrefixes("/websocket-client");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket/price-feed")
				.addInterceptors(webSocketHandshakeInterceptor)
				.setHandshakeHandler(new MyPrincipalHandshakeHandler())
				.setAllowedOriginPatterns("*").withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {

		/*
		 * 配置消息线程池
		 * 1. corePoolSize 配置核心线程池，当线程数小于此配置时，不管线程中有无空闲的线程，都会产生新线程处理任务
		 * 2. maxPoolSize 配置线程池最大数，当线程池数等于此配置时，不会产生新线程
		 * 3. keepAliveSeconds 线程池维护线程所允许的空闲时间，单位秒
		 */
		registration.taskExecutor().corePoolSize(10)
				.maxPoolSize(20)
				.keepAliveSeconds(60);
		/*
		 * 添加stomp自定义拦截器，可以根据业务做一些处理
		 * springframework 4.3.12 之后版本此方法废弃，代替方法 interceptors(ChannelInterceptor... interceptors)
		 * 消息拦截器，实现ChannelInterceptor接口
		 */
		registration.interceptors(webSocketChannelInterceptor);
	}


	@Autowired
	WebSocketChannelInterceptor webSocketChannelInterceptor;


	/**
	 * 拦截器加入 spring ioc容器
	 * @return
	 */
	//@Bean
	public WebSocketChannelInterceptor webSocketChannelInterceptor()
	{
		return new WebSocketChannelInterceptor();
	}
}
