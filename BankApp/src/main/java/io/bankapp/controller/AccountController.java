package io.bankapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.bankapp.dao.AccountsRepository;
import io.bankapp.exception.UserNotFoundException;
import io.bankapp.model.Accounts;
import io.bankapp.model.Logger;
import io.bankapp.service.AccountService;


@RestController
public class AccountController {
	@Autowired
	private AccountService accountService;
	@Autowired
	private LoggerController loggerController;
	@Autowired
	private AccountsRepository repo;

	// createAccount happens upon createCustomer
	@PostMapping("/account/register")
	public ResponseEntity<Accounts> createAccount(@RequestBody Accounts a) {
		Accounts acct = new Accounts();
		acct.setAcctID(a.getAcctID());
		acct.setAcctStatus(a.getAcctStatus());
		acct.setBalance(a.getBalance());
		Accounts ab=repo.save(acct);
//		accountService.createAccount(acct);
		return new ResponseEntity<>(ab,HttpStatus.OK);
		
	}

	// checkBalance
	@GetMapping("/account/{acctID}/balance")
	public int getBalance(@PathVariable int acctID) throws UserNotFoundException {
		
		if( accountService.getBalance(acctID) !=0)
		{
			try {
			return accountService.getBalance(acctID);
			}
			catch(Exception e)
			{
throw new UserNotFoundException("Not found");
}
		}
		return acctID;
		}
//		if(a !=0)
//		{
//		return new ResponseEntity<>(a,HttpStatus.OK);
//		}
//		else
//		{
//			return new ResponseEntity<>("not",HttpStatus.NO_CONTENT);
//		}
	
	

	// depositAmount
	@PutMapping("/account/{acctID}/deposit/{amount}")
	public ResponseEntity<?> depositAmount(@PathVariable int acctID, @PathVariable int amount) throws UserNotFoundException {
			if(getAccountInfo(acctID)!=null)
			{
				try {
		int initBal = getBalance(acctID);
		accountService.depositAmount(acctID, amount);
		Logger logger = new Logger(acctID, "Deposited", "Success", initBal, initBal + amount);
		loggerController.addLog(logger);
		return new ResponseEntity<>(logger,HttpStatus.OK);
		}
			
		catch(Exception e) {
		
			return new ResponseEntity<>("Please check acctId and deposit amt",HttpStatus.NOT_FOUND);
	
		}
		}
			return null;
	}

	// withdrawAmount
	@PutMapping("/account/{acctID}/withdraw/{amount}")
	public ResponseEntity<?> withdrawAmount(@PathVariable int acctID, @PathVariable int amount) throws UserNotFoundException {
		if(getAccountInfo(acctID)!=null)
		{
			try {
		int initBal = getBalance(acctID);
		accountService.withdrawAmount(acctID, amount);
		Logger logger = new Logger(acctID, "Withdrawn", "Success", initBal, initBal - amount);
		loggerController.addLog(logger);
		return new ResponseEntity<>(logger,HttpStatus.OK);
			}
			
			catch(Exception e) {
			
				return new ResponseEntity<>("Please check acctId and withdraw amt",HttpStatus.NOT_FOUND);
		
			}
			}
				return null;
	}

	// transferAmount
	@PutMapping("/account/{acctID}/transfer/{destAcctID}/{amount}")
	public ResponseEntity<?> transferAmount(@PathVariable int acctID, @PathVariable int destAcctID, @PathVariable int amount) throws UserNotFoundException {
		if(getAccountInfo(acctID)!=null && 	getAccountInfo(destAcctID)!=null)
		{
			try {
		int initBalSender = getBalance(acctID);
		int initBalReceiver = getBalance(destAcctID);
		accountService.transferAmount(acctID, destAcctID, amount);
		Logger loggerSender = new Logger(acctID, "Transferred", "Success", initBalSender, initBalSender - amount);
		loggerController.addLog(loggerSender);
		Logger loggerReceiver = new Logger(destAcctID, "Received", "Success", initBalReceiver,
				initBalReceiver + amount);
		loggerController.addLog(loggerReceiver);
		return new ResponseEntity<>(loggerReceiver,HttpStatus.OK);}
			catch(Exception e)
			{
				return new ResponseEntity<>("Please check properly before u transfer amt",HttpStatus.NOT_FOUND);

			}
		}
		return null;

	}

	// deleteAccount
	@DeleteMapping("/account/{acctID}")
	public ResponseEntity<String> deleteAccount(@PathVariable int acctID) {
		if(repo.findById(acctID)!=null)
		{
			try {
		accountService.deleteAccount(acctID);
//		loggerController.deleteLog(acctID);
		return new ResponseEntity<>("Successfully deleted",HttpStatus.OK);
		}
			catch(Exception e)
			{
				return new ResponseEntity<>("AcctId doesnt exists to delete",HttpStatus.OK);

			}
		}
		return null;
	}

	// getAccountInfo
	@GetMapping("/account/{acctID}")
	public ResponseEntity<?> getAccountInfo(@PathVariable int acctID) {
		Accounts a= accountService.getAccountInfo(acctID);
		if(a !=null)
		{
		return new ResponseEntity<>(a,HttpStatus.OK);
		}
		else
		{
		return new ResponseEntity<>("AcctID doesnt exists Please check",HttpStatus.NOT_FOUND);
		}
	}

}
