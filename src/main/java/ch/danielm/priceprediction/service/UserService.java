package ch.danielm.priceprediction.service;

import ch.danielm.priceprediction.model.User;
import ch.danielm.priceprediction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUser(final String name) {
        return userRepository.findByName(name);
    }

    public User getUser(final String name) throws InvalidKeyException {
        return findUser(name).orElseThrow(InvalidKeyException::new);
    }

    public boolean exists(String name) {
        return findUser(name).isPresent();
    }

    public User persist(User user) {
        return userRepository.save(user);
    }
}
