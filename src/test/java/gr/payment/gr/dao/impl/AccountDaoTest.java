package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.utils.DaoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.List;

public class AccountDaoTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private AccountRepository accountRepository;

	@Before
	public void before() {
		accountRepository = new AccountDao(DaoUtil.buildAccountContext());
		accountRepository.save(new AccountEntity("111", "AAA", new BigDecimal("100.0")));
		accountRepository.save(new AccountEntity("222", "BBB", new BigDecimal("100.0")));
	}

	@Test
	public void saveAndFind_success() {
		AccountEntity account = new AccountEntity("999", "ZZZ", new BigDecimal("100.0"));
		accountRepository.save(account);
		AccountEntity savedAccount = accountRepository.findByUid(account.getUid());
		Assert.assertEquals(account.getUid(), savedAccount.getUid());
		Assert.assertEquals(account.getOwnerName(), savedAccount.getOwnerName());
		Assert.assertTrue(savedAccount.getBalance().compareTo(account.getBalance()) == 0);
	}

	@Test
	public void findAll() {
		List<AccountEntity> accounts = accountRepository.findAll();
		Assert.assertEquals(2, accounts.size());
		Assert.assertTrue(accounts.stream().anyMatch(a -> a.getUid().equals("111")));
		Assert.assertTrue(accounts.stream().anyMatch(a -> a.getUid().equals("222")));
	}

	@Test
	public void transfer() {
		accountRepository.transfer("111", "222", new BigDecimal("10"));
		Assert.assertTrue(accountRepository.findByUid("111").getBalance().compareTo(new BigDecimal("90")) == 0);
		Assert.assertTrue(accountRepository.findByUid("222").getBalance().compareTo(new BigDecimal("110")) == 0);
	}

	@Test
	public void transfer_wrongRecipient_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountRepository.transfer("111", "333", new BigDecimal("10.0"));
	}

	@Test
	public void transfer_wrongSender_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountRepository.transfer("333", "222", new BigDecimal("10.0"));
	}

	@Test
	public void transfer_wrongBalance_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=111 doesn't have enough money");
		accountRepository.transfer("111", "222", new BigDecimal("999.0"));
	}

	@Test
	public void create_success() {
		AccountEntity account = new AccountEntity("999", "ZZZ", new BigDecimal("100"));
		accountRepository.save(account);
		AccountEntity createdAccount = accountRepository.findByUid(account.getUid());
		Assert.assertNotNull(createdAccount);
		Assert.assertEquals(account.getUid(), createdAccount.getUid());
		Assert.assertEquals(account.getOwnerName(), createdAccount.getOwnerName());
	}

}