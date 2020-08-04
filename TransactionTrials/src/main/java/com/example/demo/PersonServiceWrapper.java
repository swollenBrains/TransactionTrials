package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PersonServiceWrapper {

    private final PersonService personService;

    @Autowired
    public PersonServiceWrapper(PersonService personService) {
        this.personService = personService;
    }

    @Transactional
    public Person editPersonAndThrowExeptionInsideNewTransaction(Integer personId, String newFirstName) {
        Person person = personService.getPerson(personId).get();
        person.setFirstName(newFirstName);
        personService.fetchPersonAndThrowException(person.getId());
        return person;
    }
}
