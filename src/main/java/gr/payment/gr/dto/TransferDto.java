package gr.payment.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferDto {
	public final String from;
	public final String to;
	public final BigDecimal value;

	public TransferDto(@JsonProperty("from") String from,
					   @JsonProperty("to") String to,
					   @JsonProperty("value") BigDecimal value) {
		this.from = from;
		this.to = to;
		this.value = value;
	}
}
