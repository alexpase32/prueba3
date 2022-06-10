package usco.agrosoft.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import usco.agrosoft.dao.CountryDao;
import usco.agrosoft.models.Country;

@RestController
public class CountryController {
    
    @Autowired
    private CountryDao countryDao;

    @CrossOrigin
    @RequestMapping(value = "api/countries", method = RequestMethod.GET)
    public List<Country> getCountries() {
        return countryDao.getCountries();
    }

    @CrossOrigin
    @RequestMapping(value = "api/country/{idCountry}", method = RequestMethod.GET)
    public JSONObject getCountry(@PathVariable String idCountry) {
        JSONObject response = new JSONObject();
        response.put("error", "true");
        Country country = countryDao.getCountry(idCountry);
        if(country == null) {
            response.put("response", "País no encontrado");
            return response;
        }
        response.put("error", "false");
        response.put("response", country);
        return response;
    }
}
