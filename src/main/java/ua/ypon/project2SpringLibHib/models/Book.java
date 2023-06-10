package ua.ypon.project2SpringLibHib.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * net.ukr@caravell 01/05/2023
 */

@Entity
@Table(name = "Book")
public class Book {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Назва книги не повинна бути порожньою")
    @Size(min = 2, max = 100, message = "Назва книги повинна бути в діапазоні від 2 до 100 символів")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Заповніть поле автор")
    @Size(min = 2, max = 100, message = "Ім'я автора повинно бути в діапазоні від 2 до 100 символів")
    @Column(name = "author")
    private String author;

    @Min(value = 1500, message = "Рік повинен бути більшим за 1500")
    @Column(name = "year")
    private int year;

    // Анотація @ManyToOne встановлює зв'язок багато-до-одного з сутністю Person
    // Анотація @JoinColumn вказує, які стовпці використовуватимуться для зв'язку з сутністю Person
    @ManyToOne()
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    private Person owner;

    //значення яке зберігає точний час в секундах до або після опівночі 1 січня 2000року
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    // Анотація @Transient вказує, що поле takeBook не буде зберігатися в базі даних
    @Transient
    private boolean takeBook;

    public Book() {
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Book(String title, String author, int year, Person owner) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public boolean isTakeBook() {
        return takeBook;
    }

    public void setTakeBook(boolean takeBook) {
        this.takeBook = takeBook;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", owner=" + owner +
                ", createAt=" + createAt +
                '}';
    }
}
