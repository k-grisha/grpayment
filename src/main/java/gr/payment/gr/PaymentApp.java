package gr.payment.gr;


import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.impl.AccountH2Dao;
import gr.payment.gr.dao.impl.AccountMapDao;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;

import static spark.Spark.*;

public class PaymentApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentApp.class);

	public static void main(String[] args) {
		// Приложение маленькое, не используем никаких DI контейнеров, все соибраем руками.

		AccountH2Dao jooqDao = new AccountH2Dao();
		jooqDao.save(new AccountEntity("111", "Ivan", new BigDecimal("100.1")));
		jooqDao.save(new AccountEntity("222", "Grisha", new BigDecimal("100.2")));

		final AccountRepository accountRepository = new AccountMapDao();
		accountRepository.save(new AccountEntity("111", "Ivan", new BigDecimal("100.1")));
		accountRepository.save(new AccountEntity("222", "Grisha", new BigDecimal("100.2")));
		accountRepository.save(new AccountEntity("333", "Petr", new BigDecimal("100.3")));
		final AccountService accountService = new AccountService(jooqDao);
		final AccountController accountController = new AccountController(accountService);


		port(8090);
		post(AccountController.PATH_TRANSFER, accountController.transfer());
		get(AccountController.PATH_ACCOUNTS, accountController.getAll());
		get(AccountController.PATH_ACCOUNT_UID, accountController.getByUid());

		exception(PaymentException.class, (exception, request, response) -> {
			response.body(exception.getMessage());
			response.status(400);
		});

		after("*", (Request request, Response response) -> response.type("application/json"));

	}
}
