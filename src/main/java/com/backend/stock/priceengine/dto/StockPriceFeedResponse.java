package com.backend.stock.priceengine.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
public class StockPriceFeedResponse implements Serializable {
    List<StockPriceEntity> stockPriceEntities;
}
