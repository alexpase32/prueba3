package usco.agrosoft.dao;

import java.util.List;

import usco.agrosoft.models.Country;

public interface CountryDao {
    List<Country> getCountries();

    Country getCountry(String idCountry);
}
