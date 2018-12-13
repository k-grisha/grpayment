package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.model.TransferEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Daemon transfer consumer
 */
public class TransferConsumer extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferConsumer.class);

	private final Queue<TransferEntity> queue;
	private final AccountRepository accountRepository;

	private boolean running = true;

	public TransferConsumer(Queue<TransferEntity> queue, AccountRepository accountRepository) {
		this.queue = queue;
		this.accountRepository = accountRepository;
	}

	@Override
	public void run() {
		while (running) {
			while (queue.size() != 0) {
				TransferEntity transferEntity = queue.poll();
				try {
					accountRepository.transfer(transferEntity.getFrom(), transferEntity.getTo(), transferEntity.getAmount());
					LOGGER.info("Transfer {} completed", transferEntity.getUid());
				} catch (Exception e) {
					LOGGER.error("Unable to finish transfer " + transferEntity.getUid(), e);
				}
			}
			if (!running) {
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopConsume() {
		this.running = false;
	}
}
