package gr.payment.gr.service;

import gr.payment.gr.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

	private Map<String, BigDecimal> map = new ConcurrentHashMap<>();

	public BigDecimal getBalance(String account) {
		return map.get(account);
	}

	public void pay(PaymentDto paymentDto) {
		BigDecimal from = map.get(paymentDto.from);
		BigDecimal to = map.get(paymentDto.to);
		from = from.subtract(paymentDto.value);
		to = to.add(paymentDto.value);

	}

	public void addAccount(String account, BigDecimal balance) {
		map.put(account, balance);
	}
}
