package gr.payment.gr.dao;

import gr.payment.gr.model.TransferEntity;

public interface TransferRepository {

	/**
	 * Save transfer
	 *
	 * @param transferEntity transfer data
	 * @return uid of transfer
	 */
	String save(TransferEntity transferEntity);

}
