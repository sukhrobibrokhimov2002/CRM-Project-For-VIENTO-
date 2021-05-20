package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.PeopleWhoCalled;
import uz.viento.crm_system.payload.PeopleWhoCalledDto;
import uz.viento.crm_system.payload.ReqPeopleWhoCalledToChangePhnOrDate;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.PeopleWhoCalledService;

import java.util.List;

@RestController
@RequestMapping("/people-who-called")
public class PeopleWhoCalledController {

    @Autowired
    PeopleWhoCalledService service;


    @PostMapping("/add")
    private ResponseEntity<?> add(@RequestBody PeopleWhoCalledDto peopleWhoCalledDto) {
        ResponseApi add = service.add(peopleWhoCalledDto);
        if (add.isSuccess()) return ResponseEntity.ok(add);
        return ResponseEntity.status(409).body(add);
    }

    @PatchMapping("/edit-number-and-date/{id}")
    private ResponseEntity<?> editNumberOrDate(@PathVariable Long id, @RequestBody ReqPeopleWhoCalledToChangePhnOrDate peopleWhoCalledToChangePhnOrDate) {
        ResponseApi add = service.editPhoneNumberOrShouldCallDate(id, peopleWhoCalledToChangePhnOrDate);
        if (add.isSuccess()) return ResponseEntity.ok(add);
        return ResponseEntity.status(409).body(add);
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> editNumberOrDate(@PathVariable Long id) {
        ResponseApi delete = service.delete(id);
        if (delete.isSuccess()) return ResponseEntity.ok(delete);
        return ResponseEntity.status(409).body(delete);
    }


    @GetMapping("/get-missed-people")
    private ResponseEntity<?> getMissedPeople(@RequestParam int page) {
        Page<PeopleWhoCalled> missedPeople = service.getMissedPeople(page);
        return ResponseEntity.ok(missedPeople);
    }


@GetMapping("/get-people-for-today")
    private ResponseEntity<?> getPeopleForToday() {
    List<PeopleWhoCalled> peopleForCallingToday = service.getPeopleForCallingToday();
    return ResponseEntity.ok(peopleForCallingToday);
    }

    @GetMapping("/get-should-call-people-in-future")
    private ResponseEntity<?> getPeopleForFuture(@RequestParam int page) {
        Page<PeopleWhoCalled> missedPeople = service.getShouldCallPeople(page);
        return ResponseEntity.ok(missedPeople);
    }

 @GetMapping("/get-all")
    private ResponseEntity<?> getAll(@RequestParam int page) {
        Page<PeopleWhoCalled> missedPeople = service.getAll(page);
        return ResponseEntity.ok(missedPeople);
    }







}
