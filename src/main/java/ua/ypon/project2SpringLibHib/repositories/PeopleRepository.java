package ua.ypon.project2SpringLibHib.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;

import java.util.List;

/**
 * net.ukr@caravell 01/05/2023s
 */
@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}

