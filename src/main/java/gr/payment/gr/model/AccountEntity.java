package gr.payment.gr.model;

import java.math.BigDecimal;

public class AccountEntity {
	/** Уникальный номер счета */
	private String uid;
	/** Имя владельца */
	private String ownerName;
	/** Текущий баланс */
	volatile private BigDecimal balance;

	public AccountEntity(String uid, String ownerName, BigDecimal balance) {
		this.uid = uid;
		this.ownerName = ownerName;
		this.balance = balance;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
