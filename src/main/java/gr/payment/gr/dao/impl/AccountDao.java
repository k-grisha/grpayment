package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.db.tables.records.AccountRecord;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static gr.payment.gr.db.Tables.ACCOUNT;

public class AccountDao implements AccountRepository {

	private final DSLContext context;

	public AccountDao(DSLContext context) {
		this.context = context;
	}

	@Override
	public synchronized void transfer(String from, String to, BigDecimal amount) {
		context.transaction(() -> {
			AccountRecord fromAccount = context.fetchOne(ACCOUNT, ACCOUNT.UID.eq(from));
			if (fromAccount == null) {
				throw new PaymentException("Transfer cannot be finished. Account with id=" + from + " is not found");
			}
			if (fromAccount.getBalance().compareTo(amount) < 0) {
				throw new PaymentException("Transfer cannot be finished. Account with id=" + from + " doesn't have enough money");
			}
			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
			fromAccount.store();
			AccountRecord toAccount = context.fetchOne(ACCOUNT, ACCOUNT.UID.eq(to));
			if (toAccount == null) {
				throw new PaymentException("Transfer cannot be finished. Account with id=" + to + " is not found");
			}
			toAccount.setBalance(toAccount.getBalance().add(amount));
			toAccount.store();
		});
	}

	@Override
	public List<AccountEntity> findAll() {
		List<AccountRecord> account = context.fetch(ACCOUNT);
		return account.stream()
				.map(r -> new AccountEntity(r.getUid(), r.getOwnername(), r.getBalance()))
				.collect(Collectors.toList());
	}

	@Override
	public AccountEntity findByUid(String uid) {
		AccountRecord account = context.fetchOne(ACCOUNT, ACCOUNT.UID.eq(uid));
		if (account == null) {
			return null;
		}
		return new AccountEntity(account.getUid(), account.getOwnername(), account.getBalance());
	}

	@Override
	public void save(AccountEntity accountEntity) {
		context.transaction(() -> {
			AccountRecord account = context.fetchOne(ACCOUNT, ACCOUNT.UID.eq(accountEntity.getUid()));
			if (account == null) {
				account = context.newRecord(ACCOUNT);
				account.setUid(accountEntity.getUid());
			}
			account.setOwnername(accountEntity.getOwnerName());
			account.setBalance(accountEntity.getBalance());
			account.store();
		});
	}

}
