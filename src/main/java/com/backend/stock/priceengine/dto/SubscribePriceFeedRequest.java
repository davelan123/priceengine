package com.backend.stock.priceengine.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class SubscribePriceFeedRequest implements Serializable {
    private String transactionId;
    private String symbol;
}
