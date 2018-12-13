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
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;

public class PaymentApp {

	public static void main(String[] args) {
		// instead DI framework
		AccountRepository accountRepository = new AccountDao(DaoUtil.buildAccountContext());
		// test data
		accountRepository.save(new AccountEntity("111", "AAA", new BigDecimal("100")));
		accountRepository.save(new AccountEntity("222", "BBB", new BigDecimal("200")));
		Queue<TransferEntity> queue = new ArrayBlockingQueue<>(100000);
		TransferRepository transferRepository = new TransferProducer(queue);
		TransferService transferService = new TransferService(accountRepository, transferRepository);
		AccountService accountService = new AccountService(accountRepository);
		AccountController accountController = new AccountController(accountService, transferService);
		new TransferConsumer(queue, accountRepository).start();

		port(8080);
		post(AccountController.PATH_TRANSFER, accountController.transfer());
		get(AccountController.PATH_ACCOUNTS, accountController.getAll());
		post(AccountController.PATH_ACCOUNTS, accountController.create());
		get(AccountController.PATH_ACCOUNT_UID, accountController.getByUid());

		exception(PaymentException.class, (exception, request, response) -> {
			response.body(exception.getMessage());
			response.status(400);
		});

		notFound((req, res) -> "\"Not found (code 404)\"");

		internalServerError((req, res) -> "\"Internal server error (code 500) \"");

		after("*", (Request request, Response response) -> response.type("application/json"));
	}
}
