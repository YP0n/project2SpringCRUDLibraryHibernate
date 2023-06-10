package ua.ypon.project2SpringLibHib.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;
import ua.ypon.project2SpringLibHib.repositories.BooksRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * net.ukr@caravell 01/05/2023
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BooksRepository booksRepository;

    @Autowired
    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
   }

    // Метод findAll повертає список всіх книг.
    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    // Метод findAllPage повертає список книг з пагінацією.
    public List<Book> findAllPage(Pageable pageable) {
                return booksRepository.findAll(pageable).getContent();
    }

    // Метод findAllSortedByYear повертає список книг, відсортованих за роком видання.
    public List<Book> findAllSortedByYear() {
        Sort sort = Sort.by("year");
        return booksRepository.findAll(sort);
    }

    // Метод findOne повертає одну книгу за заданим ідентифікатором.
    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    // Метод getBookOwner повертає власника книги за заданим ідентифікатором.
    //визови в сервісі так-як це всередені транзакції
    public Optional<Person> getBookOwner(int id) {
        Optional<Book> bookOptional = booksRepository.findBookById(id);
        return bookOptional.map(Book::getOwner);
    }

    // Метод findBookByOwner повертає список книг, що належать заданій особі.
    public List<Book>findBookByOwner(Person owner) {
        return booksRepository.findBookByOwner(owner);
    }

    // Метод save зберігає нову книгу або оновлює існуючу.
    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    // Метод update оновлює існуючу книгу за заданим ідентифікатором.
    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    // Метод delete видаляє книгу за заданим ідентифікатором.
    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    //Вивільняє книгу(коли повертають)
    @Transactional
    public void release(int id) {
        Book book = booksRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Книга з ID " + id + " не знайдена"));
        book.setOwner(null);
        booksRepository.save(book);
    }

    //Призначають книгу людині(коли забирає)
    @Transactional
    public void assign(int id, Person person) {
        Book book = booksRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Книга з ID " + id + " не знайдена"));
        book.setOwner(person);
        book.setCreateAt(new Date());
        booksRepository.save(book);
    }

    // Метод reBook перевіряє, чи книгу можна перезабрати (якщо пройшло більше 10 днів з дати видачі).
    public boolean reBook(Book book) {
        if(book.getCreateAt() != null && new Date().getTime()
                - book.getCreateAt().getTime() >= 864000000){
            book.setTakeBook(true);
            return true;
        }
        return false;
    }


    // Метод searchBookByTitleStartsWith повертає список книг, які починаються з заданої назви.
    @Transactional
    public List<Book> searchBookByTitleStartsWith(String title) {
        return booksRepository.searchBookByTitleStartsWith(title);
    }
}
