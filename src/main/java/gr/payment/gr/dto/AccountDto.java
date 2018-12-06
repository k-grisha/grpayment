package gr.payment.gr.dto;

import java.math.BigDecimal;

public final class AccountDto {
	public final String uid;
	public final String ownerName;
	public final BigDecimal balance;

	public AccountDto(String uid, String ownerName, BigDecimal balance) {
		this.uid = uid;
		this.ownerName = ownerName;
		this.balance = balance;
	}
}
