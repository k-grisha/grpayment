package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Transfer service
 */
public class TransferService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

	private final AccountRepository accountRepository;


	public TransferService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * Money Transfer
	 *
	 * @param transferEntity Data of transfer
	 * @return UID of transaction
	 */
	public String transfer(TransferEntity transferEntity) {
		if (transferEntity.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new PaymentException("Transfer amount can not be less than zero");
		}
		accountRepository.transfer(transferEntity.getFrom(), transferEntity.getTo(),transferEntity.getAmount());

		//todo some Transfer Store service
		String uid = UUID.randomUUID().toString();
		LOGGER.info("Transfer {} is finished. from {} to {} amount {}",
				uid, transferEntity.getFrom(), transferEntity.getTo(), transferEntity.getAmount());
		return uid;
	}
}
