package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.payload.LoginDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.repository.UserRepository;
import uz.viento.crm_system.security.JwtProvider;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Optional<User> byUsername = userRepository.findByPhoneNumber(phoneNumber);
        if (byUsername.isPresent())
            return byUsername.get();

        throw new UsernameNotFoundException("User topilmadi");
    }

    public ResponseApi login(LoginDto loginDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getPhoneNumber(), loginDto.getPassword()));
            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(user.getPhoneNumber());
            return new ResponseApi(token, true);
        } catch (BadCredentialsException e) {
            return new ResponseApi("Parol yoki telefon nomer xato", false);
        }
    }
}
