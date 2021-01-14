package org.example.repos;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByUserName(String username);

    User findByActivationCode(String code);

}
