package de.uol.pgdoener.th1.user;

import de.uol.pgdoener.th1.api.UserApi;
import de.uol.pgdoener.th1.api.UserApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService implements UserApiDelegate {

    private final UserDetailsManager userDetailsManager;

    /**
     * POST /user/create : Create a new user
     * Create a new user with the given email, name and password.
     *
     * @param email    (required)
     * @param name     (required)
     * @param password (required)
     * @return User created. (status code 200)
     * or User already exists. (status code 400)
     * @see UserApi#createUser
     */
    @Override
    public ResponseEntity<Void> createUser(String email, String name, String password) {
        if (userDetailsManager.userExists(name)) {
            log.info("User already exists: {}", name);
            return ResponseEntity.badRequest().build();
        }

        UserDetails user = new ServerUser(name, password, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userDetailsManager.createUser(user);
        log.info("User created: {}", name);
        return ResponseEntity.ok().build();
    }

}
