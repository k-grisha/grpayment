package gr.payment.gr.service;

import gr.payment.gr.dto.TransferDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

	private Map<String, BigDecimal> map = new ConcurrentHashMap<>();

	public BigDecimal getBalance(String account) {
		return map.get(account);
	}

	public void pay(TransferDto transferDto) {
		BigDecimal from = map.get(transferDto.from);
		BigDecimal to = map.get(transferDto.to);
		from = from.subtract(transferDto.value);
		to = to.add(transferDto.value);

	}

	public void addAccount(String account, BigDecimal balance) {
		map.put(account, balance);
	}
}
