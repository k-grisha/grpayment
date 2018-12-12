package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.impl.AccountH2Dao;
import gr.payment.gr.dao.impl.TransferConsumer;
import gr.payment.gr.dao.impl.TransferProducer;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class AccountServiceTest {

	private AccountService accountService;
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void before() {
		AccountRepository accountRepository = new AccountH2Dao();
		accountRepository.save(new AccountEntity("111", "AAA", new BigDecimal("100.0")));
		accountRepository.save(new AccountEntity("222", "BBB", new BigDecimal("100.0")));
		Queue<TransferEntity> queue = new ArrayBlockingQueue<>(10000);
		TransferProducer transferProducer = new TransferProducer(queue);
		new TransferConsumer(queue, accountRepository).run();
		accountService = new AccountService(accountRepository, transferProducer);
	}

	@Test
	public void transfer_success() {
		Assert.assertTrue(accountService.getByUid("111").getBalance().compareTo(new BigDecimal("100.0")) == 0);
		Assert.assertTrue(accountService.getByUid("222").getBalance().compareTo(new BigDecimal("100.0")) == 0);
		accountService.transfer(new TransferEntity("111", "222", new BigDecimal("10")));
		Assert.assertTrue(accountService.getByUid("111").getBalance().compareTo(new BigDecimal("90.0")) == 0);
		Assert.assertTrue(accountService.getByUid("222").getBalance().compareTo(new BigDecimal("110.0")) == 0);
	}

	@Test
	public void transfer_wrongRecipient_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountService.transfer(new TransferEntity("111", "333", new BigDecimal("10.0")));
	}

	@Test
	public void transfer_wrongSender_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountService.transfer(new TransferEntity("333", "222", new BigDecimal("10.0")));
	}

	@Test
	public void transfer_wrongAmount_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Transfer amount can not be less than zero");
		accountService.transfer(new TransferEntity("333", "222", new BigDecimal("-10.0")));
	}

	@Test
	public void transfer_wrongBalance_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=111 doesn't have enough money");
		accountService.transfer(new TransferEntity("111", "222", new BigDecimal("999.0")));
	}

	@Test
	public void getAll_success() {
		List<AccountEntity> accounts = accountService.getAll();
		Assert.assertEquals(2, accounts.size());
		Assert.assertTrue(accounts.stream().anyMatch(a -> a.getUid().equals("111")));
		Assert.assertTrue(accounts.stream().anyMatch(a -> a.getUid().equals("222")));
	}

	@Test
	public void getByUid_success() {
		AccountEntity account = accountService.getByUid("333");
		Assert.assertNull(account);
		account = accountService.getByUid("111");
		Assert.assertEquals("111", account.getUid());
	}

	@Test
	public void asd() {
		Queue<String> queue = new ArrayBlockingQueue<>(123);
		queue.add("A");
		queue.add("B");
		queue.add("C");
		System.out.println(queue.poll() + queue.size());
		queue.add("A");
		System.out.println(queue.poll() + queue.size());
		System.out.println(queue.poll() + queue.size());
		System.out.println(queue.poll() + queue.size());
		System.out.println(queue.poll() + queue.size());
	}
}