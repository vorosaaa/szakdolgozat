package org.ati.core.service;

import org.ati.core.model.ConfirmationToken;
import org.ati.core.model.UserDTO;
import org.ati.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void deleteUser(UserDTO userDTO) {
        userRepository.delete(userDTO);
    }

    public List<UserDTO> listAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO save(UserDTO user) {
        if (user.getId() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public UserDTO getOne(Long id) {
        return userRepository.getOne(id);
    }

    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
