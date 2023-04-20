package com.backend.stock.priceengine.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
public class StockPriceEntity implements Serializable {
    private String symbol;
    private String marketState;
    private long regularMarketTime;
    private double regularMarketPrice;
    private double regularMarketDayHigh;
    private double regularMarketDayLow;
    private double regularMarketChange;
    private double regularMarketChangePercent;
    private String delayTime;



}
