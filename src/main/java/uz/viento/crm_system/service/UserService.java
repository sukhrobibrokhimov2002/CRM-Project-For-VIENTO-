package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.Roles;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.entity.enums.RoleName;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.AttachmentRepository;
import uz.viento.crm_system.repository.RoleRepository;
import uz.viento.crm_system.repository.UserRepository;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    public ResponseApi registerUser(RegisterDto userDto) throws IOException {
        boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(userDto.getPhoneNumber());
        if (existsByPhoneNumber) return new ResponseApi("User already have", false);
        if (!userDto.getPassword().equals(userDto.getPrePassword()))
            return new ResponseApi("Passwords don't match", false);
        Optional<Roles> roleName = roleRepository.findByRoleName(RoleName.ROLE_USER);
        if (!roleName.isPresent()) return new ResponseApi("Role Not found", false);
        Roles roles = roleName.get();

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setRoles(Collections.singletonList(roles));
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.save(user);
        return new ResponseApi("Successfully registered", true);
    }

    public ResponseApi addUser(AddingUserDto addingUserDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Roles> roles = user.getRoles();
        boolean anyMatch = roles.stream().anyMatch(roles1 -> {
            roles1.getRoleName().equals(RoleName.ROLE_ADMIN.name());
            return true;
        });

        if (anyMatch) {
            boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(addingUserDto.getPhoneNumber());
            if (existsByPhoneNumber) return new ResponseApi("This phone number is already exists", false);
//            Optional<Attachment> optionalAttachment = attachmentRepository.findById(addingUserDto.getAttachmentId());
//            if (!optionalAttachment.isPresent())
//                return new ResponseApi("Attachment not found", false);

            List<Roles> allById = roleRepository.findAllById(addingUserDto.getRoleId());
            User users = new User();

            users.setPassword(passwordEncoder.encode(addingUserDto.getPassword()));
            users.setRoles(allById);
            users.setFullName(addingUserDto.getFullName());
            users.setPhoneNumber(addingUserDto.getPhoneNumber());
            userRepository.save(users);
            return new ResponseApi("Successfully added", true);
        } else {
            return new ResponseApi("You do'nt have permission for that", true);

        }
    }

    public ResponseApi editUserInfo(AddingUserDto addingUserDto, @CurrentUser User user, UUID id) {
        List<Roles> roles = user.getRoles();
        boolean anyMatch = roles.stream().anyMatch(roles1 -> roles1.equals(RoleName.ROLE_ADMIN.name()));
        if (anyMatch) {
            Optional<User> userRepositoryById = userRepository.findById(id);
            if (!userRepositoryById.isPresent()) return new ResponseApi("User not found", false);
            List<Roles> allById = roleRepository.findAllById(addingUserDto.getRoleId());

            boolean existsByPhoneNumberAndIdNot = userRepository.existsByPhoneNumberAndIdNot(addingUserDto.getPhoneNumber(), id);
            if (existsByPhoneNumberAndIdNot) return new ResponseApi("Phone Number is already exists", false);
            User user1 = userRepositoryById.get();
            user1.setRoles(allById);
            user1.setPhoneNumber(addingUserDto.getPhoneNumber());
            user1.setPassword(passwordEncoder.encode(addingUserDto.getPassword()));
            user1.setFullName(addingUserDto.getFullName());
            userRepository.save(user1);
            return new ResponseApi("Successfully edited", true);
        }
        return new ResponseApi("You do not have permission", false);


    }

    public boolean checkPassword(String oldPassword, String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }


//    public boolean checkAdmin(User user) {
//        List<Roles> roles = user.getRoles();
//        return roles.stream().anyMatch(roles1 -> roles1.equals(RoleName.ROLE_ADMIN));
//    }


    public ResponseApi changeUserPassword(ReqChangePassword reqChangePassword, User currentUser) {

        if (currentUser.getPhoneNumber().equals(reqChangePassword.getPhoneNumber())) {
            boolean checkPassword = checkPassword(reqChangePassword.getPassword()
                    , currentUser.getPassword());
            if (!checkPassword) return new ResponseApi("Password is incorrect", false);
            if (!reqChangePassword.getNewPassword().equals(reqChangePassword.getConfirmNewPassword()))
                return new ResponseApi("Password don't match", false);
            currentUser.setPassword(passwordEncoder.encode(reqChangePassword.getNewPassword()));
            userRepository.save(currentUser);
            return new ResponseApi("Successfully changed", true);
        }


        return new ResponseApi("Error ", false);


    }

    public ResponseApi changeUserOwnPhoneNumber(User user, ReqChangePassword reqChangePassword) {

        if (!user.getPhoneNumber().equals(reqChangePassword.getPhoneNumber()))
            return new ResponseApi("Phone Number is incorrect", false);
        boolean checkPassword = checkPassword(reqChangePassword.getPassword(), user.getPassword());
        if (!checkPassword) return new ResponseApi("Password is incorrect", false);
        boolean byPhoneNumberAndIdNot = userRepository.existsByPhoneNumberAndIdNot(reqChangePassword.getNewPhoneNumber(), user.getId());
        if (byPhoneNumberAndIdNot) return new ResponseApi("This phone number is already exists", false);
        user.setPhoneNumber(reqChangePassword.getNewPhoneNumber());
        userRepository.save(user);
        return new ResponseApi("Phone Number successfully changed", true);
    }

    public ResponseApi addAdditionalPhone(UUID userId, String phoneNumber) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) return new ResponseApi("User not found", false);
        boolean existsByAdditionalPhoneNumber = userRepository.existsByAdditionalPhoneNumber(phoneNumber);

        if (existsByAdditionalPhoneNumber) return new ResponseApi("This phone Number is already exists", false);

        User user = optionalUser.get();
        user.setAdditionalPhoneNumber(phoneNumber);
        return new ResponseApi("Successfully added", true);
    }

    public ResponseApi changeUserPhoneNumber(String phoneNumber, ReqChangeNumber reqChangeNumber) {
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (!byPhoneNumber.isPresent()) return new ResponseApi("User not found", false);
        User user = byPhoneNumber.get();


        boolean existsByPhoneNumberAndIdNot = userRepository.existsByPhoneNumberAndIdNot(reqChangeNumber.getNewPhoneNumber(), user.getId());
        if (existsByPhoneNumberAndIdNot) return new ResponseApi("This phone Number is already exists", false);
        user.setPhoneNumber(reqChangeNumber.getNewPhoneNumber());
        userRepository.save(user);
        return new ResponseApi("Successfully changed", true);
    }

    public ResponseApi enableOrDisableUser(UUID id, boolean enable) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) return new ResponseApi("User not found", false);
        User user = optionalUser.get();
        user.setEnabled(enable);
        userRepository.save(user);
        return new ResponseApi("Enable status successfully changed", true);

    }

    public Page<User> getAllUser(int page) {

        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<User> userRepositoryAll = userRepository.findAll(pageRequest);
        return userRepositoryAll;
    }

    public User getById(UUID id) {
        Optional<User> repository = userRepository.findById(id);
        return repository.orElse(null);
    }

    public User getByPhoneNumber(String phoneNumber) {
        Optional<User> phoneNumber1 = userRepository.findByPhoneNumber(phoneNumber);
        return phoneNumber1.orElse(null);
    }
}
