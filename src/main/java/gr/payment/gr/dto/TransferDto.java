package gr.payment.gr.dto;

import java.math.BigDecimal;

public class TransferDto {
	public final String from;
	public final String to;
	public final BigDecimal value;

	public TransferDto(String from, String to, BigDecimal value) {
		this.from = from;
		this.to = to;
		this.value = value;
	}
}
