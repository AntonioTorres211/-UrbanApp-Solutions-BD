package br.com.cidade_atende_mini;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:context-tests;DB_CLOSE_DELAY=-1")
class CidadeAtendeMiniApplicationTests {

	@Test
	void contextLoads() {
	}

}
