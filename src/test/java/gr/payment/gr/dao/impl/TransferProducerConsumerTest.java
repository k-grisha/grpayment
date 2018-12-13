package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.model.TransferEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TransferProducerConsumerTest {

	private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
	private TransferProducer transferProducer;

	@Before
	public void before() {
		Queue<TransferEntity> queue = new ArrayBlockingQueue<>(100);
		transferProducer = new TransferProducer(queue);
		new TransferConsumer(queue, accountRepository).start();
	}

	@Test
	public void produceConsume_success() throws InterruptedException {
		TransferEntity transfer = new TransferEntity("111", "AAA", new BigDecimal("100"));
		transferProducer.save(transfer);
		transferProducer.save(transfer);
		Thread.sleep(1000);
		verify(accountRepository, times(2))
				.transfer(transfer.getFrom(), transfer.getTo(), transfer.getAmount());
	}
}