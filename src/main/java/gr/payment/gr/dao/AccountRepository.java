package gr.payment.gr.dao;

import gr.payment.gr.model.AccountEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Репозитарий для рбаты с Аккаунтами
 */
public interface AccountRepository {

	// TODO постраничный запрос Аккаунтов

	/**
	 * Получить все Аккаунты
	 *
	 * @return Список всех Аккаунтов или пустой список
	 */
	List<AccountEntity> findAll();

	/**
	 * Поиск Аккаунта по номеру
	 *
	 * @param uid уникальный номер
	 * @return Аккаунт или Null
	 */
	AccountEntity findByUid(String uid);

	/**
	 * Сохранить новый аккаунт
	 *
	 * @param accountEntity Новый Аккаунт
	 */
	void save(AccountEntity accountEntity);


	/**
	 * Обновить баланс Аккаунта
	 *
	 * @param iud   Уникальный номер Аккаунта
	 * @param value Новый баланс
	 */
	void updateBalance(String iud, BigDecimal value);
}
