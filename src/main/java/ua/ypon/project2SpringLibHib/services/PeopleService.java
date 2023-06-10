package ua.ypon.project2SpringLibHib.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;
import ua.ypon.project2SpringLibHib.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;

/**
 * net.ukr@caravell 01/05/2023
 */
/*
Клас "PeopleService" є сервісним класом,
який надає функціональність для роботи з об'єктами "Person".
Він містить анотації, які вказують на його роль у контексті Spring.
 */
@Service//Анотація "@Service" вказує, що цей клас є сервісом, який виконує бізнес-логіку.
// Він буде керувати операціями з об'єктами "Person".
@Transactional(readOnly = true)//Анотація "@Transactional(readOnly = true)" вказує,
// що методи сервісу виконуються в межах транзакції з параметром "readOnly = true",
// що дозволяє лише читати дані з бази даних без змін.
public class PeopleService {

    private final PeopleRepository peopleRepository;

    //Залежність до репозиторію "PeopleRepository" встановлюється за допомогою анотації "@Autowired".
    // Це забезпечує ін'єкцію залежностей,
    // тобто Spring автоматично створить і надасть екземпляр репозиторію класу "PeopleRepository"
    // для використання в сервісі.
    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }


    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    //Метод "findOne(int id)" шукає об'єкт "Person" за заданим ідентифікатором.
    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    //Метод "save(Person person)" зберігає новий об'єкт "Person" у базі даних
    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    //Метод "update(int id, Person updatedPerson)" оновлює існуючий об'єкт "Person" з новими даними.
    // Він приймає ідентифікатор та оновлений об'єкт "Person" і зберігає оновлення у базі даних.
    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    //Метод "delete(int id)" видаляє об'єкт "Person" з бази даних за заданим ідентифікатором.
    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }
}
/*
Усі методи з анотацією "@Transactional" виконуються в межах однієї транзакції,
що дозволяє забезпечити цілісність даних та автоматичне керування транзакціями у контексті бази даних.
 */
