package gr.payment.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferDto {
	public final String from;
	public final String to;
	public final BigDecimal amount;

	public TransferDto(@JsonProperty("from") String from,
					   @JsonProperty("to") String to,
					   @JsonProperty("amount") BigDecimal amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
}
