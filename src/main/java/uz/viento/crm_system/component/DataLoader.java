package uz.viento.crm_system.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.viento.crm_system.entity.Measurement;
import uz.viento.crm_system.entity.Roles;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.enums.RoleName;
import uz.viento.crm_system.repository.MeasurementRepository;
import uz.viento.crm_system.repository.RoleRepository;
import uz.viento.crm_system.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;


    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MeasurementRepository measurementRepository;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            Roles admin = new Roles();
            admin.setRoleName(RoleName.ROLE_ADMIN);
            Roles user = new Roles();
            user.setRoleName(RoleName.ROLE_USER);
            Roles save = roleRepository.save(admin);
            Roles save1 = roleRepository.save(user);
            List<Roles> roles = new ArrayList<>();
            roles.add(save);
            roles.add(save1);


            User user1 = new User();
            user1.setPassword(passwordEncoder.encode("12345"));
            user1.setRoles(roles);
            user1.setPhoneNumber("+998885856005");
            user1.setFullName("Sukhrob Ibrokhimov");
            userRepository.save(user1);

            Measurement measurement = new Measurement();
            measurement.setMeasureName("KG");
            Measurement measurement1 = new Measurement();
            measurement1.setMeasureName("PIECE");
            measurementRepository.save(measurement1);
            measurementRepository.save(measurement);


        }

    }
}
