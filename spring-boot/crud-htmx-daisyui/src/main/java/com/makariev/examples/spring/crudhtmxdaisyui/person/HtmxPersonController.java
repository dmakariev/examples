package com.makariev.examples.spring.crudhtmxdaisyui.person;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/person-crud-htmx")
@RequiredArgsConstructor
public class HtmxPersonController {

    private final PersonRepository personRepository;

    @GetMapping
    public String getPersonsPage(Model model) {
        //available themes 
        final List<String> themes = Arrays.asList(
                "light",
                "dark",
                "cupcake",
                "bumblebee",
                "emerald",
                "corporate",
                "synthwave",
                "retro",
                "cyberpunk",
                "valentine",
                "halloween",
                "garden",
                "forest",
                "aqua",
                "lofi",
                "pastel",
                "fantasy",
                "wireframe",
                "black",
                "luxury",
                "dracula",
                "cmyk",
                "autumn",
                "business",
                "acid",
                "lemonade",
                "night",
                "coffee",
                "winter",
                "dim",
                "nord",
                "sunset"
        );
        // Initialize variables and load initial data
        Pageable pageable = PageRequest.of(0, 5);
        Page<Person> personPage = personRepository.findAll(pageable);

        //model.addAttribute("themes", themes);
        model.addAttribute("persons", personPage.getContent());
        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("size", 5);

        return "person-crud-htmx";
    }

//    @GetMapping("/htmx/list")
//    public String findList(@RequestParam(name = "page", defaultValue = "0") int page,
//                          @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Person> personPage = personRepository.findAll(pageable);
//
//        model.addAttribute("persons", personPage.getContent());
//        model.addAttribute("totalPages", personPage.getTotalPages());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("size", size);
//
//        return "person-crud-htmx :: personRows";
//    }
    @GetMapping("/htmx/main")
    public String findAll(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("persons", personPage.getContent());
        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "person-crud-htmx :: main";
    }

//    @GetMapping("/htmx/pagination")
//    public String getPagination(@RequestParam(name = "page", defaultValue = "0") int page,
//                                @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Person> personPage = personRepository.findAll(pageable);
//
//        model.addAttribute("totalPages", personPage.getTotalPages());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("size", size);
//
//        return "person-crud-htmx :: pagination";
//    }
    @GetMapping("/htmx/form")
    public String showPersonForm(@RequestParam(name = "id", required = false) Long id, @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Person person = id != null
                ? personRepository.findById(id).orElse(new Person())
                : new Person();

        model.addAttribute("person", person);
        model.addAttribute("editMode", id != null);
        model.addAttribute("currentPage", page);

        return "person-crud-htmx :: personForm";
    }

    @PostMapping("/htmx/create")
    public String createPerson(@ModelAttribute Person person,
            @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        personRepository.save(person);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }

    @PostMapping("/htmx/update")
    public String updatePerson(@ModelAttribute Person person,
            @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Person existingPerson = personRepository.findById(person.getId())
                .orElseThrow();
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setBirthYear(person.getBirthYear());
        personRepository.save(existingPerson);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }

    @DeleteMapping("/htmx/{id}")
    public String deletePerson(@PathVariable("id") Long id, @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        personRepository.deleteById(id);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }
}
