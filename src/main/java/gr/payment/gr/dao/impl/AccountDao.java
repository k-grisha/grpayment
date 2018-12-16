package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.db.tables.records.AccountRecord;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gr.payment.gr.db.Tables.ACCOUNT;

public class AccountDao implements AccountRepository {

	private final DSLContext context;

	public AccountDao(DSLContext context) {
		this.context = context;
	}

	@Override
	public void transfer(String from, String to, BigDecimal amount) {
		context.transaction(() -> {
			Result<Record> records = context.select()
					.from(ACCOUNT)
					.where(ACCOUNT.UID.in(Arrays.asList(from, to)))
					.forUpdate()
					.fetch();

			Record fromAccount = records.stream()
					.filter(r -> r.get(ACCOUNT.UID).equals(from))
					.findFirst()
					.orElseThrow(() -> new PaymentException("Transfer cannot be finished. Account with id=" + from + " is not found"));
			Record toAccount = records.stream()
					.filter(r -> r.get(ACCOUNT.UID).equals(to))
					.findFirst()
					.orElseThrow(() -> new PaymentException("Transfer cannot be finished. Account with id=" + to + " is not found"));

			if (fromAccount.get(ACCOUNT.BALANCE).compareTo(amount) < 0) {
				throw new PaymentException("Transfer cannot be finished. Account with id=" + from + " doesn't have enough money");
			}

			context.batch(
					context.update(ACCOUNT)
							.set(ACCOUNT.BALANCE, fromAccount.get(ACCOUNT.BALANCE).subtract(amount))
							.where(ACCOUNT.UID.eq(from)),
					context.update(ACCOUNT)
							.set(ACCOUNT.BALANCE, toAccount.get(ACCOUNT.BALANCE).add(amount))
							.where(ACCOUNT.UID.eq(to))
			).execute();
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
