package ua.ypon.project2SpringLibHib.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * net.ukr@caravell 01/05/2023
 */
/*
Цей код налаштовує основні компоненти Spring для вашого веб-додатку,
 такі як шаблонизатор Thymeleaf, база даних,
 Hibernate та транзакційний менеджер.
 Він використовує анотаційний підхід для конфігурації та автоматичного керування компонентами.
 */
@Configuration//Анотація @Configuration позначає клас SpringConfig як конфігураційний клас для Spring.
@ComponentScan("ua.ypon.project2SpringLibHib")//Анотація @ComponentScan("ua.ypon.project2SpringLibHib") вказує Spring,
// що потрібно сканувати пакет ua.ypon.project2SpringLibHib для пошуку компонентів,
// які потрібно автоматично налаштувати та керувати.
@PropertySource("classpath:hibernate.properties")//Анотація @PropertySource("classpath:hibernate.properties") вказує,
// що файл властивостей hibernate.properties містить налаштування для Hibernate.
@EnableTransactionManagement//Анотація @EnableTransactionManagement увімкнює керування транзакціями для методів,
// помічених анотаціями @Transactional.
@EnableJpaRepositories("ua.ypon.project2SpringLibHib.repositories")//Анотація @EnableJpaRepositories("ua.ypon.project2SpringLibHib.repositories")
// вказує Spring, що потрібно включити репозиторії JPA з пакету ua.ypon.project2SpringLibHib.repositories.
@EnableWebMvc//Анотація @EnableWebMvc увімкнює підтримку веб-рівня MVC у Spring.
public class SpringConfig implements WebMvcConfigurer {//Клас SpringConfig реалізує інтерфейс WebMvcConfigurer для налаштування конфігурації веб-рівня MVC.

    private final ApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;//окружение для получения доступа к свойствам которые подключаем
    }

    //Метод templateResolver() створює та налаштовує SpringResourceTemplateResolver,
    // який відповідає за розпізнавання та завантаження шаблонів Thymeleaf з папки /WEB-INF/views/.
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    //Метод templateEngine() створює SpringTemplateEngine,
    // який об'єднує SpringResourceTemplateResolver зі Spring та налаштовує його.
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    //Метод configureViewResolvers() налаштовує ThymeleafViewResolver,
    // який відповідає за розпізнавання та відображення шаблонів Thymeleaf.
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");

        registry.viewResolver(resolver);
    }

    //Метод dataSource() створює та налаштовує об'єкт DriverManagerDataSource,
    // який використовується для підключення до бази даних.
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(environment.getRequiredProperty("hibernate.driver_class"));//название драйвера
        dataSource.setUrl(environment.getRequiredProperty("hibernate.connection.url"));//обращение к БД
        dataSource.setUsername(environment.getRequiredProperty("hibernate.connection.user_name"));
        dataSource.setPassword(environment.getRequiredProperty("hibernate.connection.pass_word"));

        return dataSource;
    }

    //Метод hibernateProperties() повертає об'єкт Properties, який містить властивості для налаштування Hibernate,
    // такі як діалект бази даних та відображення SQL-запитів.
private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));

        return properties;
}
//    @Bean
//    public JdbcTemplate jdbcTemplate() {
//        return new JdbcTemplate(dataSource());
//    }

    //Метод entityManagerFactory() створює LocalContainerEntityManagerFactoryBean,
    // який використовується для налаштування та створення менеджера сутностей JPA.
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("ua.ypon.project2SpringLibHib.models");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    //Метод transactionManager() створює PlatformTransactionManager,
    // який використовується для керування транзакціями в JPA.
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }
}
