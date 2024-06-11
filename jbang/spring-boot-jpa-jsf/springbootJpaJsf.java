//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 22
//DEPS org.springframework.boot:spring-boot-dependencies:3.3.0@pom
//DEPS org.springframework.boot:spring-boot-starter-web
//DEPS org.springframework.boot:spring-boot-starter-data-jpa
//DEPS org.springframework.boot:spring-boot-starter-actuator
//DEPS com.h2database:h2
//DEPS org.postgresql:postgresql
//DEPS org.projectlombok:lombok
//DEPS org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
//DEPS org.slf4j:slf4j-simple

//DEPS org.joinfaces:faces-spring-boot-starter:5.3.0
//DEPS org.mvnpm:simpledotcss:2.3.1
//DEPS org.webjars.npm:picocss__pico:2.0.6

//DEPS org.springframework.boot:spring-boot-starter-thymeleaf

//JAVA_OPTIONS -Dserver.port=8080
//JAVA_OPTIONS -Dspring.datasource.url=jdbc:h2:mem:person-db;MODE=PostgreSQL;
//JAVA_OPTIONS -Dspring.h2.console.enabled=true -Dspring.h2.console.settings.web-allow-others=true
//JAVA_OPTIONS -Dmanagement.endpoints.web.exposure.include=health,env,loggers
//JAVA_OPTIONS -Djoinfaces.faces-servlet.enabled=true
//JAVA_OPTIONS -Djoinfaces.faces.automatic-extensionless-mapping=true
//FILES META-INF/resources/index.html=index-fetch.html
//FILES META-INF/resources/hello.xhtml=hello.xhtml
//FILES META-INF/resources/person.xhtml=person.xhtml

//FILES templates/persons.html=persons.html
//FILES templates/fragments/personRows.html=personRows.html
//FILES templates/fragments/personForm.html=personForm.html
//FILES templates/fragments/pagination.html=pagination.html

//REPOS mavencentral,sb_snapshot=https://repo.spring.io/snapshot,sb_milestone=https://repo.spring.io/milestone
package com.makariev.examples.jbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.application.ViewHandler;
import java.io.Serializable;


@SpringBootApplication
@ComponentScan(basePackages = "com.makariev.examples.jbang")
public class springbootJpaJsf {

    public static void main(String[] args) {
        SpringApplication.run(springbootJpaJsf.class, args);
    }
    
@Component("helloBean")
public static class HelloBean {

    private String name;
    private String message;

    public String getHello() {
        return "Hello world, from JSF!";
    }

    public void createMessage() {
        message = "Hello, " + name + "!";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

}

//@org.springframework.context.annotation.Scope("view")
//@ViewScoped
//@RequestScope
@Component("personBean")
@SessionScope
@Getter
@Setter
public class PersonBean implements Serializable {
    private final PersonRepository personRepository;
    private List<Person> persons;
    private Person formData;
    private boolean editMode;
    private Long editedPersonId;
    private int pageSize = 5;
    private int currentPage = 1;
    private long totalPages;

    public PersonBean(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostConstruct
    public void init() {
        formData = new Person();
        loadPersons();
    }

    public void loadPersons() {
        var page = personRepository.findAll(PageRequest.of(currentPage - 1, pageSize));
        persons = page.getContent();
        totalPages = page.getTotalPages();
    }

    public void create() {
        formData = new Person();
        editMode = false;
        executeScript("showPersonDialog()");
    }

    public void save() {
        System.out.println("Entering save method");

        FacesContext facesContext = FacesContext.getCurrentInstance();
        List<FacesMessage> messages = new ArrayList<>();

        if (facesContext == null) {
            System.out.println("FacesContext is null");
        } else {
            System.out.println("FacesContext is available");
        }

        if (formData.getFirstName() == null || formData.getFirstName().isEmpty()) {
            System.out.println("First name is null or empty");
            messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "First Name is required."));
        }

        if (formData.getLastName() == null || formData.getLastName().isEmpty()) {
            System.out.println("Last name is null or empty");
            messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Last Name is required."));
        }

        if (formData.getBirthYear() == null || formData.getBirthYear() <= 0) {
            System.out.println("Birth year is null or less than or equal to 0");
            messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Birth Year must be a positive number."));
        }

        if (!messages.isEmpty()) {
            for (FacesMessage message : messages) {
                facesContext.addMessage(null, message);
            }
            return;
        }

        if (editMode) {
            System.out.println("Edit mode: updating existing person");
            Person person = personRepository.findById(editedPersonId).orElseThrow();
            person.setFirstName(formData.getFirstName());
            person.setLastName(formData.getLastName());
            person.setBirthYear(formData.getBirthYear());
            personRepository.save(person);
        } else {
            System.out.println("Create mode: saving new person");
            personRepository.save(formData);
        }

        loadPersons();
        formData = new Person();
        System.out.println("Exiting save method");
        executeScript("closeDialog()");
    }

    public void edit(Person person) {
        System.out.println("Editing person: " + person);
        this.formData = person;
        this.editMode = true;
        this.editedPersonId = person.getId();
        recreateView();
        executeScript("showPersonDialog()");
    }

    public void delete(Person person) {
        personRepository.delete(person);
        loadPersons();
    }

    public void changePage(int page) {
        currentPage = page;
        loadPersons();
    }

    public List<Integer> getPageNumbers() {
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    private void executeScript(String script) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getPartialViewContext().getEvalScripts().add(script);
    }

    public void recreateView() {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        ViewHandler handler = context.getApplication().getViewHandler();
        UIViewRoot root = handler.createView(context, viewId);
        root.setViewId(viewId);
        context.setViewRoot(root);
    }
}


@Data
@Entity
@Table(name = "person")
@NoArgsConstructor
@AllArgsConstructor
public static class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Integer birthYear;
}



@Component
@RequiredArgsConstructor
public static class InitialRecords {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void exercise() {

        if (personRepository.count() > 0) {
            return;
        }
        List.of(
                new Person(1L, "Ada", "Lovelace", 1815),
                new Person(2L, "Niklaus", "Wirth", 1934),
                new Person(3L, "Donald", "Knuth", 1938),
                new Person(4L, "Edsger", "Dijkstra", 1930),
                new Person(5L, "Grace", "Hopper", 1906),
                new Person(6L, "John", "Backus", 1924)
        ).forEach(personRepository::save);
    }
}

@Slf4j
@RestController
public static class HiController {

    @GetMapping("/hi")
    public String sayHi(@RequestParam(required = false, defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public static class PersonController {

    private final PersonRepository personRepository;

    @GetMapping
    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @GetMapping("{id}")
    public Optional<Person> findById(@PathVariable("id") Long id) {
        return personRepository.findById(id);
    }

    @PostMapping
    public Person create(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @PutMapping("{id}")
    public Person updateById(@PathVariable("id") Long id, @RequestBody Person person) {
        var loaded = personRepository.findById(id).orElseThrow();
        loaded.setFirstName(person.getFirstName());
        loaded.setLastName(person.getLastName());
        loaded.setBirthYear(person.getBirthYear());
        return personRepository.save(loaded);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        personRepository.deleteById(id);
    }
}



}

interface PersonRepository extends JpaRepository<com.makariev.examples.jbang.springbootJpaJsf.Person, Long> {
}

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
class PersonPageController {

    private final PersonRepository personRepository;

    @GetMapping
    public String getPersonsPage() {
        return "persons";
    }
}

////////

@Controller
@RequestMapping("/htmx/persons")
@RequiredArgsConstructor
class PersonApiController {

    private final PersonRepository personRepository;

    @GetMapping("/list")
    public String findAll(@RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.makariev.examples.jbang.springbootJpaJsf.Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("persons", personPage.getContent());
        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "fragments/personRows :: personRows";
    }

    @GetMapping("/pagination")
    public String getPagination(@RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.makariev.examples.jbang.springbootJpaJsf.Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "fragments/pagination :: pagination";
    }

    @GetMapping("/form")
    public String showPersonForm(@RequestParam(name = "id", required = false) Long id, Model model) {
        com.makariev.examples.jbang.springbootJpaJsf.Person person = id != null
                ? personRepository.findById(id).orElse(new com.makariev.examples.jbang.springbootJpaJsf.Person())
                : new com.makariev.examples.jbang.springbootJpaJsf.Person();

        model.addAttribute("person", person);
        model.addAttribute("editMode", id != null);

        return "fragments/personForm :: personForm";
    }

    @PostMapping("/create")
    public String createPerson(@ModelAttribute com.makariev.examples.jbang.springbootJpaJsf.Person person,
                               @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        personRepository.save(person);
        return findAll(page, 5, model);
    }

    @PostMapping("/update")
    public String updatePerson(@ModelAttribute com.makariev.examples.jbang.springbootJpaJsf.Person person,
                               @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        com.makariev.examples.jbang.springbootJpaJsf.Person existingPerson = personRepository.findById(person.getId())
                .orElseThrow();
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setBirthYear(person.getBirthYear());
        personRepository.save(existingPerson);
        return findAll(page, 5, model);
    }
}





