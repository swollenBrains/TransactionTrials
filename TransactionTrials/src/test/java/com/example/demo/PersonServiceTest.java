package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import javax.transaction.Transactional;

@SpringBootTest
class PersonServiceTest {

    private static final String ORIGINAL_FIRST_NAME = "Ankit";
    private static final String MODIFIED_FIRST_NAME = "Ankit1";
    private static final String LAST_NAME = "Gupta";

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonServiceWrapper personServiceWrapper;

    @Test
    void editPersonFirstNameButNotSave() {
        Person person = personService.createPerson(ORIGINAL_FIRST_NAME, LAST_NAME);
        personService.editPersonFirstNameButNotSave(person, MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        //This does not save because the person is unmanaged
        Assertions.assertEquals(ORIGINAL_FIRST_NAME, resultingPerson.get().getFirstName());
    }

    @Test
    @Transactional
    void editPersonInTransactionButDontCallSave() {
        Person person = personService.createPerson(ORIGINAL_FIRST_NAME, LAST_NAME);
        personService.editPersonFirstNameButNotSave(person, MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        //this saves because now person is a managed entity
        Assertions.assertEquals(MODIFIED_FIRST_NAME, resultingPerson.get().getFirstName());
    }

    @Test
    void editPersonFirstNameButNotSaveWhenPersonIsCreatedInNewTransaction() {
        Person person = personService.createPersonInNewTransaction(ORIGINAL_FIRST_NAME, LAST_NAME, false);
        personService.editPersonFirstNameButNotSave(person, MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        //This does not save because the person is unmanaged
        Assertions.assertEquals(ORIGINAL_FIRST_NAME, resultingPerson.get().getFirstName());
    }

    @Test
    void checkIfUpdateBeforeExceptionInNewTransactionIsAlreadyFlushed() {
        Integer personId = null;
        try {
            Person person = personService.createPersonInNewTransaction(ORIGINAL_FIRST_NAME, LAST_NAME, false);
            personId = person.getId();
            personServiceWrapper.editPersonAndThrowExeptionInsideNewTransaction(personId, MODIFIED_FIRST_NAME);
        } catch(RuntimeException e) {
            //doNothing
        }
        Person finalState = personService.getPerson(personId).get();
        Assertions.assertEquals(ORIGINAL_FIRST_NAME, finalState.getFirstName());
        //This proves that even though we threw an exception inside a new transaction the changes in outer transaction were also not committed.
    }

    @Test
    @Transactional
    void editPersonInTransactionButDontCallSaveWhenPersonIsCreatedInNewTransaction() {
        Person person = personService.createPersonInNewTransaction(ORIGINAL_FIRST_NAME, LAST_NAME, false);
        personService.editPersonFirstNameButNotSave(person, MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        //this saves because person is created in a new transaction and when returned its not a managed entity
        Assertions.assertEquals(ORIGINAL_FIRST_NAME, resultingPerson.get().getFirstName());
    }

    @Test
    void editPersonInTransactionAndCallSave() {
        Person person = personService.createPerson(PersonServiceTest.ORIGINAL_FIRST_NAME, LAST_NAME);
        personService.editPersonFirstNameAndSave(person, MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        Assertions.assertEquals(MODIFIED_FIRST_NAME, resultingPerson.get().getFirstName());
    }

    @Test
    void fetchPersonAndEditFirstNameButNotSave() {
        Person person = personService.createPerson(PersonServiceTest.ORIGINAL_FIRST_NAME, LAST_NAME);
        personService.fetchPersonAndEditFirstNameButNotSave(person.getId(), MODIFIED_FIRST_NAME);
        Optional<Person> resultingPerson = personService.getPerson(person.getId());
        Assertions.assertEquals(MODIFIED_FIRST_NAME, resultingPerson.get().getFirstName());
    }

}
