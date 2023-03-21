package com.mindhub.homebanking.services;

import com.google.cloud.storage.StorageException;
import com.mindhub.homebanking.dto.ClientCreateDTO;
import com.mindhub.homebanking.dto.ClientEditDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientService {

    void createUser(ClientCreateDTO clientCreateDTO, Authentication authentication) throws Exception;

    void activeUser(String token) throws Exception;

    void resendActiveEmail(String email) throws EntityNotFoundException;

    boolean existsByEmail(String email);

    Optional<Client> findById(Long id);

    void uploadAvatar(MultipartFile multipartFile, Authentication authentication) throws FileNotFoundException, StorageException;

    void editClient(ClientEditDTO clientEditDTO, Authentication authentication);

    Client getCurrentClient(Authentication authentication);

    List<Client> findAll();

    void editPassword(Authentication authentication, String password, String newPassword) throws AccessDeniedException;

    Client findByEmail(String email);

    void save(Client client);

}
