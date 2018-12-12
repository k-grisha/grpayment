package gr.payment.gr;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dto.AccountDto;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.io.IOException;
import java.util.Random;

public class ControllerTest {

	private static Integer randomPort = 1000 + new Random().nextInt(60000);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static class TestSparkApplication implements SparkApplication {
		@Override
		public void init() {
			PaymentApp.main(null);
		}
	}

	@ClassRule
	public static SparkServer<TestSparkApplication> testServer = new SparkServer<>(TestSparkApplication.class, 8090);


	@Test
	public void getAll() throws Exception {
		GetMethod resp = testServer.get(AccountController.PATH_ACCOUNTS + "111", false);
		HttpResponse execute = testServer.execute(resp);
		AccountDto accountDto = MAPPER.readValue(execute.body(), AccountDto.class);

	}
}
