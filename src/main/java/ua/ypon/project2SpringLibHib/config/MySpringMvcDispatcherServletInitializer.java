package ua.ypon.project2SpringLibHib.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * net.ukr@caravell 01/05/2023
 */
/*
У цьому класі можно налаштувати фільтри та зв'язати їх зі шляхами URL для обробки HTTP-запитів у вашому веб-додатку.
 */
//Клас MySpringMvcDispatcherServletInitializer використовується для налаштування та реєстрації сервлета диспетчера Spring MVC у вашому веб-додатку.
public class MySpringMvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    //Метод getRootConfigClasses() повертає конфігураційні класи, які використовуються для налаштування кореневого контексту Spring.
    // У даному випадку, повертається null, оскільки не використовується окремий конфігураційний клас для кореневого контексту.
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    //Метод getServletConfigClasses() повертає конфігураційні класи, які використовуються для налаштування контексту сервлета диспетчера Spring MVC.
    // У даному випадку, повертається клас SpringConfig, який містить налаштування для Spring.
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {SpringConfig.class};
    }

    //Метод getServletMappings() вказує шляхи, на які спрямовуються HTTP-запити до сервлета диспетчера.
    // У даному випадку, всі запити спрямовуються на кореневий шлях ("/").
    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    //Метод onStartup() викликається при запуску додатку і реєструє фільтри для обробки HTTP-запитів.
    @Override
    public void onStartup(ServletContext aServletContext) throws ServletException {
        super.onStartup(aServletContext);
        registerCharacterEncodingFilter(aServletContext);
        registerHiddenFieldFilter(aServletContext);
    }

    //Метод registerHiddenFieldFilter() реєструє фільтр HiddenHttpMethodFilter,
    // який дозволяє використовувати HTTP-методи PUT, PATCH і DELETE у веб-формах.
    private void registerHiddenFieldFilter(ServletContext aContext) {
        aContext.addFilter("hiddenHttpMethodFilter",
                new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
    }

    //Метод registerCharacterEncodingFilter() реєструє фільтр CharacterEncodingFilter,
    // який встановлює кодування символів для обробки тексту у кодуванні UTF-8.
    private void registerCharacterEncodingFilter(ServletContext aContext) {
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic chracterEncoding = aContext.addFilter("characterEncoding", characterEncodingFilter);
        chracterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
    }
}
