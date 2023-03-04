package com.emailattachment.service;

import com.emailattachment.dto.UserDto;
import com.emailattachment.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Repository
public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<UserDto> findAllUsers();

}
