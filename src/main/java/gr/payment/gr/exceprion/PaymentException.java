package gr.payment.gr.exceprion;

import java.io.IOException;

public class PaymentException extends RuntimeException {
	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String s, IOException e) {
		super(s, e);
	}
}
