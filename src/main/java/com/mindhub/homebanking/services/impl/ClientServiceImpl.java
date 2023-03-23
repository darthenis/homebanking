package com.mindhub.homebanking.services.impl;

import com.google.cloud.storage.StorageException;
import com.mindhub.homebanking.dto.ClientCreateDTO;
import com.mindhub.homebanking.dto.ClientEditDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.KeyTokenRepository;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    KeyTokenServiceImpl keyTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private KeyTokenRepository keyTokenRepository;

    @Value("${private.key.id}")
    private String privateKeyId;

    @Value("${private.key}")
    private String privateKey;

    @Value("${client.id}")
    private String clientId;


    @Override
    public void createUser(ClientCreateDTO clientCreateDTO, Authentication authentication) throws Exception {
        Client newClient;

        if (authentication == null) {

            newClient = new Client(clientCreateDTO.getFirstName(), clientCreateDTO.getLastName(), clientCreateDTO.getEmail(), passwordEncoder.encode(clientCreateDTO.getPassword()), RoleType.CLIENT);

            Account account = accountService.createFirstAccount();

            newClient.addAccount(account);

            KeyToken keyToken = keyTokenService.generateAndSendKeyToken(clientCreateDTO.getEmail(), newClient);

            newClient.addKeyToken(keyToken);

            clientRepository.save(newClient);

            accountService.save(account);

        } else {


            if (authentication.getAuthorities().stream().anyMatch(authority -> Objects.equals(authority.toString(), "ADMIN"))) {

                newClient = new Client(clientCreateDTO.getFirstName(), clientCreateDTO.getLastName(), clientCreateDTO.getEmail(), passwordEncoder.encode(clientCreateDTO.getPassword()), clientCreateDTO.getRoleType());

                clientRepository.save(newClient);

            }

        }
    }

    @Override
    public void activeUser(String token) throws Exception {

        KeyToken keyToken = keyTokenService.findByToken(token);


        if(keyToken.getExpiredAt().compareTo(LocalDateTime.now()) < 0){

            throw new Exception("expired");

        }

        Client client = keyToken.getClient();

        client.setEnabled(true);

        client.setKeyToken(null);

        clientRepository.save(client);

        Authentication authentication = new UsernamePasswordAuthenticationToken(client.getEmail(), null,
                AuthorityUtils.createAuthorityList(client.getRoleType().toString()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Override
    public void resendActiveEmail(String email) throws Exception {

        Client client = clientRepository.findByEmail(email);

        if(client == null){

            throw new EntityNotFoundException("Client not found");

        }

        if(client.getIsEnabled()){

            throw new EntityNotFoundException("Client already actived");

        }

        KeyToken keyToken = keyTokenService.generateAndSendKeyToken(client.getEmail(), client);

        client.addKeyToken(keyToken);

        clientRepository.save(client);

    }

    @Override
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public void uploadAvatar(MultipartFile multipartFile, Authentication authentication) throws FileNotFoundException, StorageException {
        UploadFileUtil uploadFileUtil = new UploadFileUtil();

        String extensionFile = uploadFileUtil.getExtension(multipartFile.getOriginalFilename());

        if(extensionFile.equals("jpg") || extensionFile.equals("jpeg") || extensionFile.equals("png")){

           throw new FileNotFoundException("Invalid extension must be jpg, jpeg or png");

        }

        try {

            Client client = clientRepository.findByEmail(authentication.getName());

            String urlImg = uploadFileUtil.upload(multipartFile, client.getId(), privateKeyId, privateKey, clientId);

            client.setAvatarUrl(urlImg);

            clientRepository.save(client);

        } catch(Exception err){

            throw new StorageException(500, "image not uploaded");

        }


    }

    @Override
    public void editClient(ClientEditDTO clientEditDTO, Authentication authentication) {

        Client client = clientRepository.findByEmail(authentication.getName());

        if (!clientEditDTO.getFirstName().isEmpty()) {

            client.setFirstName(clientEditDTO.getFirstName());
        }

        if (!clientEditDTO.getLastName().isEmpty()) {

            client.setLastName(clientEditDTO.getLastName());
        }

        if (!clientEditDTO.getEmail().isEmpty()) {

            client.setEmail(clientEditDTO.getEmail());

            authentication.setAuthenticated(false);

            Authentication auth = new UsernamePasswordAuthenticationToken(clientEditDTO.getEmail(), client.getPassword(), AuthorityUtils.createAuthorityList(client.getRoleType().toString()));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        if(!clientEditDTO.getAddress().isEmpty()) {

            client.setAddress(clientEditDTO.getAddress());
        }

        if(!clientEditDTO.getCountry().isEmpty()) {

            client.setCountry(clientEditDTO.getCountry());
        }

        if(!clientEditDTO.getTel().isEmpty()){

            client.setTel(clientEditDTO.getTel());
        }

        clientRepository.save(client);

    }

    @Override
    public Client getCurrentClient(Authentication authentication) {

        Client client = clientRepository.findByEmail(authentication.getName());

        Set<Card> cards = client.getCards().stream().filter(card -> !card.getIsDisabled()).collect(Collectors.toSet());

        Set<Account> accounts = client.getAccounts().stream().filter(account -> !account.getIsDisabled()).collect(Collectors.toSet());

        client.setCards(cards);

        client.setAccounts(accounts);

        return client;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void editPassword(Authentication authentication, String password, String newPassword) throws AccessDeniedException {

        Client client = clientRepository.findByEmail(authentication.getName());

        if(!passwordEncoder.matches(password, client.getPassword())){

            throw new AccessDeniedException("Invalid password");

        }

        client.setPassword(passwordEncoder.encode(newPassword));

        clientRepository.save(client);

    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }

}
