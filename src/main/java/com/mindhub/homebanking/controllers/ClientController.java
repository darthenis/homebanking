package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientCreateDTO;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.dto.ClientEditDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.impl.ClientServiceImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;


import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController{

    @Autowired
    ClientServiceImpl clientService;


    @PostMapping("/clients")
    public ResponseEntity<Object> register(
            @RequestBody ClientCreateDTO clientCreateDTO, Authentication authentication) {

        if (clientCreateDTO.getFirstName().isEmpty()) {

            return new ResponseEntity<>("Missing firstName", HttpStatus.FORBIDDEN);

        } else if (clientCreateDTO.getLastName().isEmpty()) {

            return new ResponseEntity<>("Missing lastName", HttpStatus.FORBIDDEN);

        } else if (clientCreateDTO.getEmail().isEmpty()) {

            return new ResponseEntity<>("Missing email", HttpStatus.FORBIDDEN);

        } else if (clientCreateDTO.getPassword().isEmpty()) {

            return new ResponseEntity<>("Missing password", HttpStatus.FORBIDDEN);

        }

        if (clientService.existsByEmail(clientCreateDTO.getEmail())) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }
        try{

            clientService.createUser(clientCreateDTO, authentication);

            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch(Exception exception){

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }

    @PostMapping("/clients/active")
    public ResponseEntity<?> activeClient(@RequestParam String token){

        if(token.isEmpty()){
            return new ResponseEntity<>("missing token", HttpStatus.BAD_REQUEST);
        }

        try{

            clientService.activeUser(token);

            return new ResponseEntity<>(HttpStatus.OK);

        }catch(Exception exception){

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);

        }

    }

    @PostMapping("/clients/resend")
    public ResponseEntity<?> resendToken(@RequestParam String email) throws IOException {

        if(email == null){

            return new ResponseEntity<>("missing email", HttpStatus.BAD_REQUEST);

        }
        try{

            clientService.resendActiveEmail(email);

            return new ResponseEntity<>(HttpStatus.OK);

        }catch(EntityNotFoundException entityNotFoundException){


            return new ResponseEntity<>(entityNotFoundException.getMessage(), HttpStatus.FORBIDDEN);

        }


    }

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {

        List<Client> clients = clientService.findAll();

        return clients.stream().map(ClientDTO::new).collect(toList());

    }

    @GetMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {

        return new ClientDTO(clientService.getCurrentClient(authentication));

    }

    @PatchMapping("/clients/current")
    public ResponseEntity<?> editClient(Authentication authentication,
                                               @RequestBody ClientEditDTO clientEditDTO) {

            clientService.editClient(clientEditDTO, authentication);

            return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {

        return clientService.findById(id).map(ClientDTO::new).orElse(null);

    }

    @PostMapping("/clients/current/avatar")
    public ResponseEntity<?> upload( Authentication authentication, @RequestParam("avatarFile") MultipartFile multipartFile) throws FileSizeLimitExceededException {

        if(multipartFile.isEmpty()){

            return new ResponseEntity<>("Missing multipartiFile", HttpStatus.BAD_REQUEST);

        }
        try{

            clientService.uploadAvatar(multipartFile, authentication);

            return new ResponseEntity<>(HttpStatus.OK);

        }catch(IOException ioException){

            return new ResponseEntity<>(ioException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }

    @PutMapping("/clients/current/changePassword")
    public ResponseEntity<?> editPassword(@RequestParam String password, String newPassword, Authentication authentication){

        try{

            clientService.editPassword(authentication, password, newPassword);

            return new ResponseEntity<>("Password changed succesfully", HttpStatus.OK);

        }catch(AccessDeniedException accessDeniedException){

            return new ResponseEntity<>(accessDeniedException.getMessage(), HttpStatus.FORBIDDEN);

        }



    }

}
