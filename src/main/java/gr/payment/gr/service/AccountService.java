package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
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

	/** Мониторы для сегментной синхронизации */
	private final Object[] locks = new Object[8];

	public AccountService(AccountRepository accountRepository) {
		Arrays.setAll(locks, i -> new Object());
		this.accountRepository = accountRepository;
	}

	/**
	 * Перевод денег между аккаунатми
	 *
	 * @param from   ID от кого перевод
	 * @param to     ID кому перевод
	 * @param amount Объем перевода
	 * @return ID транзакции
	 */
	public String transfer(String from, String to, BigDecimal amount) {
//		LOGGER.info("Transfer request. from {} to {} amount {}", from, to, amount);
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new PaymentException("Transfer amount can not be less than zero");
		}

		//todo монитор только по from череват тем что деньг получателю, пришедшие от разных отправителей, могут быть перезаписаны
		synchronized (locks[from.hashCode() % locks.length]) {
			AccountEntity fromAccount = accountRepository.findByUid(from);
			if (fromAccount == null) {
				throw new PaymentException("Account with id=" + from + " is not found");
			}
			AccountEntity toAccount = accountRepository.findByUid(to);
			if (toAccount == null) {
				throw new PaymentException("Account with id=" + to + " is not found");
			}

			if (fromAccount.getBalance().compareTo(amount) < 0) {
				throw new PaymentException("Account with id=" + from + " doesn't have enough money");
			}

			// todo транзакционно
			accountRepository.updateBalance(fromAccount.getUid(), fromAccount.getBalance().subtract(amount));
			accountRepository.updateBalance(toAccount.getUid(), toAccount.getBalance().add(amount));
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
//		LOGGER.info("Transfer is finished. from {} to {} amount {}", from, to, amount);
		// todo Сохранить данные таранзакции в соотв. сервисе, желательно асинхронно.
		return UUID.randomUUID().toString();
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
