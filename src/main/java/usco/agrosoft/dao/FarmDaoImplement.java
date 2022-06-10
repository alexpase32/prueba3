package usco.agrosoft.dao;
import usco.agrosoft.models.Country;
import usco.agrosoft.models.Farm;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.*; 
import java.time.format.*;

import net.minidev.json.JSONObject;

import java.util.List;

@Repository
@Transactional
public class FarmDaoImplement implements FarmDao {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserFarmDao userFarmDao;

    @Autowired
    CountryDao countryDao;

    @Override
    @Transactional
    public List<Farm> getFarms(String listIdFarms, String sorter, String order, String page) {
        try{
            //conver page to int
            int pageInt = Integer.parseInt(page);
            String preQuery ="FROM Farm WHERE id_farm IN ("+ listIdFarms + ") AND is_active_farm = :isActiveFarm ORDER BY " + sorter + " " + order;      
            //String preQuery ="FROM Farm WHERE id_farm IN ("+ listIdFarms + ") AND is_active_farm = :isActiveFarm LIKE :search";                                                                              
            List<Farm> result = (List<Farm>)entityManager.createQuery(preQuery)
                    //.setParameter("search", "%"+search+"%")
                    .setParameter("isActiveFarm", true)
                    .setFirstResult(pageInt * 10)
                    .setMaxResults(10)
                    .getResultList();
            return result;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    @Transactional
    public List<Farm> findFarms(String listIdFarms, String search, String page) {
        String preQuery ="FROM Farm WHERE id_farm IN ("+ listIdFarms + ") AND is_active_farm = :isActiveFarm AND lower(name_farm) LIKE lower(:search)";
        int pageInt = Integer.parseInt(page);
        List<Farm> result = (List<Farm>)entityManager.createQuery(preQuery)
                    .setParameter("search", "%"+search+"%")
                    .setParameter("isActiveFarm", true)
                    .setFirstResult(pageInt * 10)
                    .setMaxResults(10)
                    .getResultList(); 
        try{            
            return result; 
        }catch(NoResultException e){
            return null;
        }
        
    }

    @Override
    public Long getFarmCount(String listIdFarms) {
        try{
            String preQuery ="SELECT COUNT(*) FROM Farm WHERE id_farm IN ("+ listIdFarms + ") AND is_active_farm = :isActiveFarm";                                                                               
            Long result = (Long)entityManager.createQuery(preQuery)
                    .setParameter("isActiveFarm", true)
                    .getSingleResult();
            return result;
        } catch (NoResultException e){
            return 0l;
        }
    }

    @Override
    @Transactional
    public Farm getFarm(String idFarm) {
        String query = "FROM Farm WHERE id_farm = :idFarm AND is_active_farm = :isActiveFarm";
        try {
            Farm result = (Farm) entityManager.createQuery(query)
                    .setParameter("idFarm", idFarm)
                    .setParameter("isActiveFarm", true)
                    .getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public JSONObject addFarm(Farm farm) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if user is already in farm
        String nameFarm = farm.getNameFarm();
        String descriptionFarm = farm.getDescriptionFarm();
        String idCountry = farm.getIdCountry();
        LocalDateTime createdDate = farm.getCreatedDate();

        Farm farmToSave = new Farm();
        
        if (nameFarm.length()<=20 && nameFarm.length()>=1){
            farmToSave.setNameFarm(nameFarm);
        }else{
            response.put("response", "El nombre de la granja debe ser mayor a 1 caracter y menor o igual que 20 caracteres");
            return response;
        }
        if (descriptionFarm.length()>=15 && descriptionFarm.length()<=100){
            farmToSave.setDescriptionFarm(descriptionFarm);
        }else{
            response.put("response", "La descripcion de la granja debe ser mayor a 15 caracteres y menor o igual que 100 caracteres ");
            return response;
        }
        if(countryDao.getCountry(idCountry)==null){
            response.put("response", "La región de la granja no existe");
            return response;
            
        }else{
            farmToSave.setIdCountry(idCountry);
        }

        farmToSave.setActiveFarm(true);
        String idFarmToSave = UUID.randomUUID().toString();
        farmToSave.setIdFarm(idFarmToSave);
        farmToSave.setCreatedDate(createdDate);
        
        try{
            entityManager.merge(farmToSave);
            response.put("error", false);
            response.put("response", idFarmToSave);
            return response;
        } catch (Exception e){
            response.put("response", "Error al guardar la granja");
            return response;
        }
    }
    
    @Override
    @Transactional
    public String deleteFarm(String idFarm) {
        String query = "FROM Farm WHERE id_farm = :idFarm AND is_active_farm = :isActiveFarm";
        try{
            Farm farm = (Farm) entityManager.createQuery(query)
                    .setParameter("idFarm", idFarm)
                    .setParameter("isActiveFarm", true)
                    .getSingleResult();

            if(farm == null){
                return "0";
            }

            farm.setActiveFarm(false);
            entityManager.merge(farm);

            //delete all user_farm with this farm
            userFarmDao.deleteUserFarmByFarmId(idFarm);
            
            //return 1 when the farm is deleted successfully
            return "1";
        }catch (NoResultException e){
            //return 0 when the farm is deleted before or not exist
            return "0";
        }
    }
    
    @Override
    @Transactional
    public JSONObject modifyFarm(Farm farm){
        String query = "FROM Farm WHERE id_farm = :idFarm AND is_active_farm = :isActiveFarm";
        JSONObject response = new JSONObject();
        response.put("error", true);
        try{
            Farm farmFound = (Farm) entityManager.createQuery(query)
                .setParameter("idFarm", farm.getIdFarm())
                .setParameter("isActiveFarm", true)
                .getSingleResult();
            
            String nameFarm = farm.getNameFarm();
            String descriptionFarm = farm.getDescriptionFarm(); 
            if (nameFarm.length()<=20 && nameFarm.length()>=1){
                farmFound.setNameFarm(nameFarm);
            }else{
                response.put("response", "El nombre de la granja debe ser mayor a 1 caracter y menor o igual que 20 caracteres");
                return response;
            }
            if (descriptionFarm.length()>=15 && descriptionFarm.length()<=100){
                farmFound.setDescriptionFarm(descriptionFarm);
            }else{
                response.put("response", "La descripcion de la granja debe ser mayor a 15 caracteres y menor o igual que 100 caracteres ");
                return response;
            }
            try{
                entityManager.merge(farmFound);
                entityManager.close();
                response.put("error", false);
                response.put("response", "Cambios guardados con exito");
                return response;
            }catch(NoResultException e){
                response.put("response", "Error al guardar los cambios");
                return response;
            }
        } catch (NoResultException e){
            response.put("response", "La granja a modificar no existe");
            return response;
        }
        
    }
    @Override
    public boolean verifIdFarm(String idFarm) {
        //validate if the idFarm is exist
        String query = "FROM Farm WHERE id_farm = :idFarm AND is_active_farm = :isActiveFarm";
        try{
            Farm farm = (Farm) entityManager.createQuery(query)
                    .setParameter("idFarm", idFarm)
                    .setParameter("isActiveFarm", true)
                    .getSingleResult();
            return true;
    } catch (NoResultException e){
            return false;
        }
    }

    @Override
    public List<Farm> listFarms(String listIdFarms) {
        String query = "FROM Farm WHERE id_farm IN (" + listIdFarms + ") AND is_active_farm = :isActiveFarm";
        try{
            List<Farm> farms = (List<Farm>) entityManager.createQuery(query)
                    .setParameter("isActiveFarm", true)
                    .getResultList();
            return farms;
        } catch (NoResultException e){
            return null;
        }
    }

}