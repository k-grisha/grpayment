package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Класс с основной бизнес-логикой
 */
public class AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;

	/**
	 * Мониторы для сегментной синхронизации
	 */
	private final Object[] locks = new Object[16];

	public AccountService(AccountRepository accountRepository, TransferRepository transferRepository) {
		Arrays.setAll(locks, i -> new Object());
		this.accountRepository = accountRepository;
		this.transferRepository = transferRepository;
	}


	/**
	 * Перевод денег между аккаунатми
	 *
	 * @param from   ID от кого перевод
	 * @param to     ID кому перевод
	 * @param amount Объем перевода
	 * @return ID транзакции
	 */
	public String transfer(TransferEntity transferEntity) {
//		LOGGER.info("Transfer request. from {} to {} amount {}", from, to, amount);
		if (transferEntity.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new PaymentException("Transfer amount can not be less than zero");
		}
		transferRepository.save(transferEntity);
//		accountRepository.transfer(from, to, amount);
//		LOGGER.info("Transfer is finished. from {} to {} amount {}", from, to, amount);
		// todo Сохранить данные таранзакции в соотв. сервисе, желательно асинхронно.
		return UUID.randomUUID().toString();
	}

	private Object getLoc(String from, String to) {
		int id = (from.hashCode() % locks.length + to.hashCode() % locks.length) / 2;
		return locks[id];
	}


	/**
	 * Получить все Аккаунты
	 */
	public List<AccountEntity> getAll() {
		return accountRepository.findAll();
	}

	/**
	 * Поиск Аккаунта по номеру
	 */
	public AccountEntity getByUid(String uid) {
		return accountRepository.findByUid(uid);
	}


}
