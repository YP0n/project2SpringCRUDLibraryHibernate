package ua.ypon.project2SpringLibHib.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;
import ua.ypon.project2SpringLibHib.services.BookService;
import ua.ypon.project2SpringLibHib.services.PeopleService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * net.ukr@caravell 01/05/2023
 */
@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model,
                        @RequestParam(required = false) Integer page,
                        @RequestParam(required = false) Integer books_per_page,
                        @RequestParam(required = false) Boolean sort_by_year) {
        // Метод, що обробляє GET-запит до шляху "/books"
        // Відображає список книг з можливістю сортування і пагінації
        // Отримання параметрів з URL-запиту
        // page - номер сторінки
        // books_per_page - кількість книг на сторінці
        // sort_by_year - сортування за роком видання
        if (page != null && books_per_page != null) {
            // Якщо передані параметри сторінки та кількості книг на сторінці

            Sort sort = sort_by_year != null && sort_by_year ? Sort.by("year") : Sort.unsorted();
            Pageable pageable = PageRequest.of(page != null ? page : 0, books_per_page != null ? books_per_page : 3, sort);

            // Отримання списку книг з пагінацією та сортуванням
            List<Book> books = bookService.findAllPage(pageable);
            model.addAttribute("books", books);
        } else if (Boolean.TRUE.equals(sort_by_year)) {
            // Якщо передано параметр сортування за роком видання

            // Отримання списку книг, відсортованих за роком видання
            List<Book> books = bookService.findAllSortedByYear();
            model.addAttribute("books", books);
        } else {
            // Якщо параметри не передано, відображаються всі книги без сортування
            List<Book> books = bookService.findAll();
            model.addAttribute("books", books);
        }
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @ModelAttribute("person") Person person) {
        // Метод, що обробляє GET-запит до шляху "/books/{id}"
        // Відображає деталі конкретної книги за її ідентифікатором (ID)

        // Отримання книги з бази даних за її ідентифікатором
        model.addAttribute("book", bookService.findOne(id));

        // Отримання власника книги (якщо книга зайнята) або список осіб, кому можна призначити книгу (якщо книга вільна)
        Optional<Person> bookOwner = bookService.getBookOwner(id);//якщо книга зайнята-показуємо ким,а як ні-список
        if (bookOwner.isPresent()) {
            model.addAttribute("owner", bookOwner.get());//якщо книга зайнята-показуємо ким
        } else {
            model.addAttribute("people", peopleService.findAll());//а як ні-список кому призначити
        }
        return "books/show";
    }

    // Решта методів контролера (newBook, create, edit, update, delete, release, assign, search) мають аналогічний принцип дії
    // Вони обробляють відповідні HTTP-запити та виконують відповідні дії з об'єктами Book
    // Наприклад, метод create обробляє POST-запит на шляху "/books"
    // Він створює нову книгу, отримуючи дані з форми, та зберігає її в базі даних через bookService.save(book)
    // Після цього перенаправляє користувача на шлях "/books" з використанням "redirect:/books"

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()//valid- проверка условий. BindingResult-объект для хранения ошибок при
    // создании Book и введении неValid.
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {//с помощью @ModelAttribute создаем новую
        // книгу(пустой объект класса Book)

        if (bindingResult.hasErrors())
            return "books/new";

        bookService.save(book);//затем эта же аннотация внедряет значение из формы
        // в этот объект класса Person. Затем она этот объект добавляет в модель.

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", bookService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";
        bookService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @PostMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {//метод для вивільнення книги
        bookService.release(id);
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
//В selectedPerson призначено тільки поле id, всі інші поля - null
        bookService.assign(id, selectedPerson);//метод для присвоєння книги
        return "redirect:/books/" + id;
    }

    /*
    Цей код додає атрибут books до моделі, який містить результати пошуку,
    або атрибут error, який містить повідомлення про те, що книги не знайдено,
    якщо результати пошуку порожній. Потім він повертає відповідні представлення,
    в залежності від результату пошуку.
     */
    @GetMapping("/search")
    public String search(@RequestParam("title") String title, Model model) {
        List<Book> books = bookService.searchBookByTitleStartsWith(title);
        if (books.isEmpty()) {
            model.addAttribute("error", "Книги не знайдено");
        } else {
            model.addAttribute("books", books);
            List<Person> owners =books.stream().map(Book::getOwner).collect(Collectors.toList());
            model.addAttribute("owners", owners);
        }
        return "books/search";
    }
}
