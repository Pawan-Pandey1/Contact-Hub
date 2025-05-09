package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

    //Save contacts
    Contact save(Contact contact);

    //Update contacts
    Contact update(Contact contact);

    //Get contacts
    List<Contact>getAll();

    //Get contact by ID
    Contact getById(String id);

    //Delete contact
    void delete(String id);

    //Search Contact
    List<Contact>search(String name, String email, String phoneNumber);

    // get contacts by userId
    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user,int page,int size,String sortFiled,String sortDirection);
}
