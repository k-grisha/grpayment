package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.AccountEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountMapDao implements AccountRepository {
	private Map<String, AccountEntity> map = new ConcurrentHashMap<>();

	@Override
	public List<AccountEntity> findAll() {
		return new ArrayList<>(map.values());
	}

	@Override
	public AccountEntity findByUid(String uid) {
		return map.get(uid);
	}

	@Override
	public void save(AccountEntity accountEntity) {
		map.put(accountEntity.getUid(), accountEntity);
	}

	@Override
	public void updateBalance(String iud, BigDecimal value) {
		AccountEntity account = map.get(iud);
		if (account == null) {
			throw new PaymentException("Account with uid=" + iud + " not found");
		}
		account.setBalance(value);
	}

	@Override
	public void increaseBalance(String iud, BigDecimal value) {
		AccountEntity account = map.get(iud);
//		account
	}

}
