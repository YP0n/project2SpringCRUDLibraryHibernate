package ua.ypon.project2SpringLibHib.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.ypon.project2SpringLibHib.models.Book;
import ua.ypon.project2SpringLibHib.models.Person;
import ua.ypon.project2SpringLibHib.services.BookService;
import ua.ypon.project2SpringLibHib.services.PeopleService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * net.ukr@caravell 01/05/2023
 */
/*
Клас "PeopleController" є контролером, який відповідає за обробку запитів,
пов'язаних з об'єктами "Person". Він містить анотації,
які вказують на його роль у контексті Spring та визначають маршрутизацію запитів.
 */
@Controller//Анотація "@Controller" вказує, що цей клас є компонентом-контролером, який обробляє HTTP-запити.
//Усі методи контролера повертають рядок, який вказує на ім'я представлення (шаблону),
// який буде відображатись після обробки запиту.
@RequestMapping("/people")//Анотація "@RequestMapping("/people")" визначає базовий шлях для всіх запитів,
// оброблюваних цим контролером. Таким чином, всі запити, що починаються з "/people",
// будуть спрямовуватися до методів цього контролера.
public class PeopleController {

    private final PeopleService peopleService;
    private final BookService bookService;

    //Залежність до сервісу "PeopleService" та "BookService" встановлюється
    // за допомогою анотації "@Autowired". Це забезпечує ін'єкцію залежностей,
    // тобто Spring автоматично створить і надасть екземпляри сервісів класів
    // "PeopleService" та "BookService" для використання в контролері.
    @Autowired
    public PeopleController(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }

    //Метод "index(Model model)" обробляє GET-запит на шлях "/people".
    // Він отримує всі об'єкти "Person" з сервісу "PeopleService" і
    // передає їх у модель для подальшого відображення у представленні "people/index".
    @GetMapping
    public String index(Model model) {
        //Получим всех людей из Repository и передадим на отображеник в представлениe
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    //Метод "show(@PathVariable("id") int id, Model model)" обробляє GET-запит на шлях "/people/{id}",
    // де "{id}" - це ідентифікатор об'єкта "Person".
    // Він отримує об'єкт "Person" з сервісу "PeopleService" за заданим ідентифікатором і
    // передає його у модель, а також отримує список книг, пов'язаних з цим об'єктом "Person".
    // Ці дані використовуються для відображення інформації про особу та списку книг у представленні "people/show".
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        List<Book> books = bookService.findBookByOwner(peopleService.findOne(id));
        model.addAttribute("books", books);
        List<Boolean> isTakeBookList = new ArrayList<>();
        for (Book book : books) {
            isTakeBookList.add(bookService.reBook(book));
            System.out.println(isTakeBookList);
        }
        model.addAttribute("isTakeBookList", isTakeBookList);
        return "people/show";
    }

    //Метод "newPerson(@ModelAttribute("person") Person person)" обробляє GET-запит на шлях "/people/new".
    // Він ініціалізує об'єкт "Person" і
    // передає його у модель для використання при створенні нової особи у представленні "people/new".
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    //Метод "create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult)"
    // обробляє POST-запит на шлях "/people". Він отримує об'єкт "Person" з форми,
    // який передається за допомогою анотації "@ModelAttribute",
    // і проводить валідацію цього об'єкта за допомогою анотації "@Valid".
    // Якщо валідація не пройшла успішно, помилки зберігаються в об'єкті "BindingResult".
    // Якщо валідація успішна, об'єкт "Person" зберігається у базі даних за допомогою сервісу "PeopleService",
    // і користувач перенаправляється на шлях "/people".
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {//с помощью @ModelAttribute создаем нового
        // человека(пустой объект класса Person)
        if(bindingResult.hasErrors())
            return "people/new";

        peopleService.save(person);//затем эта же аннотация внедряет значение из формы
        // в этот объект класса Person. Затем она этот объект добавляет в модель.
        return "redirect:/people";
    }

    //Метод "edit(Model model, @PathVariable("id") int id)" обробляє GET-запит на шлях "/people/{id}/edit".
    // Він отримує об'єкт "Person" з сервісу "PeopleService" за заданим ідентифікатором і
    // передає його у модель для використання при редагуванні особи у представленні "people/edit".
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOne(id));
        return "people/edit";
    }

    //Метод "update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
    // @PathVariable("id") int id)" обробляє PATCH-запит на шлях "/people/{id}".
    // Він отримує об'єкт "Person" з форми, проводить валідацію та оновлює існуючий об'єкт "Person"
    // з новими даними у базі даних за допомогою сервісу "PeopleService".
    @PatchMapping ("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult, @PathVariable("id") int id) {
        if(bindingResult.hasErrors())
            return "people/edit";
        peopleService.update(id, person);
        return "redirect:/people";
    }

    //Метод "delete(@PathVariable("id") int id)" обробляє DELETE-запит на шлях "/people/{id}".
    // Він видаляє об'єкт "Person" з бази даних за заданим ідентифікатором за допомогою сервісу "PeopleService".
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }
}
