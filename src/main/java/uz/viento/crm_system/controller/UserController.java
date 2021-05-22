package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.service.CurrentUser;
import uz.viento.crm_system.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;


    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editUserInfo(@PathVariable UUID id, @RequestBody AddingUserDto addingUserDto, @CurrentUser User user) {
        ResponseApi responseApi = userService.editUserInfo(addingUserDto, user, id);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @PatchMapping("/change-own-password")
    public ResponseEntity<?> changePassword(@RequestBody ReqChangePassword reqChangePassword, @CurrentUser User user) {
        ResponseApi responseApi = userService.changeUserPassword(reqChangePassword, user);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @PatchMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ReqForgetPassword reqForgetPassword) {
        ResponseApi responseApi = userService.forgotPassword(reqForgetPassword);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);


    }

    @PostMapping("/change-own-phoneNumber")
    public ResponseEntity<?> changePhoneNumber(@RequestBody ReqChangePassword reqChangePassword, @CurrentUser User user) {
        ResponseApi responseApi = userService.changeUserOwnPhoneNumber(user, reqChangePassword);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PostMapping("/addAdditionalPhone/{userId}")
    public ResponseEntity<?> addAdditionalPhone(@RequestBody ReqAddAdditionalPhone reqAddAdditionalPhone, @PathVariable UUID userId) {
        ResponseApi responseApi = userService.addAdditionalPhone(userId, reqAddAdditionalPhone);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }


    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUser(@RequestParam Integer page) {
        Page<ResUser> allUser = userService.getAllUser(page);
        return ResponseEntity.ok(allUser);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        ResUser byId = userService.getById(id);
        return ResponseEntity.ok(byId);
    }

    @PreAuthorize("hasRole(ROLE_ADMIN)")
    @GetMapping("/getByPhoneNumber")
    public ResponseEntity<?> getUserById(@RequestParam String phoneNumber) {
        ResUser byId = userService.getByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(byId);
    }

    @PreAuthorize("hasRole(ROLE_ADMIN)")
    @PostMapping("/changeUserPhoneNumber")
    public ResponseEntity<?> changeUsersPhoneNumber(@RequestParam String phoneNumber, @RequestBody ReqChangeNumber reqChangeNumber) {
        ResponseApi responseApi = userService.changeUserPhoneNumber(phoneNumber, reqChangeNumber);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }
}
