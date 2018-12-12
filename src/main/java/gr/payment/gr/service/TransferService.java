package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class TransferService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;


	public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
		this.accountRepository = accountRepository;
		this.transferRepository = transferRepository;
	}

	/**
	 * Money Transfer
	 *
	 * @param transferEntity
	 * @return UID of transaction
	 */
	public String transfer(TransferEntity transferEntity) {
		LOGGER.info("Transfer request. from {} to {} amount {}",
				transferEntity.getFrom(), transferEntity.getTo(), transferEntity.getAmount());
		if (transferEntity.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new PaymentException("Transfer amount can not be less than zero");
		}
		AccountEntity fromAccount = accountRepository.findByUid(transferEntity.getFrom());
		if (fromAccount == null) {
			throw new PaymentException("Account with id=" + transferEntity.getFrom() + " is not found");
		}
		if (fromAccount.getBalance().compareTo(transferEntity.getAmount()) < 0) {
			throw new PaymentException("Account with id=" + transferEntity.getFrom() + " doesn't have enough money");
		}
		AccountEntity toAccount = accountRepository.findByUid(transferEntity.getTo());
		if (toAccount == null) {
			throw new PaymentException("Account with id=" + transferEntity.getTo() + " is not found");
		}

		String uid = transferRepository.save(transferEntity);
		LOGGER.info("Transfer {} is registered. from {} to {} amount {}",
				uid, transferEntity.getFrom(), transferEntity.getTo(), transferEntity.getAmount());
		return uid;
	}
}
