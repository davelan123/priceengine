package com.backend.stock.priceengine.controller;

import com.backend.stock.priceengine.dto.GlobalProperties;
import com.backend.stock.priceengine.dto.StockFeedManagementRequest;
import com.backend.stock.priceengine.service.PriceFeedService;
import com.backend.stock.priceengine.service.impl.StockPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class PriceFeedManagementController {
    @Autowired
    private PriceFeedService priceFeedService;

    @PutMapping("/symbol")
    public void addSymbol(@RequestBody StockFeedManagementRequest stockFeedManagementRequest){
        priceFeedService.addSymbols(stockFeedManagementRequest.getSymbol());
    }

    @DeleteMapping("/symbol")
    public void removeSymbol(@RequestBody StockFeedManagementRequest stockFeedManagementRequest){
        priceFeedService.removeSymbols(stockFeedManagementRequest.getSymbol());
    }

    @GetMapping("/symbols")
    public List<String> getSymbols(){
        return priceFeedService.getSymbols();
    }


}
