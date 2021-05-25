package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.PeopleWhoCalled;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.service.PeopleWhoCalledService;

import java.util.List;

@RestController
@RequestMapping("/people-who-called")
public class PeopleWhoCalledController {

    @Autowired
    PeopleWhoCalledService service;

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PostMapping("/add")
    private ResponseEntity<?> add(@RequestBody PeopleWhoCalledDto peopleWhoCalledDto) {
        ResponseApi add = service.add(peopleWhoCalledDto);
        if (add.isSuccess()) return ResponseEntity.ok(add);
        return ResponseEntity.status(409).body(add);
    }
  @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PostMapping("/make-order/{id}")
    private ResponseEntity<?> makerOrder(@PathVariable Long id,@RequestBody ReqOrderByPeopleWhoCalled peopleWhoCalledDto) {
        ResponseApi add = service.makeOrderByPeopleWhoCalled(id,peopleWhoCalledDto);
        if (add.isSuccess()) return ResponseEntity.ok(add);
        return ResponseEntity.status(409).body(add);
    }


    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PatchMapping("/edit-number-and-date/{id}")
    private ResponseEntity<?> editNumberOrDate(@PathVariable Long id, @RequestBody ReqPeopleWhoCalledToChangePhnOrDate peopleWhoCalledToChangePhnOrDate) {
        ResponseApi add = service.editPhoneNumberOrShouldCallDate(id, peopleWhoCalledToChangePhnOrDate);
        if (add.isSuccess()) return ResponseEntity.ok(add);
        return ResponseEntity.status(409).body(add);
    }


    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> editNumberOrDate(@PathVariable Long id) {
        ResponseApi delete = service.delete(id);
        if (delete.isSuccess()) return ResponseEntity.ok(delete);
        return ResponseEntity.status(409).body(delete);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/get-missed-people")
    private ResponseEntity<?> getMissedPeople(@RequestParam int page) {
        Page<ResPeopleWhoCalled> missedPeople = service.getMissedPeople(page);
        return ResponseEntity.ok(missedPeople);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
@GetMapping("/get-people-for-today")
    private ResponseEntity<?> getPeopleForToday() {
    List<ResPeopleWhoCalled> peopleForCallingToday = service.getPeopleForCallingToday();
    return ResponseEntity.ok(peopleForCallingToday);
    }
    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/get-should-call-people-in-future")
    private ResponseEntity<?> getPeopleForFuture(@RequestParam int page) {
        Page<ResPeopleWhoCalled> missedPeople = service.getShouldCallPeople(page);
        return ResponseEntity.ok(missedPeople);
    }
    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
 @GetMapping("/get-all")
    private ResponseEntity<?> getAll(@RequestParam int page) {
        Page<ResPeopleWhoCalled> missedPeople = service.getAll(page);
        return ResponseEntity.ok(missedPeople);
    }









}
