package gr.payment.gr.service;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class TransferServiceTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private TransferService transferService;

	@Before
	public void before() {
		AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
		Map<String, AccountEntity> entityMap = new HashMap<>();
		AccountEntity accountA = new AccountEntity("111", "AAA", new BigDecimal("100.0"));
		AccountEntity accountB = new AccountEntity("222", "BBB", new BigDecimal("100.0"));
		entityMap.put(accountA.getUid(), accountA);
		entityMap.put(accountB.getUid(), accountB);
		when(accountRepository.findAll()).thenReturn(new ArrayList<>(entityMap.values()));
		when(accountRepository.findByUid(anyString()))
				.thenAnswer(invocation -> entityMap.get(invocation.getArgumentAt(0, String.class)));
		transferService = new TransferService(accountRepository);
	}

	@Test
	public void transfer_success() {
		String transferUid = transferService.transfer(new TransferEntity("111", "222", new BigDecimal("10")));
		Assert.assertTrue(transferUid != null && !transferUid.isEmpty());
	}

	@Test
	public void transfer_wrongAmount_fail() {
		expectedEx.expect(PaymentException.class);
		expectedEx.expectMessage("Transfer amount can not be less than zero");
		transferService.transfer(new TransferEntity("111", "222", new BigDecimal("-10.0")));
	}

}