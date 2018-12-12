package gr.payment.gr;


import gr.payment.gr.controller.AccountController;
import gr.payment.gr.dao.impl.AccountH2Dao;
import gr.payment.gr.dao.impl.TransferConsumer;
import gr.payment.gr.dao.impl.TransferProducer;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import gr.payment.gr.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class PaymentApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentApp.class);

	public static void main(String[] args) {
		// Приложение маленькое, не используем никаких DI контейнеров, все соибраем руками.

		AccountH2Dao accountH2Dao = new AccountH2Dao();
		accountH2Dao.save(new AccountEntity("111", "Ivan", new BigDecimal("100.1")));
		accountH2Dao.save(new AccountEntity("222", "Grisha", new BigDecimal("100.2")));
		Queue<TransferEntity>queue = new ArrayBlockingQueue<>(10000);
		TransferProducer transferProducer = new TransferProducer(queue);
		AccountService accountService = new AccountService(accountH2Dao, transferProducer);
		AccountController accountController = new AccountController(accountService);
		new TransferConsumer(queue, accountH2Dao).start();

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
