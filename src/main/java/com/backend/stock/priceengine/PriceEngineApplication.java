package com.backend.stock.priceengine;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@OpenAPIDefinition(
		info = @Info(title = "Websocket for price feed", version = "2.0", description = "Websocket for price feed"))
public class PriceEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceEngineApplication.class, args);
	}

}
