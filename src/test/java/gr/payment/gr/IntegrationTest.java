package gr.payment.gr;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dto.AccountDto;
import gr.payment.gr.dto.TransferDto;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.servlet.SparkApplication;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTest.class);
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
		AccountDto accountDto = get(AccountController.PATH_ACCOUNTS + "111", AccountDto.class);
		Assert.assertEquals("111", accountDto.uid);
	}

	@Test
	public void createAccountTest() throws Exception {
		AccountDto account = new AccountDto("999", "ZZZ", new BigDecimal("100"));
		AccountDto accountDto = get(AccountController.PATH_ACCOUNTS + account.uid, AccountDto.class);
		Assert.assertNull(accountDto);
		String json = MAPPER.writeValueAsString(account);
		post(AccountController.PATH_ACCOUNTS, json, AccountDto.class);
		accountDto = get(AccountController.PATH_ACCOUNTS + account.uid, AccountDto.class);
		Assert.assertNotNull(accountDto);
		Assert.assertEquals(account.uid, accountDto.uid);
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

	@Test
	public void concurrentTest() throws Exception {
		post(AccountController.PATH_ACCOUNTS,
				MAPPER.writeValueAsString(new AccountDto("111", "AAA", new BigDecimal("10000.0"))),
				AccountDto.class);
		post(AccountController.PATH_ACCOUNTS,
				MAPPER.writeValueAsString(new AccountDto("222", "BBB", new BigDecimal("10000.0"))),
				AccountDto.class);
		post(AccountController.PATH_ACCOUNTS,
				MAPPER.writeValueAsString(new AccountDto("333", "CCC", new BigDecimal("10000.0"))),
				AccountDto.class);


		for (int i = 0; i < 50; i++) {
			PayThread pt1 = new PayThread("111", "222", BigDecimal.ONE);
			pt1.start();
			PayThread pt2 = new PayThread("222", "111", BigDecimal.ONE);
			pt2.start();
			PayThread pt3 = new PayThread("333", "111", BigDecimal.ONE);
			pt3.start();
		}

		TimeUnit.MILLISECONDS.sleep(10000);

		AccountDto accountA = get(AccountController.PATH_ACCOUNTS + "111", AccountDto.class);
		AccountDto accountB = get(AccountController.PATH_ACCOUNTS + "222", AccountDto.class);
		AccountDto accountC = get(AccountController.PATH_ACCOUNTS + "333", AccountDto.class);
//		AccountDto accountD = get(AccountController.PATH_ACCOUNTS + "444", AccountDto.class);
		Assert.assertTrue(accountA.balance.compareTo(new BigDecimal("12500.0")) == 0);
		Assert.assertTrue(accountB.balance.compareTo(new BigDecimal("10000.0")) == 0);
		Assert.assertTrue(accountC.balance.compareTo(new BigDecimal("7500.0")) == 0);
//		Assert.assertTrue(accountD.balance.compareTo(new BigDecimal("7500.0"))==0);
		System.out.println(accountA);

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


	final class PayThread extends Thread {
		private String json;

		public PayThread(String from, String to, BigDecimal amount) {
			try {
				json = MAPPER.writeValueAsString(new TransferDto(from, to, amount));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("");
			}
		}

		@Override
		public void run() {
			LOGGER.info(Thread.currentThread().getName() + " start");
			for (int i = 0; i < 50; i++) {
				try {
					post(AccountController.PATH_TRANSFER, json, String.class);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			LOGGER.info(Thread.currentThread().getName() + "finish");
		}
	}

}
