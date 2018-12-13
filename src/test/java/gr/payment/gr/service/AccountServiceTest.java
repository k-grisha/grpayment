package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.model.AccountEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private AccountService accountService;

	@Before
	public void before() {
		Map<String, AccountEntity> entityMap = new ConcurrentHashMap<>();
		AccountEntity accountA = new AccountEntity("111", "AAA", new BigDecimal("100.0"));
		AccountEntity accountB = new AccountEntity("222", "BBB", new BigDecimal("100.0"));
		entityMap.put(accountA.getUid(), accountA);
		entityMap.put(accountB.getUid(), accountB);
		AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
		when(accountRepository.findAll()).thenReturn(new ArrayList<>(entityMap.values()));
		when(accountRepository.findByUid(anyString()))
				.thenAnswer(invocation -> entityMap.get(invocation.getArgumentAt(0, String.class)));
		accountService = new AccountService(accountRepository);
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
		AccountEntity account = accountService.getByUid("111");
		Assert.assertEquals("111", account.getUid());
	}

	@Test
	public void getByUid_fail() {
		AccountEntity account = accountService.getByUid("333");
		Assert.assertNull(account);
	}

}