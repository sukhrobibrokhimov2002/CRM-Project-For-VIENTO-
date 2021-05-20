package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.CurrencyType;
import uz.viento.crm_system.payload.CurrencyDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.repository.CurrencyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    @Autowired
    CurrencyRepository currencyRepository;


    public ResponseApi addCurrency(CurrencyDto currencyDto) {
        boolean existsByName = currencyRepository.existsByAbbreviation(currencyDto.getAbbreviation());
        if (existsByName) return new ResponseApi("Currency already exists", false);
        CurrencyType currencyType = new CurrencyType();
        currencyType.setAbbreviation(currencyDto.getAbbreviation());
        currencyType.setName(currencyDto.getName());
        currencyRepository.save(currencyType);
        return new ResponseApi("Successfully added", true);
    }

    public ResponseApi editCurrency(long id, CurrencyDto currencyDto) {
        Optional<CurrencyType> optionalCurrencyType = currencyRepository.findById(id);
        if (!optionalCurrencyType.isPresent()) return new ResponseApi("Currency not found", false);

        boolean existsByName = currencyRepository.existsByAbbreviationAndIdNot(currencyDto.getAbbreviation(), id);
        if (existsByName) return new ResponseApi("Currency already exists", false);
        CurrencyType currencyType = optionalCurrencyType.get();
        if (currencyDto.getAbbreviation() != null) {
            currencyType.setAbbreviation(currencyDto.getAbbreviation());
        }
        if (currencyDto.getName() != null) {
            currencyType.setName(currencyDto.getName());
        }
        currencyRepository.save(currencyType);
        return new ResponseApi("Successfully edited", true);
    }


    public ResponseApi deleteCurrency(long id) {
        try {
            currencyRepository.deleteById(id);
            return new ResponseApi("Successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }
    }

    public List<CurrencyType> getCurrencies() {
        List<CurrencyType> all = currencyRepository.findAll();
        return all;
    }
}
