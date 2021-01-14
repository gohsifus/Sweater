package org.example.service;

import antlr.StringUtils;
import org.example.model.Role;
import org.example.model.User;
import org.example.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUserName(username);

        if(user == null){

            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    /**
     * Добавление пользователя в базу
     * @param user пользователь
     * @return true or false если false то пользователь существует в базе
     * */
    public boolean addUser(User user){

        User userFromDb = userRepo.findByUserName(user.getUserName());

        if(userFromDb != null){
            return false;
        }

        user.setActive(true);
        if(user.getRoles() == null){
            user.setRoles(Collections.singleton(Role.USER));
        }
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

        if(!user.getEmail().isEmpty()){
            String message = String.format(
                    "Привет, %s! \n" +
                    "Для подтверждения регистрации перейдите по ссылке: http://localhost:8080/activate/%s",
                    user.getUserName(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Код активизации", message);
        }

        return true;
    }

    /**
     * Активация пользователя
     * @param code код активации
     * @return true or false если false то пользователю не отправляли код активации
     * */
    public boolean activateUser(String code){
        User user = userRepo.findByActivationCode(code);

        if(user == null){
            return false;
        }

        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

    public Iterable<User> findAll(){
        return userRepo.findAll();
    }

    /**
     * Изменяет пользователя в базе
     * @param user измененный пользователь
     * @return true or false если false то пользователь новый
     * */
    public boolean editUser(User user){

        Optional<User> oldUser = userRepo.findById(user.getId());

        if( oldUser.get() != null){

            userRepo.deleteById(user.getId());

            if(user.getEmail() == null){
                userRepo.save(user);
                return true;
            }

            if(oldUser.get().getEmail() == null | !oldUser.get().getEmail().equals(user.getEmail())){ //Если email изменился
                addUser(user);
                return true;
            }

            userRepo.save(user);

            return true;
        } else {
            return false;
        }
    }
}
