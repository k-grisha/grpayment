package gr.payment.gr.utils;

import gr.payment.gr.dao.impl.AccountDao;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;

import java.io.IOException;
import java.util.Properties;

import static gr.payment.gr.db.Tables.ACCOUNT;
import static org.jooq.impl.DSL.constraint;

public class DaoUtil {

	public static DSLContext buildAccountContext() {
		final BasicDataSource ds = new BasicDataSource();
		final Properties properties = new Properties();
		try {
			properties.load(AccountDao.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			// todo
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
				.set(new Settings().withExecuteWithOptimisticLocking(true))
				.set(new ThreadLocalTransactionProvider(cp, true));
		DSLContext context = DSL.using(configuration);
		context.dropTableIfExists(ACCOUNT).execute();
		context.createTable(ACCOUNT)
				.column(ACCOUNT.UID)
				.column(ACCOUNT.OWNERNAME)
				.column(ACCOUNT.BALANCE)//
				.constraint(constraint("PK_ACCOUNT").primaryKey(ACCOUNT.UID))
				.execute();
		return context;
	}
}
