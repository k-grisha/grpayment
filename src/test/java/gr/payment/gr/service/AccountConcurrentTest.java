package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.impl.AccountMapDao;
import gr.payment.gr.model.AccountEntity;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountConcurrentTest {


	@Test
	public void concurrent() throws InterruptedException {
		final AccountRepository accountRepository = new AccountMapDao();
		accountRepository.save(new AccountEntity("111", "Ivan", new BigDecimal("10000")));
		accountRepository.save(new AccountEntity("222", "Grisha", new BigDecimal("0")));
		accountRepository.save(new AccountEntity("333", "Petr", new BigDecimal("10000")));
		accountRepository.save(new AccountEntity("444", "Vasia", new BigDecimal("10000")));
		AccountService accountServiceConcur = new AccountService(accountRepository);

		for (int i = 0; i < 100; i++) {
			PayThread pt1 = new PayThread(accountServiceConcur, "111", "222");
			pt1.start();
			PayThread pt2 = new PayThread(accountServiceConcur, "333", "444");
			pt2.start();
			PayThread pt3 = new PayThread(accountServiceConcur, "444", "222");
			pt3.start();
		}
		Thread.sleep(10000);

		Assert.assertEquals(BigDecimal.ZERO, accountServiceConcur.getByUid("111").getBalance());
		Assert.assertEquals(new BigDecimal(20000), accountServiceConcur.getByUid("222").getBalance());
		Assert.assertEquals(BigDecimal.ZERO, accountServiceConcur.getByUid("333").getBalance());
		Assert.assertEquals(new BigDecimal(10000), accountServiceConcur.getByUid("444").getBalance());
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
			for (int i = 0; i < 100; i++) {
				accountService.transfer(from, to, new BigDecimal(1));
			}
		}
	}
}
