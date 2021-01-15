package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(Model model){


        model.addAttribute("user", new User());
        return "registration";
    }


    @PostMapping("/registration")
    public @ResponseBody
    ModelAndView addUser(Model model, @Valid User user, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            model.addAttribute("errors", ControllerUtils.getErrors(bindingResult));
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("registration");
            return modelAndView;
        }

        if(user.getPassword().equals(user.getPassword2())) {
            if (!userService.addUser(user)) {

                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("registration");
                return modelAndView;
            }
        } else {
            model.addAttribute("confirmError", "Пароли не совпадают");
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("registration");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");

        return modelAndView;
    }

    /**
     * Подтверждение кода активации пользователем
     * @param code код активации
     * */
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("message", "User successfully activated!");
        } else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }
}
