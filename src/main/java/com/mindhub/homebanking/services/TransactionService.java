package com.mindhub.homebanking.services;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import com.mindhub.homebanking.dto.PayApplicationDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TransactionService {


    void writeTableHeader(PdfPTable pdfPTable);

    void writeTableData(PdfPTable pdfPTable) throws ParseException;

    void export(HttpServletResponse response, LocalDateTime dateFrom, LocalDateTime dateThru, String accountNumber, String fullName, Double balance) throws DocumentException, IOException, ParseException;

    void UserPDFExporter(List<Transaction> transactions);

    void createTransaction(Authentication authentication, String numberOrigin, String numberDestination, Double amount) throws EntityNotFoundException, AccessDeniedException, Exception;

    void createPay(PayApplicationDTO payApplicationDTO) throws IllegalArgumentException, EntityNotFoundException, Exception;

    List<Transaction> filterTransactions(Authentication authentication, LocalDateTime dateFrom, LocalDateTime dateThru, Long accountId) throws EntityNotFoundException;
}
