package gr.payment.gr.exceprion;

public class PaymentException extends RuntimeException {
	public PaymentException(String message) {
		super(message);
	}
}
