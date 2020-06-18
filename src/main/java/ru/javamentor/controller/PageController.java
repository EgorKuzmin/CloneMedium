package ru.javamentor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.javamentor.model.User;
import ru.javamentor.service.TopicService;
import ru.javamentor.service.UserService;

import java.security.Principal;

@Controller
public class PageController {

    public final TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    public PageController(TopicService topicService) {
        this.topicService = topicService;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(@ModelAttribute("message") String message, @ModelAttribute("warning") String warning, Model model) {
        boolean flagMessage = false;
        boolean flagWarning = false;
        if (message != null && !message.equals("")) {
            flagMessage = true;
        }
        if (warning != null && !warning.equals("")) {
            flagWarning = true;
        }
        model.addAttribute("flagMes", flagMessage);
        model.addAttribute("flagWar", flagWarning);
        return "login";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String homePage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId());
        return "home";
    }

    @RequestMapping(value = "/allTopics", method = RequestMethod.GET)
    public String allTopicsPage() {
        return "all_topics_page";
    }

    @GetMapping("/")
    public String indexPage() {
        return "root";
    }
  
    @GetMapping("/topic/{id}")
    public String topicPage(@PathVariable Long id, Model model) {
        model.addAttribute("topicId", id);
        return "topic";
    }

    @GetMapping("/admin/allUsers")
    public String adminAllUsersPage() {
        return "admin-all_users";
    }

    @GetMapping("/admin/moderate")
    public String adminModeratePage() {
        return "admin-moderate";
    }
}

