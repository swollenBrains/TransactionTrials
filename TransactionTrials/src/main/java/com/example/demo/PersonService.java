package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person createPerson(String firstName, String lastName) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        return personRepository.save(person);
    }

    @Transactional(TxType.REQUIRES_NEW)
    public Person createPersonInNewTransaction(String firstName, String lastName, boolean throwException) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        if(throwException) {
            throw new RuntimeException("intentional exception");
        }
        return personRepository.save(person);
    }

    @Transactional
    public Person editPersonFirstNameButNotSave(Person person, String firstName) {
        person.setFirstName(firstName);
        return person;
    }

    @Transactional
    public Person fetchPersonAndEditFirstNameButNotSave(Integer id, String firstName) {
        Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id : "+id));
        System.out.println("Name change from "+person.getFirstName() + " to "+ firstName);
        person.setFirstName(firstName);
        return person;
    }

    @Transactional(TxType.REQUIRES_NEW)
    public void fetchPersonAndThrowException(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id : "+id));
        System.out.println("Name is "+person.getFirstName());
        throw new RuntimeException("Intentional exception");
    }

    public Optional<Person> getPerson(Integer id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Person editPersonFirstNameAndSave(Person person, String firstName) {
        person.setFirstName(firstName);
        return personRepository.save(person);
    }


}
