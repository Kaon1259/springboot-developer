package com.example.springboot_developer.service;

import com.example.springboot_developer.dto.UserDto;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    final private UserRepository userRepository;                //Repository 빈
    final private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User save(UserDto userDto) {
        try {
            User user = userRepository.findByEmail(userDto.getEmail()).orElse(null);
            if (user != null) { throw new Exception("Already exists"); }

            log.info("Saving user {}", userDto);

            return userRepository.save(User.builder()
                    .email(userDto.getEmail())
                    .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                    .nickname(userDto.getNickname())
                    .registrationId(userDto.getRegistrationid())
                    .build()
            );
        }catch (Exception e){
            log.error("Exception : " + e.getMessage());
            return null;
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByEmailAndRegistrationId(String email, String registrationId) {
        return userRepository.findByEmailAndRegistrationId(email, registrationId).orElse(null);
    }

    public int updateNickname(Long userId, String nickname) {
        User user = findById(userId);
        if((user !=null )) {
            if(user.getNickname().equals(nickname)) { return 0;}
            else { return userRepository.updateNickname(userId, nickname); }
        }
        return 0;
    }
}
