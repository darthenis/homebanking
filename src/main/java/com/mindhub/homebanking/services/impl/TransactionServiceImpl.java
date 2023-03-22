package com.mindhub.homebanking.services.impl;

import com.lowagie.text.DocumentException;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.mindhub.homebanking.dto.PayApplicationDTO;
import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.DateUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ClientServiceImpl clientService;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    CardServiceImpl cardService;

    private List<Transaction> transactions;

    @Override
    public void UserPDFExporter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void createTransaction(Authentication authentication, String numberOrigin, String numberDestination, Double amount) throws AccessDeniedException, EntityNotFoundException, Exception {

        Client client = clientService.findByEmail(authentication.getName());

        Optional<Account> accountFrom = accountService.findByNumber(numberOrigin);

        if(accountFrom.isEmpty()) throw new EntityNotFoundException("Origin account doesn't exist");

        Optional<Account> accountDestination = accountService.findByNumber(numberDestination);

        if(accountDestination.isEmpty()) throw new EntityNotFoundException("The destination account does not exist");

        if(client.getId() != accountFrom.get().getClient().getId()) throw new AccessDeniedException("The account doesn't belong to the authenticated client");

        if(accountFrom.get().getBalance() < amount) throw new Exception("The client has no funds");

        accountDestination.get().setBalance(accountDestination.get().getBalance() + amount);

        accountFrom.get().setBalance(accountFrom.get().getBalance() - amount);

        Transaction transactionDestination = new Transaction(TransactionType.CREDIT, amount, "Credit from account " + accountFrom.get().getNumber(), LocalDateTime.now(), accountDestination.get().getBalance());

        Transaction transactionFrom = new Transaction(TransactionType.DEBIT, amount, "Debit to account " + accountDestination.get().getNumber(), LocalDateTime.now(),accountFrom.get().getBalance());

        accountDestination.get().addTransaction(transactionDestination);

        accountFrom.get().addTransaction(transactionFrom);

        accountService.save(accountDestination.get());

        accountService.save(accountFrom.get());

        transactionRepository.save(transactionDestination);

        transactionRepository.save(transactionFrom);

    }

    @Override
    public void createPay(PayApplicationDTO payApplicationDTO) throws IllegalArgumentException, EntityNotFoundException, Exception{

        if(payApplicationDTO.getNumber().isEmpty()) throw new IllegalArgumentException("missing number");
        if(payApplicationDTO.getCvv().isEmpty()) throw new IllegalArgumentException("missing cvv");
        if(payApplicationDTO.getAmount() == null) throw new IllegalArgumentException("missing amount");
        if(payApplicationDTO.getDescription().isEmpty()) throw new IllegalArgumentException("missing description");

        Card card = cardService.findByNumber(payApplicationDTO.getNumber()).orElse(null);

        if(card == null) throw new EntityNotFoundException("Card not found");

        if(!card.getCvv().equals(payApplicationDTO.getCvv())) throw new Exception("Cvv wrong");

        if(LocalDate.now().isAfter(card.getThruDate())) throw new Exception("Card is expired");

        if(card.getAccount().getBalance() < payApplicationDTO.getAmount()) throw new Exception("the account has no funds");

        Double result = card.getAccount().getBalance() - payApplicationDTO.getAmount();

        card.getAccount().setBalance(result);

        Transaction transaction = new Transaction(TransactionType.DEBIT, payApplicationDTO.getAmount(), payApplicationDTO.getDescription(), LocalDateTime.now(), card.getAccount().getBalance());

        card.getAccount().addTransaction(transaction);

        accountService.save(card.getAccount());

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> filterTransactions(Authentication authentication, LocalDateTime dateFrom, LocalDateTime dateThru, Long accountId) throws EntityNotFoundException{

        Client client = clientService.findByEmail(authentication.getName());

        Optional<Account> selectedAccount = client.getAccounts().stream().filter(account -> account.getId() == accountId).findAny();

        if(selectedAccount.isEmpty()) throw new EntityNotFoundException("Account not found");

        Set<Transaction> transactions = selectedAccount.get().getTransactions();

        if(dateFrom != null && dateThru == null) {

           return transactions.stream().filter(transaction -> transaction.getDate().isAfter(dateFrom) || transaction.getDate().equals(dateFrom)).collect(Collectors.toList());

        }

        if(dateThru != null && dateFrom != null) {

            return transactions.stream().filter(transaction -> (transaction.getDate().isBefore(dateThru) && transaction.getDate().isAfter(dateFrom)) || transaction.getDate().equals(dateFrom) || transaction.getDate().equals(dateThru) ).collect(Collectors.toList());

        }

        return new ArrayList<>(transactions);

    }

    @Override
    public void writeTableHeader(PdfPTable pdfPTable) {
        PdfPCell cell = new PdfPCell();
        Color color = new Color(115, 178, 240);
        cell.setBackgroundColor(color);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Type", font));
        pdfPTable.addCell(cell);

        cell.setPhrase(new Phrase("Amount", font));
        pdfPTable.addCell(cell);

        cell.setPhrase(new Phrase("Date", font));
        pdfPTable.addCell(cell);

        cell.setPhrase(new Phrase("Hour", font));
        pdfPTable.addCell(cell);

        cell.setPhrase(new Phrase("Balance", font));
        pdfPTable.addCell(cell);

        cell.setPhrase(new Phrase("Description", font));
        pdfPTable.addCell(cell);
    }

    @Override
    public void writeTableData(PdfPTable pdfPTable) throws ParseException {
        for (Transaction transaction : transactions) {

            pdfPTable.addCell(String.valueOf(transaction.getType()));
            pdfPTable.addCell(NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                    .format(transaction.getAmount()));
            pdfPTable.addCell(new DateUtils().formatDate(String.valueOf(transaction.getDate()).substring(0, 10)));
            pdfPTable.addCell(String.valueOf(transaction.getDate()).substring(11, 16));
            pdfPTable.addCell(NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                    .format(transaction.getBalance()));
            pdfPTable.addCell(transaction.getDescription());
        }
    }

    @Override
    public void export(HttpServletResponse response, LocalDateTime dateFrom, LocalDateTime dateThru, String accountNumber, String fullName, Double balance) throws DocumentException, IOException, ParseException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(20);
        font.setColor(37, 96, 155);

        Font font2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font2.setSize(12);
        font2.setColor(37, 96, 155);

        Paragraph title = new Paragraph("Detail account " + accountNumber, font);
        title.setAlignment(Element.ALIGN_CENTER);

        Paragraph name =  new Paragraph("Client: " + fullName, font2);

        Paragraph balanceTotal = new Paragraph("Balance total: " + NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(balance), font2);

        Paragraph dates;

        if(dateFrom != null && dateThru != null) dates = new Paragraph("Date "+ dateFrom +" to " + dateThru, font2);

        else if(dateFrom != null) dates = new Paragraph("Date from"+ dateFrom + "to today", font2);

        else dates = new Paragraph("All transactions", font2);

        document.add(title);
        document.add(name);
        document.add(balanceTotal);
        document.add(dates);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 2.0f, 2.0f, 1.0f, 2.5f, 3.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();

    }


}
