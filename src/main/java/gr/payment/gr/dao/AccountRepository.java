package gr.payment.gr.dao;

import gr.payment.gr.model.AccountEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Accounts repository
 */
public interface AccountRepository {

	/**
	 * Get all accounts
	 *
	 * @return List of accounts or empty list
	 */
	List<AccountEntity> findAll();

	/**
	 * Find account by uuid
	 *
	 * @param uid uid of account
	 * @return account or null
	 */
	AccountEntity findByUid(String uid);

	/**
	 * Save account
	 *
	 * @param accountEntity account for save
	 */
	void save(AccountEntity accountEntity);

	/**
	 * Money transfer
	 *
	 * @param from uid of sender
	 * @param to uid of recipient
	 * @param amount amount
	 */
	void transfer(String from, String to, BigDecimal amount);
}
