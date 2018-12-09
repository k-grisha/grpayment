package gr.payment.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class AccountDto {
	public final String uid;
	public final String ownerName;
	public final BigDecimal balance;

	public AccountDto(@JsonProperty("uid") String uid,
					  @JsonProperty("ownerName") String ownerName,
					  @JsonProperty("balance") BigDecimal balance) {
		this.uid = uid;
		this.ownerName = ownerName;
		this.balance = balance;
	}
}
