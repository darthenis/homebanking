package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {
			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Account account1 = new Account("VIN001", LocalDate.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500.0);
			Transaction trans1 = new Transaction(TransactionType.CREDIT, 2500.0, "deposit", LocalDateTime.now());
			Transaction trans2 = new Transaction(TransactionType.DEBIT, 500.0, "extraction", LocalDateTime.now());
			Transaction trans3 = new Transaction(TransactionType.CREDIT, 6600.0, "deposit", LocalDateTime.now());
			Transaction trans4 = new Transaction(TransactionType.DEBIT, 1500.0, "extraction", LocalDateTime.now());
			Transaction trans5 = new Transaction(TransactionType.CREDIT, 3000.0, "deposit", LocalDateTime.now());
			Transaction trans6 = new Transaction(TransactionType.DEBIT, 1500.0, "extraction", LocalDateTime.now());
			Transaction trans7 = new Transaction(TransactionType.CREDIT, 3600.0, "deposit", LocalDateTime.now());
			Transaction trans8 = new Transaction(TransactionType.DEBIT, 1000.0, "extraction", LocalDateTime.now());
			Transaction trans9 = new Transaction(TransactionType.CREDIT, 1500.0, "deposit", LocalDateTime.now());
			Transaction trans10 = new Transaction(TransactionType.DEBIT, 900.0, "extraction", LocalDateTime.now());
			Transaction trans11 = new Transaction(TransactionType.CREDIT, 9000.0, "deposit", LocalDateTime.now());
			Transaction trans12 = new Transaction(TransactionType.DEBIT, 2600.0, "extraction", LocalDateTime.now());
			Transaction trans13 = new Transaction(TransactionType.CREDIT, 1000.0, "deposit", LocalDateTime.now());
			Transaction trans14 = new Transaction(TransactionType.DEBIT, 200.0, "extraction", LocalDateTime.now());
			Transaction trans15 = new Transaction(TransactionType.CREDIT, 5000.0, "deposit", LocalDateTime.now());
			Transaction trans16 = new Transaction(TransactionType.DEBIT, 6000.0, "extraction", LocalDateTime.now());
			Transaction trans17 = new Transaction(TransactionType.CREDIT, 2600.0, "deposit", LocalDateTime.now());
			Transaction trans18 = new Transaction(TransactionType.DEBIT, 1000.0, "extraction", LocalDateTime.now());
			Transaction trans19 = new Transaction(TransactionType.CREDIT, 800.0, "deposit", LocalDateTime.now());
			Transaction trans20 = new Transaction(TransactionType.DEBIT, 7600.0, "extraction", LocalDateTime.now());

			account1.addTransaction(trans1);
			account1.addTransaction(trans2);
			account2.addTransaction(trans3);
			account2.addTransaction(trans4);
			account1.addTransaction(trans5);
			account1.addTransaction(trans6);
			account2.addTransaction(trans7);
			account2.addTransaction(trans8);
			account1.addTransaction(trans9);
			account1.addTransaction(trans10);
			account2.addTransaction(trans11);
			account2.addTransaction(trans12);
			account1.addTransaction(trans13);
			account1.addTransaction(trans14);
			account2.addTransaction(trans15);
			account2.addTransaction(trans16);
			account1.addTransaction(trans17);
			account1.addTransaction(trans18);
			account2.addTransaction(trans19);
			account2.addTransaction(trans20);
			cli1.addAccount(account1);
			cli1.addAccount(account2);
			clientRepository.save(cli1);
			accountRepository.save(account1);
			accountRepository.save(account2);
			transactionRepository.save(trans1);
			transactionRepository.save(trans2);
			transactionRepository.save(trans3);
			transactionRepository.save(trans4);
			transactionRepository.save(trans5);
			transactionRepository.save(trans6);
			transactionRepository.save(trans7);
			transactionRepository.save(trans8);
			transactionRepository.save(trans9);
			transactionRepository.save(trans10);
			transactionRepository.save(trans11);
			transactionRepository.save(trans12);
			transactionRepository.save(trans13);
			transactionRepository.save(trans14);
			transactionRepository.save(trans15);
			transactionRepository.save(trans16);
			transactionRepository.save(trans17);
			transactionRepository.save(trans18);
			transactionRepository.save(trans19);
			transactionRepository.save(trans20);


		};
	}
}
