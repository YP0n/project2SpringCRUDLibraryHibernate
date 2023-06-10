package ua.ypon.project2SpringLibHib.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * net.ukr@caravell 01/05/2023
 */
@Entity//Анотація "@Entity" вказує, що цей клас є сутністю, яка буде зберігатись у базі даних.
@Table(name = "Person")//Анотація "@Table(name = "Person")" вказує назву таблиці,
// в якій будуть зберігатись дані про особу.
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Анотація "@GeneratedValue(strategy = GenerationType.IDENTITY)"
    // вказує, що значення ідентифікатора буде автоматично генеруватись.
    private int id;

    @NotEmpty(message = "Ім'я не повинно бути пустим")
    @Size(min = 2, max = 100, message = "Ім'я повинне бути від 2 до 100 символів")
    @Column(name = "name")
    private String name;

    @Min(value = 1900, message = "Рік народження повинен бути більшим за 1900")
    @Column(name = "yearofbirth")
    private int yearOfBirth;

    @OneToMany(mappedBy = "owner")//Анотація "@OneToMany(mappedBy = "owner")" вказує,
    // що існує зв'язок один-до-багатьох між особою і книгою. Поле "books" представляє список книг,
    // які належать цій особі.
    private List<Book> books;

    //конструктор за замовчування-потрібен для Spring(наприклад для @ModelAttribute)-створення сутності
    public Person() {
    }

    public Person(String name, int yearOfBirth) {
        this.name = name;
        this.yearOfBirth = yearOfBirth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
