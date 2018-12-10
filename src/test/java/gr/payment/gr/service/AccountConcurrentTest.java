package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.impl.AccountMapDao;
import gr.payment.gr.model.AccountEntity;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountConcurrentTest {


	@Test
	public void concurrent() throws InterruptedException {
		final AccountRepository accountRepository = new AccountMapDao();
		accountRepository.save(new AccountEntity("111", "Ivan", new BigDecimal("100000")));
		accountRepository.save(new AccountEntity("222", "Grisha", new BigDecimal("1")));
		accountRepository.save(new AccountEntity("333", "Petr", new BigDecimal("200000")));
		accountRepository.save(new AccountEntity("444", "Vasia", new BigDecimal("200000")));
		AccountService accountServiceConcur = new AccountService(accountRepository);

		for (int i = 0; i < 100; i++) {
			PayThread ct = new PayThread(accountServiceConcur, "111", "222");
			ct.start();
			PayThread ct2 = new PayThread(accountServiceConcur, "333", "222");
			ct2.start();
			PayThread ct3 = new PayThread(accountServiceConcur, "444", "222");
			ct3.start();
		}
		Thread.sleep(5000);

		System.out.println(accountServiceConcur.getByUid("111").getBalance());
		System.out.println(accountServiceConcur.getByUid("222").getBalance());
		System.out.println(accountServiceConcur.getByUid("333").getBalance());
		System.out.println(accountServiceConcur.getByUid("444").getBalance());
	}

	class PayThread extends Thread {
		private AccountService accountService;
		private final String from;
		private final String to;

		public PayThread(AccountService accountService, String from, String to) {
			this.accountService = accountService;
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			long t1 = System.currentTimeMillis();
			System.out.println(t1 + " " +  Thread.currentThread().getName() + " start");
			for (int i = 0; i < 100; i++) {
				accountService.transfer(from, to, new BigDecimal(1));
			}
			long t2 = System.currentTimeMillis();
			System.out.println(t2 + " " + Thread.currentThread().getName() + " finis at " + (t2 - t1));
		}
	}
}
