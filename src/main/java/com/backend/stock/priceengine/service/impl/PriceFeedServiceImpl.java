package com.backend.stock.priceengine.service.impl;

import com.backend.stock.priceengine.dto.GlobalProperties;
import com.backend.stock.priceengine.dto.StockPriceEntity;
import com.backend.stock.priceengine.dto.StockPriceFeedResponse;
import com.backend.stock.priceengine.service.PriceFeedService;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.backend.stock.priceengine.constant.Constant.TOPIC_WEBSOCKET_PRICE_FEED;

@Service
@Slf4j
public class PriceFeedServiceImpl implements PriceFeedService {

    private static final String API_ENDPOINT = "https://query1.finance.yahoo.com/v7/finance/quote?lang=ko-KR&region=KR&corsDomain=finance.yahoo.com";
    public final long FREQUENCY = 1000;
    private final List<String> symbols = Collections.synchronizedList(new ArrayList<String>());
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    public GlobalProperties globalProperties;
    @Autowired
    private StockPriceService stockPriceService;

    @Override
    public void uploadPriceFeed(){
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
            symbols.add("AAL");
            while (true) {
                if (!CollectionUtils.isEmpty(symbols)) {
                    String symbolListInStr = symbols.stream().collect(Collectors.joining(","));
                    HttpGet request = new HttpGet(API_ENDPOINT + "&symbols="
                            + URLEncoder.encode(symbolListInStr, StandardCharsets.UTF_8));
                    HttpResponse result = httpClient.execute(request);
                    String json = EntityUtils.toString(result.getEntity());
                    List<StockPriceEntity> stockPriceEntities = getStockPriceEntityList(new JSONObject(json));
                    Set<String> sessionIds = globalProperties.getSessionIds();
                    if (!CollectionUtils.isEmpty(sessionIds)) {
                        for (String sessionId:sessionIds
                             ) {
                            log.info("sessionId : {}", sessionId);
                            stockPriceService.sendPrice(
                                    StockPriceFeedResponse.builder().stockPriceEntities(stockPriceEntities).build(),
                                    TOPIC_WEBSOCKET_PRICE_FEED,
                                    sessionId
                            );
                            //log.info("stockPriceEntities [{}] message sent ", new Gson().toJson(stockPriceEntities));
                        }
                    }

                }
                try{
                    Thread.sleep(FREQUENCY);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private StockPriceEntity getFakeStockPriceFeedResponse(){
        Faker faker = new Faker();

        return StockPriceEntity.builder()
                .marketState("TradingHours")
                .delayTime("1000")
                .regularMarketChangePercent(0.300)
                .symbol("AAL")
                .regularMarketPrice(14.6)
                .regularMarketDayLow(13.8)
                .regularMarketDayHigh(14.8)
                .build();
    }


    private List<StockPriceEntity> getStockPriceEntityList(JSONObject jsonObject){
        List<StockPriceEntity> stockPriceEntities = new ArrayList<>();
        log.info("jsonObject : {}", jsonObject);
        JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");
        result = sort(result);
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);

            String symbol = data.optString("symbol");
            String marketState = data.optString("marketState");
            long regularMarketTime = data.optLong("regularMarketTime");

            double regularMarketPrice = data.optDouble("regularMarketPrice");
            double regularMarketDayHigh = data.optDouble("regularMarketDayHigh");
            double regularMarketDayLow = data.optDouble("regularMarketDayLow");
            double regularMarketChange = data.optDouble("regularMarketChange");
            double regularMarketChangePercent = data.optDouble("regularMarketChangePercent");
            String delayTime = prettyTime(currentTimeMillis - (regularMarketTime * 1000));
            StockPriceEntity stockPriceEntity = StockPriceEntity.builder()
                    .symbol(symbol)
                    .regularMarketPrice(regularMarketPrice)
                    .regularMarketDayHigh(regularMarketDayHigh)
                    .regularMarketDayLow(regularMarketDayLow)
                    .regularMarketChange(regularMarketChange)
                    .regularMarketChangePercent(regularMarketChangePercent)
                    .delayTime(delayTime)
                    .marketState(marketState)
                    .regularMarketTime(regularMarketTime)
                    .build();
            stockPriceEntities.add(stockPriceEntity);
        }
        return stockPriceEntities;
    }

    private String prettyTime(long millis){
        return String.format("%dm, %ds",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private JSONArray sort(JSONArray result){
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        jsonValues.sort((a, b) -> {
            double valA = a.optDouble("regularMarketChangePercent");
            double valB = b.optDouble("regularMarketChangePercent");
            return Double.compare(valB, valA);
        });

        for (JSONObject jsonValue : jsonValues) {
            sortedJsonArray.put(jsonValue);
        }

        return sortedJsonArray;
    }


    @Override
    public void addSymbols(String symbol){
        symbols.add(symbol);
    }

    @Override
    public void removeSymbols(String symbol){
        symbols.remove(symbol);
    }

    @Override
    public List<String> getSymbols(){
        return symbols;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run(){
        this.uploadPriceFeed();
    }
}
