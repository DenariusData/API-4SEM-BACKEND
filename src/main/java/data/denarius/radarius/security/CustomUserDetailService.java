package data.denarius.radarius.security;

import data.denarius.radarius.entity.Person;
import data.denarius.radarius.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final PersonService personService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person person = personService.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal
                .builder()
                .userId(person.getId())
                .email(person.getEmail())
                .password(person.getPassword())
                .role(person.getRole())
                .build();
    }
}
