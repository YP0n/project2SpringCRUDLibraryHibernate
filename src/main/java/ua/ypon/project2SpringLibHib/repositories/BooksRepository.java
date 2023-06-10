package ua.ypon.project2SpringLibHib.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;

import java.util.List;
import java.util.Optional;

/**
 * net.ukr@caravell 01/05/2023
 */
@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    // Метод findBookByOwner повертає список книг, що належать заданій особі.
    List<Book> findBookByOwner(Person owner);

    // Метод findBookById повертає книгу за заданим ідентифікатором (ID).
    Optional<Book> findBookById (int id);

    // Метод searchBookByTitleStartsWith повертає список книг, які починаються з заданої назви.
    List<Book> searchBookByTitleStartsWith(String title);
}
