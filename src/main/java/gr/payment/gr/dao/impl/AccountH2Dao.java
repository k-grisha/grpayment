package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.db.tables.records.AccountRecord;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static gr.payment.gr.db.Tables.ACCOUNT;

public class AccountH2Dao implements AccountRepository {

	final DSLContext ctx;

	public AccountH2Dao() {
		final BasicDataSource ds = new BasicDataSource();
		final Properties properties = new Properties();
		try {
			properties.load(AccountH2Dao.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ds.setDriverClassName(properties.getProperty("db.driver"));
		ds.setUrl(properties.getProperty("db.url"));
		ds.setUsername(properties.getProperty("db.username"));
		ds.setPassword(properties.getProperty("db.password"));

		final ConnectionProvider cp = new DataSourceConnectionProvider(ds);
		final Configuration configuration = new DefaultConfiguration()
				.set(cp)
				.set(SQLDialect.H2)
//				.set(new Settings().withExecuteWithOptimisticLocking(true))
				.set(new ThreadLocalTransactionProvider(cp, true));
		ctx = DSL.using(configuration);
		ctx.createTable(ACCOUNT);
		Arrays.setAll(locks, i -> new Object());
	}

	@Override
	public List<AccountEntity> findAll() {
		List<AccountRecord> account = ctx.fetch(ACCOUNT);
		return account.stream()
				.map(r -> new AccountEntity(r.getUid(), r.getOwnername(), r.getBalance()))
				.collect(Collectors.toList());
	}

	@Override
	public AccountEntity findByUid(String uid) {
		AccountRecord account = ctx.fetchOne(ACCOUNT, ACCOUNT.UID.eq(uid));
		if (account == null) {
			return null;
		}
		return new AccountEntity(account.getUid(), account.getOwnername(), account.getBalance());
	}

	@Override
	public void save(AccountEntity accountEntity) {
		ctx.transaction(() -> {
			AccountRecord account = ctx.fetchOne(ACCOUNT, ACCOUNT.UID.eq(accountEntity.getUid()));
			if (account == null) {
				account = ctx.newRecord(ACCOUNT);
				account.setUid(accountEntity.getUid());
			}
			account.setOwnername(accountEntity.getOwnerName());
			account.setBalance(accountEntity.getBalance());
			account.store();
		});
	}

	private final Object[] locks = new Object[16];

	@Override
	public void transfer(String from, String to, BigDecimal amount) {
		ctx.transaction(() -> {
			Result<AccountRecord> accounts = ctx.fetch(ACCOUNT, ACCOUNT.UID.in(from, to));

			if (accounts == null || accounts.isEmpty() || accounts.size() != 2) {
				throw new PaymentException("Accounts " + from + " and/or " + to + " not found");
			}
			AccountRecord fromAccount = accounts.stream()
					.filter(a -> a.getUid().equals(from)).findFirst()
					.orElseThrow(() -> new PaymentException("Account " + from + " not found"));
			AccountRecord toAccount = accounts.stream()
					.filter(a -> a.getUid().equals(to)).findFirst()
					.orElseThrow(() -> new PaymentException("Account " + to + " not found"));
			if (fromAccount.getBalance().compareTo(amount) < 0) {
				throw new PaymentException("Account with id=" + from + " doesn't have enough money");
			}
			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
			toAccount.setBalance(toAccount.getBalance().add(amount));
			ctx.batchStore(accounts);
		});
	}

	private void validateAccounts(String from, String to, Result<AccountRecord> accounts) {

	}


//	@Override
//	public void transfer(String from, String to, BigDecimal amount) {
//		ctx.transaction(() -> {
//			AccountRecord fromAccount = ctx.fetchOne(ACCOUNT, ACCOUNT.UID.eq(from));
//			if (fromAccount == null) {
//				throw new PaymentException("Account with id=" + from + " is not found");
//			}
//			if (fromAccount.getBalance().compareTo(amount) < 0) {
//				throw new PaymentException("Account with id=" + from + " doesn't have enough money");
//			}
//			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
//			fromAccount.store();
//			AccountRecord toAccount = ctx.fetchOne(ACCOUNT, ACCOUNT.UID.eq(to));
//			if (toAccount == null) {
//				throw new PaymentException("Account with id=" + to + " is not found");
//			}
//			toAccount.setBalance(toAccount.getBalance().add(amount));
//			toAccount.store();
//
//		});
//	}


}
