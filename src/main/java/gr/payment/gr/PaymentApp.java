package gr.payment.gr;


import com.google.gson.Gson;
import gr.payment.gr.dao.AccountDao;
import gr.payment.gr.dao.impl.AccountDaoMap;
import gr.payment.gr.dto.TransferDto;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static spark.Spark.*;

public class PaymentApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentApp.class);

	public static void main(String[] args) {
		final AccountDao accountDao = new AccountDaoMap();
		accountDao.save(new AccountEntity("111", "Ivan", new BigDecimal("100.1")));
		accountDao.save(new AccountEntity("222", "Grisha", new BigDecimal("100.2")));
		accountDao.save(new AccountEntity("333", "Petr", new BigDecimal("100.3")));
		final AccountService accountService = new AccountService(accountDao);

		final Gson gson = new Gson();

		port(8090);

		get("/accounts", (request, response) -> {
			response.type("application/json");
			return gson.toJson(accountService.getAll());
		});

		get("/accounts/:uid", (request, response) -> {
			response.type("application/json");
			return gson.toJson(accountService.getBy(request.params(":uid")));
		});

		post("/accounts/transfer", (request, response) -> {

			TransferDto transferDto = gson.fromJson(request.body(), TransferDto.class);
			accountService.transfer(transferDto);
//			gson.toJson(accountService.getAll());
//			response.type("application/json");

			return null;
		});
//		get("/:account/balance", (req, res) -> {
//			return paymentService.getBalance(req.params(":account"));
//		});
//
//		post("", (req, res) -> {
//			paymentService.pay(null);
//			return null;
//		});
	}
}
