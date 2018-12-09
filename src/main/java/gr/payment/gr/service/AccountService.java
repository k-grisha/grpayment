package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Класс с основной бизнес-логикой
 */
public class AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * Перевод денег между аккаунатми
	 *
	 * @param from  ID от кого перевод
	 * @param to    ID кому перевод
	 * @param value Объем перевода
	 * @return ID транзакции
	 */
	public String transfer(String from, String to, BigDecimal value) {
		LOGGER.info("Transfer request. from {} to {} value {}", from, to, value);
		//todo value провалидировать
		AccountEntity fromAccount = accountRepository.findByUid(from);
		if (fromAccount == null) {
			throw new PaymentException("Account with id=" + from + " is not found");
		}
		if (fromAccount.getBalance().compareTo(value) < 0) {
			throw new PaymentException("Account with id=" + from + " doesn't have enough money");
		}
		AccountEntity toAccount = accountRepository.findByUid(to);
		if (toAccount == null) {
			throw new PaymentException("Account with id=" + to + " is not found");
		}

		accountRepository.updateBalance(fromAccount.getUid(), fromAccount.getBalance().subtract(value));
		accountRepository.updateBalance(toAccount.getUid(), toAccount.getBalance().add(value));
		LOGGER.info("Transfer is finished. from {} to {} value {}", from, to, value);
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
	public AccountEntity getBy(String uid) {
		return accountRepository.findByUid(uid);
	}


}
