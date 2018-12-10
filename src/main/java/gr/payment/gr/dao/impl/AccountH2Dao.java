package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.db.tables.records.AccountRecord;
import gr.payment.gr.model.AccountEntity;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static gr.payment.gr.db.Tables.ACCOUNT;

public class AccountH2Dao implements AccountRepository {

	final DSLContext ctx;
	final DefaultTransactionProvider transactionProvider;

	public DefaultTransactionProvider getTransactionProvider() {
		return transactionProvider;
	}

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
				.set(new ThreadLocalTransactionProvider(cp, true));
		ctx = DSL.using(configuration);
		ctx.createTable(ACCOUNT);
		transactionProvider = new DefaultTransactionProvider(cp);

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

	@Override
	public void updateBalance(String iud, BigDecimal value) {
		ctx.transaction(() -> {
			AccountRecord account = ctx.fetchOne(ACCOUNT, ACCOUNT.UID.eq(iud));
			if (account != null) {
				account.setBalance(value);
				account.update();
			}
		});
	}

	@Override
	public void transfer(String from, String to, BigDecimal amount) {

	}


}
