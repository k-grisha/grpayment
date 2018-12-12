package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.model.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
	 * Get all accounts
	 *
	 * @return accounts
	 */
	public List<AccountEntity> getAll() {
		return accountRepository.findAll();
	}

	/**
	 * Get account by uid
	 *
	 * @param uid uid
	 * @return account
	 */
	public AccountEntity getByUid(String uid) {
		return accountRepository.findByUid(uid);
	}


}
