package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import uz.viento.crm_system.security.JwtProvider;

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
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;


    public ResponseApiWithObject registerAdmin(RegisterDto userDto) throws IOException {
        try {
            boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(userDto.getPhoneNumber());
            if (existsByPhoneNumber) return new ResponseApiWithObject("User already have", false, null);
            if (!userDto.getPassword().equals(userDto.getPrePassword()))
                return new ResponseApiWithObject("Passwords don't match", false, null);
            Optional<Roles> roleName = roleRepository.findByRoleName(RoleName.ROLE_USER);
            if (!roleName.isPresent()) return new ResponseApiWithObject("Role Not found", false, null);
            Roles roles = roleName.get();

            User user = new User();
            user.setFullName(userDto.getFullName());
            user.setRoles(Collections.singletonList(roles));
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setPhoneNumber(userDto.getPhoneNumber());
            userRepository.save(user);
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDto.getPhoneNumber(), userDto.getPassword()));
            User authenticatedUser = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(authenticatedUser.getPhoneNumber());
            return new ResponseApiWithObject("Successfully registered", true, token);
        } catch (Exception e) {
            return new ResponseApiWithObject("Error", false, null);
        }
    }


    public ResponseApi editUserInfo(AddingUserDto addingUserDto, @CurrentUser User user, UUID id) {
        List<Roles> roles = user.getRoles();
        boolean anyMatch = roles.stream().anyMatch(roles1 -> roles1.equals(RoleName.ROLE_ADMIN.name()));
        if (anyMatch) {
            Optional<User> userRepositoryById = userRepository.findById(id);
            if (!userRepositoryById.isPresent()) return new ResponseApi("User not found", false);

            boolean existsByPhoneNumberAndIdNot = userRepository.existsByPhoneNumberAndIdNot(addingUserDto.getPhoneNumber(), id);
            if (existsByPhoneNumberAndIdNot) return new ResponseApi("Phone Number is already exists", false);
            User user1 = userRepositoryById.get();
            user1.setPhoneNumber(addingUserDto.getPhoneNumber());
            user1.setFullName(addingUserDto.getFullName());
            userRepository.save(user1);
            return new ResponseApi("Successfully edited", true);
        }
        return new ResponseApi("You do not have permission", false);


    }


    public boolean checkPassword(String oldPassword, String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }


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

    public ResponseApi addAdditionalPhone(UUID userId, ReqAddAdditionalPhone reqAddAdditionalPhone) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) return new ResponseApi("User not found", false);
        boolean existsByAdditionalPhoneNumber = userRepository.existsByAdditionalPhoneNumber(reqAddAdditionalPhone.getNewPhoneNumber());

        if (existsByAdditionalPhoneNumber) return new ResponseApi("This phone Number is already exists", false);

        User user = optionalUser.get();
        user.setAdditionalPhoneNumber(reqAddAdditionalPhone.getNewPhoneNumber());
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


    public Page<ResUser> getAllUser(int page) {

        List<ResUser> userList = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page, 10);
        List<User> allByPassword = userRepository.findAllByPassword(null);
        for (User user : allByPassword) {
            ResUser resUser = new ResUser(
                    user.getFullName(),
                    user.getPhoneNumber(),
                    user.getAddress()
            );
            userList.add(resUser);
        }
        Page<ResUser> resUsers = new PageImpl<>(userList, pageRequest, userList.size());
        return resUsers;
    }

    public ResUser getById(UUID id) {
        Optional<User> repository = userRepository.findById(id);
        if (!repository.isPresent()) return null;
        User user = repository.get();
        ResUser resUser = new ResUser(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddress()
        );
        return resUser;
    }

    public ResUser getByPhoneNumber(String phoneNumber) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (!optionalUser.isPresent()) return null;
        User user = optionalUser.get();
        ResUser resUser = new ResUser(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddress()
        );
        return resUser;
    }

    public ResponseApi forgotPassword(ReqForgetPassword reqForgetPassword) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(reqForgetPassword.getPhoneNumber());
        if (!optionalUser.isPresent()) return new ResponseApi("User not found", false);

        if (!reqForgetPassword.getNewPassword().equals(reqForgetPassword.getConfirmPassword()))
            return new ResponseApi("Passwords don't match", false);

        User user = optionalUser.get();
        user.setPassword(reqForgetPassword.getNewPassword());
        userRepository.save(user);
        return new ResponseApi("Password successfully changed", true);
    }

    public Page<ResUser> getAllAdmin(int page) {

        List<ResUser> userList = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page, 10);
        Optional<Roles> optionalRoles = roleRepository.findByRoleName(RoleName.ROLE_ADMIN);


        Optional<List<User>> optionalUsers = userRepository.findAllByRoles(optionalRoles.get());
        if (!optionalUsers.isPresent())
            return null;
        List<User> userList1 = optionalUsers.get();

        for (User user : userList1) {
            ResUser resUser = new ResUser(
                    user.getFullName(),
                    user.getPhoneNumber(),
                    user.getAddress()
            );
            userList.add(resUser);
        }
        Page<ResUser> resUsers = new PageImpl<>(userList, pageRequest, userList.size());
        return resUsers;
    }


}
