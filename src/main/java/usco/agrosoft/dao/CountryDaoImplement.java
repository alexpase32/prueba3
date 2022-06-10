package usco.agrosoft.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import usco.agrosoft.models.Country;

@Repository
@Transactional
public class CountryDaoImplement implements CountryDao {

    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    public List<Country> getCountries() {
        String query = "FROM Country";
        try{
            List<Country> result = entityManager.createQuery(query).getResultList();
            return result;
        } catch(Exception e){
            return null;
        }
    }

    @Override
    public Country getCountry(String idCountry) {
        String query = "FROM Country WHERE id_country = :idCountry";
        try{
            Country country = (Country) entityManager.createQuery(query)
                    .setParameter("idCountry", idCountry)
                    .getSingleResult();
            return country;
        } catch(Exception e){
            return null;
        }
    }
}
