package gr.payment.gr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.payment.gr.dto.AccountDto;
import gr.payment.gr.dto.TransferDto;
import gr.payment.gr.model.AccountEntity;
import gr.payment.gr.model.TransferEntity;
import gr.payment.gr.service.AccountService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.stream.Collectors;

public class AccountController {
	private static final String PATH_PREFIX = "/rest/v1/";
	public static final String PATH_ACCOUNTS = PATH_PREFIX + "accounts/";
	private static final String UID_ACCOUNT = ":uid";
	public static final String PATH_ACCOUNT_UID = PATH_ACCOUNTS + UID_ACCOUNT;
	public static final String PATH_TRANSFER = PATH_ACCOUNTS + "transfer";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	public Route getAll() {
		return (Request request, Response response) -> {
			List<AccountDto> accountsDto = accountService.getAll().stream()
					.map(e -> new AccountDto(e.getUid(), e.getOwnerName(), e.getBalance()))
					.collect(Collectors.toList());
			return MAPPER.writeValueAsString(accountsDto);
		};
	}

	public Route getByUid() {
		return (request, response) -> {
			AccountEntity account = accountService.getByUid(request.params(UID_ACCOUNT));
			if (account == null) {
				return MAPPER.writeValueAsString(null);
			}
			AccountDto accountDto = new AccountDto(account.getUid(), account.getOwnerName(), account.getBalance());
			return MAPPER.writeValueAsString(accountDto);
		};
	}

	public Route transfer() {
		return (request, response) -> {
			TransferDto transferDto = MAPPER.readValue(request.body(), TransferDto.class);
			String transferId = accountService.transfer(new TransferEntity(transferDto.from, transferDto.to, transferDto.amount));
			return MAPPER.writeValueAsString(transferId);
		};
	}
}
