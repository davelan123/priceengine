package com.backend.stock.priceengine.intializeEvent;

import com.backend.stock.priceengine.service.PriceFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class StartupApplicationPriceServiceTask implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ExecutorService executorService;
    @Autowired
    private PriceFeedService priceFeedService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("priceFeedService intialized");
        priceFeedService.uploadPriceFeed();
        //executorService.submit(priceFeedService);
    }
}