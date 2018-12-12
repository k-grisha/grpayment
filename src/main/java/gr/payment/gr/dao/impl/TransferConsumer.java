package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.AccountRepository;
import gr.payment.gr.model.TransferEntity;

import java.util.Queue;

/**
 * daemon
 */
public class TransferConsumer extends Thread {

	private final Queue<TransferEntity> queue;
	private final AccountRepository accountRepository;

	public TransferConsumer(Queue<TransferEntity> queue, AccountRepository accountRepository) {
		this.queue = queue;
		this.accountRepository = accountRepository;
	}

	@Override
	public void run() {
		while (true) {
			while (queue.size() != 0) {
				TransferEntity transferEntity = queue.poll();
				try {
					accountRepository.transfer(transferEntity.getFrom(), transferEntity.getTo(), transferEntity.getAmount());
				} catch (Exception e) {
					queue.add(transferEntity);
					// todo logg
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
