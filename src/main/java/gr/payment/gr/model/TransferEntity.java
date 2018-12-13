package gr.payment.gr.model;

import java.math.BigDecimal;

public class TransferEntity {

	/** Transfer uid */
	private String uid;
	/** Money sender */
	private String from;
	/** Money recipient */
	private String to;
	/** Amount */
	private BigDecimal amount;

	public TransferEntity(String from, String to, BigDecimal amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
