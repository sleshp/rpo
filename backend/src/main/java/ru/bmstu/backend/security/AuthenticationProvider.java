package ru.bmstu.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.bmstu.backend.models.User;
import ru.bmstu.backend.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Value("${private.session-timout-mins}")
    private int sessionTimoutMinutes;
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {}

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Object token = authentication.getCredentials();
        Optional<User> optionalUser = userRepository.findByToken(String.valueOf(token));
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = optionalUser.get();
        boolean timeout = true;
        LocalDateTime now = LocalDateTime.now();
        if (user.getActivity() != null){
            LocalDateTime timeoutTime = user.getActivity().plusMinutes(sessionTimoutMinutes);
            if (now.isBefore(timeoutTime)) {
                timeout = false;
            }
        }
        if (timeout) {
            user.setToken(null);
            userRepository.save(user);
            throw new CredentialsExpiredException("Token has expired");
        } else {
            user.setActivity(now);
            userRepository.save(user);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList("USER")
        );
    }
}
