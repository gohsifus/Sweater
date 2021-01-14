package org.example.controller;

import org.example.model.User;
import org.example.repos.UserRepo;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model){

        model.addAttribute("users", userService.findAll());

        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){

        model.addAttribute("user", user);

        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public @ResponseBody
    ModelAndView userSave(User user, Model model){

        userService.editUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/user");

        return modelAndView;
    }

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){

        model.addAttribute("username", user.getUserName());
        model.addAttribute("email", user.getEmail());

        model.addAttribute("newUser", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
    ){
        user.setPassword(password);
        user.setEmail(email);

        userService.editUser(user);

        return "redirect:/user/profile";
    }
}
