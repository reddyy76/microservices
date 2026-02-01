package com.example.product_service;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer =
			new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add(
				"spring.data.mongodb.uri",
				mongoDBContainer::getReplicaSetUrl
		);
	}

	@BeforeEach
	void cleanDatabase() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();

		String productRequestString =
				objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/product/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString)
		).andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.count());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("I Phone 17")
				.description("Smart Phone")
				.price(BigDecimal.valueOf(50000))
				.build();
	}
}
