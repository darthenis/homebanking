package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class HomebankingApplication {


	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository,
									  AccountRepository accountRepository,
									  TransactionRepository transactionRepository,
									  LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository,
									  CardRepository cardRepository) {
		return (args) -> {

			Loan loan1 = new Loan("Mortgage", 500000.0, List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal", 100000.0, List.of(6,12,24));
			Loan loan3 = new Loan("Automotive", 300000.0, List.of(6,12,24,36));

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			Client admin = new Client("admin", "admin", "admin@mindhub.com", passwordEncoder.encode("admin"));
			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("password"));
			Client cli2 = new Client("Uriel", "Montero", "uriel@mindhub.com", passwordEncoder.encode("password"));

			Set<String> numbersCard = new HashSet<>();

			Set<Card> cards = new HashSet<>();

			do{

				int randomValue1 =  (int)Math.floor(Math.random() * (9999 + 1));
				int randomValue2 =  (int)Math.floor(Math.random() * (9999 + 1));
				int randomValue3 =  (int)Math.floor(Math.random() * (9999 + 1));
				int randomValue4 =  (int)Math.floor(Math.random() * (9999 + 1));

				numbersCard.add(String.format("%04d", randomValue1) + " " + String.format("%04d", randomValue2) + " " + String.format("%04d", randomValue3) + " " + String.format("%04d", randomValue4));

			}while(numbersCard.size() <= 2);

			byte indexAux = 0;

			for(String number:numbersCard){

				String randomCvv = String.format("%03d", (int)Math.floor(Math.random() * (999 + 1)));

				CardType cardType;

				CardColor cardColor;

				String nameComplete;

				if(indexAux == 0){

					cardColor = CardColor.GOLD;
					cardType = CardType.DEBIT;
					nameComplete = cli1.getFirstName() +" "+ cli1.getLastName();

				}else if(indexAux == 1){

					cardColor = CardColor.SILVER;
					cardType = CardType.CREDIT;
					nameComplete = cli1.getFirstName() +" "+ cli1.getLastName();

				}else{

					cardColor = CardColor.TITANIUM;
					cardType = CardType.CREDIT;
					nameComplete = cli2.getFirstName() +" "+ cli2.getLastName();

				}

				Card card = new Card(cardType, cardColor, nameComplete, number, randomCvv, LocalDate.now(), LocalDate.now().plusYears(5));

				if(indexAux < 2){

					cli1.addCard(card);

				} else {

					cli2.addCard(card);

				}

				cards.add(card);

				indexAux++;
			}



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

				double randomValue =  Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);

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
			clientRepository.save(admin);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);


			for(Transaction trans : transactions){

				transactionRepository.save(trans);

			}

			for(Card card : cards){

				cardRepository.save(card);

			}

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

		};
	}
}
