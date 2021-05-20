package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.CurrencyType;
import uz.viento.crm_system.payload.CurrencyDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.CurrencyService;

import java.util.List;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;


    @PostMapping("/add")
    public ResponseEntity<?> addCurrency(@RequestBody CurrencyDto currencyDto) {
        ResponseApi responseApi = currencyService.addCurrency(currencyDto);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editCurrency(@PathVariable Long id, @RequestBody CurrencyDto currencyDto) {
        ResponseApi responseApi = currencyService.editCurrency(id, currencyDto);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id) {
        ResponseApi responseApi = currencyService.deleteCurrency(id);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<CurrencyType> currencies = currencyService.getCurrencies();
        if (currencies.isEmpty()) return ResponseEntity.status(409).body(currencies);
        return ResponseEntity.ok(currencies);
    }

}
