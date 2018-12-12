package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.impl.AccountH2Dao;
import gr.payment.gr.dao.impl.TransferConsumer;
import gr.payment.gr.dao.impl.TransferProducer;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class AccountConcurrentTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountConcurrentTest.class);

	@Test
	public void concurrent() throws InterruptedException {
		final AccountRepository accountRepository = new AccountH2Dao();
		accountRepository.save(new AccountEntity("111", "Ivan", new BigDecimal("10000")));
		accountRepository.save(new AccountEntity("222", "Grisha", new BigDecimal("10000")));
		accountRepository.save(new AccountEntity("333", "Petr", new BigDecimal("10000")));
		accountRepository.save(new AccountEntity("444", "Vasia", new BigDecimal("10000")));
		Queue<TransferEntity> queue = new ArrayBlockingQueue<>(10000);
		TransferProducer transferProducer = new TransferProducer(queue);
		new TransferConsumer(queue, accountRepository).run();
		AccountService accountServiceConcur = new AccountService(accountRepository, transferProducer);

		for (int i = 0; i < 100; i++) {
			PayThread pt1 = new PayThread(accountServiceConcur, "111", "222");
			pt1.start();
			PayThread pt2 = new PayThread(accountServiceConcur, "222", "111");
			pt2.start();
			PayThread pt3 = new PayThread(accountServiceConcur, "444", "333");
			pt3.start();
		}
		Thread.sleep(20000);
		BigDecimal sum = accountServiceConcur.getByUid("111").getBalance()
				.add(accountServiceConcur.getByUid("222").getBalance())
				.add(accountServiceConcur.getByUid("333").getBalance())
				.add(accountServiceConcur.getByUid("444").getBalance());
		Assert.assertTrue(sum.compareTo(new BigDecimal("40000")) == 0);
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
			LOGGER.info("start");
			for (int i = 0; i < 100; i++) {
				accountService.transfer(new TransferEntity(from, to, new BigDecimal(1)));
			}
			LOGGER.info("finish");
		}
	}
}
