package gr.payment.gr.service;

import gr.payment.gr.dao.AccountDao;
import gr.payment.gr.dto.AccountDto;
import gr.payment.gr.dto.TransferDto;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
	private final AccountDao accountDao;

	public AccountService(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/**
	 * Получить все Аккаунты
	 */
	public List<AccountDto> getAll() {
		return accountDao.findAll().stream()
				.map(e -> new AccountDto(e.getUid(), e.getOwnerName(), e.getBalance()))
				.collect(Collectors.toList());
	}

	/**
	 * Поиск Аккаунта по номеру
	 */
	public AccountDto getBy(String uid) {
		AccountEntity accountEntity = accountDao.findByUid(uid);
		if (accountEntity == null) {
			return null;
		}
		return new AccountDto(accountEntity.getUid(), accountEntity.getOwnerName(), accountEntity.getBalance());
	}

	public void transfer(TransferDto transferDto) {
		//todo transferDto.value провалидировать
		AccountEntity fromAccount = accountDao.findByUid(transferDto.from);
		if (fromAccount == null) {
			throw new PaymentException("Account with id=" + transferDto.from + " is not found");
		}
		if (fromAccount.getBalance().compareTo(transferDto.value) < 0) {
			throw new PaymentException("Account with id=" + transferDto.from + " doesn't have enough money");
		}
		AccountEntity toAccount = accountDao.findByUid(transferDto.to);
		if (toAccount == null) {
			throw new PaymentException("Account with id=" + transferDto.to + " is not found");
		}

		accountDao.updateBalance(fromAccount.getUid(), fromAccount.getBalance().subtract(transferDto.value));
		accountDao.updateBalance(toAccount.getUid(), toAccount.getBalance().add(transferDto.value));

	}

}
