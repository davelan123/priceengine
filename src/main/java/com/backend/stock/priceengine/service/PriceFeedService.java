package com.backend.stock.priceengine.service;

import java.util.List;

public interface PriceFeedService extends Runnable{
    void uploadPriceFeed();
    void addSymbols(String symbol);
    void removeSymbols(String symbol);
    List<String> getSymbols();
}
