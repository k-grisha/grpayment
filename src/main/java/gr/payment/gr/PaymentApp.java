package gr.payment.gr;


import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.dao.impl.AccountDao;
import gr.payment.gr.dao.impl.TransferConsumer;
import gr.payment.gr.dao.impl.TransferProducer;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import gr.payment.gr.service.AccountService;
import gr.payment.gr.service.TransferService;
import gr.payment.gr.utils.DaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static spark.Spark.*;

public class PaymentApp {

	public static void main(String[] args) {
		// Приложение маленькое, не используем никаких DI контейнеров, все соибраем руками.
		AccountRepository accountRepository = new AccountDao(DaoUtil.buildAccountContext());
		accountRepository.save(new AccountEntity("111", "Ivan", new BigDecimal("100")));
		accountRepository.save(new AccountEntity("222", "Grisha", new BigDecimal("200")));
		Queue<TransferEntity> queue = new ArrayBlockingQueue<>(10000);
		TransferRepository transferRepository = new TransferProducer(queue);
		TransferService transferService = new TransferService(accountRepository, transferRepository);
		AccountService accountService = new AccountService(accountRepository);
		AccountController accountController = new AccountController(accountService, transferService);
		new TransferConsumer(queue, accountRepository).start();

		port(8080);
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
