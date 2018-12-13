package gr.payment.gr;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dto.AccountDto;
import gr.payment.gr.dto.TransferDto;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IntegrationTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static class TestSparkApplication implements SparkApplication {
		@Override
		public void init() {
			PaymentApp.main(null);
		}
	}

	@ClassRule
	public static SparkServer<TestSparkApplication> testServer = new SparkServer<>(TestSparkApplication.class, 8080);


	@Test
	public void getAllAccountsTest() throws Exception {
		List<AccountDto> accounts = get(AccountController.PATH_ACCOUNTS, new TypeReference<List<AccountDto>>() {
		});

		Assert.assertEquals(2, accounts.size());
	}

	@Test
	public void getAccountByUidTest() throws Exception {
		GetMethod resp = testServer.get(AccountController.PATH_ACCOUNTS + "111", false);
		HttpResponse execute = testServer.execute(resp);
		AccountDto accountDto = MAPPER.readValue(execute.body(), AccountDto.class);
		Assert.assertEquals("111", accountDto.uid);
	}

	@Test
	public void transferMoneyTest() throws Exception {
		AccountDto accountA = get(AccountController.PATH_ACCOUNTS + "111", AccountDto.class);
		AccountDto accountB = get(AccountController.PATH_ACCOUNTS + "222", AccountDto.class);
		Assert.assertTrue(accountA.balance.compareTo(new BigDecimal("100")) == 0);
		Assert.assertTrue(accountB.balance.compareTo(new BigDecimal("200")) == 0);

		String json = MAPPER.writeValueAsString(new TransferDto("111", "222", new BigDecimal("10.0")));
		String transferUid = post(AccountController.PATH_TRANSFER, json, String.class);
		Assert.assertNotNull(transferUid);

		TimeUnit.MILLISECONDS.sleep(1000);

		accountA = get(AccountController.PATH_ACCOUNTS + "111", AccountDto.class);
		accountB = get(AccountController.PATH_ACCOUNTS + "222", AccountDto.class);
		Assert.assertTrue(accountA.balance.compareTo(new BigDecimal("90")) == 0);
		Assert.assertTrue(accountB.balance.compareTo(new BigDecimal("210")) == 0);
	}

	private <T> T get(String path, Class<T> clazz) throws Exception {
		GetMethod resp = testServer.get(path, false);
		HttpResponse execute = testServer.execute(resp);
		return MAPPER.readValue(execute.body(), clazz);
	}

	private <T> T get(String path, TypeReference type) throws Exception {
		GetMethod resp = testServer.get(path, false);
		HttpResponse execute = testServer.execute(resp);
		return MAPPER.readValue(execute.body(), type);
	}

	private <T> T post(String path, String body, Class<T> clazz) throws Exception {
		PostMethod resp = testServer.post(path, body, false);
		HttpResponse execute = testServer.execute(resp);
		return MAPPER.readValue(execute.body(), clazz);
	}


}
