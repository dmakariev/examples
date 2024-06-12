//DEPS org.joinfaces:faces-spring-boot-starter:5.3.0

//JAVA_OPTIONS -Djoinfaces.faces-servlet.enabled=true
//JAVA_OPTIONS -Djoinfaces.faces.automatic-extensionless-mapping=true
//FILES META-INF/resources/person.xhtml=person.xhtml

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
