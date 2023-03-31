package com.madas.cs556.controller;

import com.madas.cs556.entity.Person;
import com.madas.cs556.model.AuthorizationRequest;
import com.madas.cs556.model.CreateTableRequest;
import com.madas.cs556.repository.PersonRepository;
import com.madas.cs556.services.authorizationServices.AuthorizationService;
import com.madas.cs556.services.authorizationServices.constants.TransferMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    AuthorizationService service;
    @Autowired
    private PersonRepository personRepository;

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Person> getAllUsers() {
        return personRepository.findAll();
    }

    @PostMapping(path = "/createTable")
    public @ResponseBody String createTable(@RequestBody CreateTableRequest createTableRequest) {
        service.createTable(createTableRequest.getTableName(), createTableRequest.getOwners(), createTableRequest.isOwnershipReq());
        return "Success!";
    }

    @GetMapping(path = "/printAcceptanceStatus/{tableName}")
    public @ResponseBody String printAcceptanceStatus(@PathVariable String tableName) {
        return service.printAcceptanceStatus(tableName);
    }


    @GetMapping(path = "/printAuthorization/{tableName}")
    public @ResponseBody String printAuthorizationForTable(@PathVariable String tableName) {
        return service.printAdmins(tableName);
    }

    @PostMapping(path = "/transferOwnershipRecursive")
    public @ResponseBody String transferOwnershipRecursive(@RequestBody AuthorizationRequest request) {
        return service.transferOwnership(request.tableName, request.from, request.to, TransferMode.RECURSIVE_REVOKE);
    }

    @PostMapping(path = "/transferOwnershipGrantor")
    public @ResponseBody String transferOwnershipGrantor(@RequestBody AuthorizationRequest request) {
        return service.transferOwnership(request.tableName, request.from, request.to, TransferMode.GRANTOR_TRANSFER);
    }

    @PostMapping(path = "/delegate")
    public @ResponseBody String delegate(@RequestBody AuthorizationRequest request) {
        return service.delegateFromTo(request.tableName, request.from, request.to);
    }

    @PostMapping(path = "/removeDelegation")
    public @ResponseBody String removeDelegation(@RequestBody AuthorizationRequest request) {
        return service.removeDelegation(request.tableName, request.from, request.to);
    }

    @GetMapping(path = "/acceptOwnership/{table}/{to}")
    public @ResponseBody String acceptOwnership(@PathVariable("table") String tableName, @PathVariable("to") Integer to) {
        return service.acceptOwnership(tableName, to);
    }

    @GetMapping(path = "/test")
    @ResponseBody
    public AuthorizationRequest sample() {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setFrom(1);
        request.setTo(2);
        request.setTableName("madas");
        return request;
    }

    @GetMapping(path = "/test2")
    @ResponseBody
    public CreateTableRequest createTableRequest() {
        CreateTableRequest request = new CreateTableRequest();
        request.setTableName("madas");
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        request.setOwners(arrayList);
        request.setTableName("madas");
        request.setOwnershipReq(true);
        return request;
    }


}
