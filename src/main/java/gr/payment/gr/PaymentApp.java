package gr.payment.gr;


import gr.payment.gr.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static spark.Spark.*;

public class PaymentApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentApp.class);

	private static final PaymentService paymentService;

	static {
		paymentService = new PaymentService();
		paymentService.addAccount("Grisha", new BigDecimal("100"));
		paymentService.addAccount("Petr", new BigDecimal("1.5"));
	}

	public static void main(String[] args) {
		port(8090);
		get("/hello/:name", (req, res) -> {
			LOGGER.info("get hello");
			return "Hello, " + req.params(":name");
		});

		get("/balance/:account", (req, res) -> {
			return paymentService.getBalance(req.params(":name"));
		});

		post("", (req, res) -> {
			paymentService.pay(null);
			return null;
		});
	}
}
