package gr.payment.gr.dao;

import gr.payment.gr.model.TransferEntity;

public interface TransferRepository {

	/**
	 * @param transferEntity
	 * @return
	 */
	String save(TransferEntity transferEntity);

}
