package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository,
									  AccountRepository accountRepository,
									  TransactionRepository transactionRepository,
									  LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository) {
		return (args) -> {

			Loan loan1 = new Loan("Mortgage", 500000.0, List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal", 100000.0, List.of(6,12,24));
			Loan loan3 = new Loan("Automotive", 300000.0, List.of(6,12,24,36));

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Client cli2 = new Client("Uriel", "Montero", "uriel@mindhub.com");

			Account account1 = new Account("VIN001", LocalDate.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500.0);
			Account account3 = new Account("VIN003", LocalDate.now(), 5500.0);
			Account account4 = new Account("VIN004", LocalDate.now().plusDays(1), 6500.0);

			ClientLoan clientLoan1 = new ClientLoan(400000.0, 60, cli1, loan1);
			ClientLoan clientLoan2 = new ClientLoan(50000.0, 12, cli1, loan2);
			ClientLoan clientLoan3 = new ClientLoan(100000.0, 24, cli2, loan2);
			ClientLoan clientLoan4 = new ClientLoan(200000.0, 36, cli2, loan3);

			cli1.addClientLoan(clientLoan1);
			cli1.addClientLoan(clientLoan2);

			cli2.addClientLoan(clientLoan3);
			cli2.addClientLoan(clientLoan4);

			loan1.addClientLoan(clientLoan1);
			loan2.addClientLoan(clientLoan2);
			loan2.addClientLoan(clientLoan3);
			loan3.addClientLoan(clientLoan4);

			List<Transaction> transactions = new ArrayList<>();

			for(int i = 1; i <= 40; i++){

				double randomValue =  Math.floor(Math.random() * (20000 - 500 + 1) + 500);

				String description;

				TransactionType type;

				if(i % 2 == 0){

					description = "deposit";

					type = TransactionType.CREDIT;

				} else {

					description = "extraction";

					type = TransactionType.DEBIT;
				}

				Transaction trans = new Transaction(type, randomValue, description, LocalDateTime.now());
				transactions.add(trans);

				if(i < 11){

					account1.addTransaction(trans);

				} else if(i < 21){

					account2.addTransaction(trans);

				} else if(i < 31){

					account3.addTransaction(trans);

				} else {

					account4.addTransaction(trans);

				}

			}


			cli1.addAccount(account1);
			cli1.addAccount(account2);
			cli2.addAccount(account3);
			cli2.addAccount(account4);

			clientRepository.save(cli1);
			clientRepository.save(cli2);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);


			for(Transaction trans : transactions){

				transactionRepository.save(trans);

			}

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

		};
	}
}
