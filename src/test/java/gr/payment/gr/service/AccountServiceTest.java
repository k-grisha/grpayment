package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

	private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
	private Map<String, AccountEntity> entityMap = new ConcurrentHashMap<>();
	private AccountService accountService;
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void before() {
		AccountEntity accountA = new AccountEntity("111", "AAA", new BigDecimal("100.0"));
		AccountEntity accountB = new AccountEntity("222", "BBB", new BigDecimal("100.0"));
		entityMap.put(accountA.getUid(), accountA);
		entityMap.put(accountB.getUid(), accountB);

		when(accountRepository.findAll()).thenReturn(new ArrayList<>(entityMap.values()));
		when(accountRepository.findByUid(anyString()))
				.thenAnswer(invocation -> entityMap.get(invocation.getArgumentAt(0, String.class)));
		doAnswer((Answer<Void>) invocation -> {
			AccountEntity entity = entityMap.get(invocation.getArgumentAt(0, String.class));
			entity.setBalance(invocation.getArgumentAt(1, BigDecimal.class));
			return null;
		}).when(accountRepository).updateBalance(anyString(), any(BigDecimal.class));

		accountService = new AccountService(accountRepository);
	}

	@Test
	public void transfer_success() {
		Assert.assertEquals(new BigDecimal("100.0"), accountService.getByUid("111").getBalance());
		Assert.assertEquals(new BigDecimal("100.0"), accountService.getByUid("222").getBalance());

		accountService.transfer("111", "222", new BigDecimal("10"));

		Assert.assertEquals(new BigDecimal("90.0"), accountService.getByUid("111").getBalance());
		Assert.assertEquals(new BigDecimal("110.0"), accountService.getByUid("222").getBalance());
	}

	@Test
	public void transfer_wrongRecipient_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountService.transfer("111", "333", new BigDecimal("10.0"));
	}

	@Test
	public void transfer_wrongSender_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=333 is not found");
		accountService.transfer("333", "222", new BigDecimal("10.0"));
	}

	@Test
	public void transfer_wrongAmount_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Transfer amount can not be less than zero");
		accountService.transfer("333", "222", new BigDecimal("-10.0"));
	}

	@Test
	public void transfer_wrongBalance_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Account with id=111 doesn't have enough money");
		accountService.transfer("111", "222", new BigDecimal("999.0"));
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
}