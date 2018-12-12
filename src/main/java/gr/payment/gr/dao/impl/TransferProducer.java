package gr.payment.gr.dao.impl;

import gr.payment.gr.dao.TransferRepository;
import gr.payment.gr.exceprion.PaymentException;
import gr.payment.gr.model.TransferEntity;

import java.util.Queue;
import java.util.UUID;

public class TransferProducer implements TransferRepository {

	private final Queue<TransferEntity> queue;

	public TransferProducer(Queue<TransferEntity> queue) {
		this.queue = queue;
	}

	@Override
	public String save(TransferEntity transferEntity) {
		if (transferEntity.getUid() != null && !transferEntity.getUid().trim().isEmpty()) {
			throw new PaymentException("Transfer with UID cannot be save");
		}
		transferEntity.setUid(UUID.randomUUID().toString());
		queue.add(transferEntity);
		return transferEntity.getUid();
	}
}
