package usco.agrosoft.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.Farm;
import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;


@Repository
@Transactional
public class UserFarmDaoImplement implements UserFarmDao {

    @PersistenceContext
    private EntityManager entityManager;

    
    UserFarm userFarm = new UserFarm();

    @Override
    @Transactional
    public UserFarm saveUserFarm(UserFarm userFarm) {
        try{
            entityManager.merge(userFarm);
            return userFarm;
        }catch(NoResultException e){
            //possible error with transactional
            return null;
        }
    }    

    @Override
    @Transactional
    public List<UserFarm> getUserFarms(String idUser, String idRole) {
        try{
            
            String query = "FROM UserFarm WHERE id_User = :idUser AND id_Role = :idRole AND is_active_user_farm = :isActiveUserFarm";
            List<UserFarm> userFarms = entityManager.createQuery(query)
                .setParameter("idUser", idUser)
                .setParameter("idRole", idRole)
                .setParameter("isActiveUserFarm", true)
                .getResultList();
            return userFarms;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    @Transactional
    public List<UserFarm> getEmployees(String idFarm, String idRole) {
        try{
            
            String query = "FROM UserFarm WHERE id_Farm = :idFarm AND id_Role = :idRole AND is_active_user_farm = :isActiveUserFarm";
            List<UserFarm> employees = entityManager.createQuery(query)
                .setParameter("idRole", idRole)
                .setParameter("idFarm", idFarm)
                .setParameter("isActiveUserFarm", true)
                .getResultList();
            return employees;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    @Transactional
    public JSONObject deleteUserFarmByFarmId(String farmId) {
        JSONObject response = new JSONObject();
        response.put("Error", false);
        String query = "FROM UserFarm WHERE id_Farm = :idFarm AND is_active_user_farm = :isActiveUserFarm";
        List<UserFarm> userFarms = entityManager.createQuery(query)
            .setParameter("idFarm", farmId)
            .setParameter("isActiveUserFarm", true)
            .getResultList();

        if(userFarms == null || userFarms.isEmpty()){
            response.put("Error", true);
            response.put("response", "No se encontró la granja");
            return response;
        }

        for(UserFarm userFarm : userFarms){
            userFarm.setActiveUserFarm(false);
            entityManager.merge(userFarm);
        }

        response.put("response", "Se eliminó la granja");
        return response;
    }

    @Override
    @Transactional
    public JSONObject deleteEmployee(String idUser, String idFarm) {
        //set is_active_user_farm = false
        JSONObject response = new JSONObject();
        response.put("Error", true);

        String query = "FROM UserFarm WHERE id_User = :idUser AND id_Farm = :idFarm AND is_active_user_farm = :isActiveUserFarm AND id_Role = :idRole";

        try{
            UserFarm userFarm = (UserFarm) entityManager.createQuery(query)
                .setParameter("idUser", idUser)
                .setParameter("idFarm", idFarm)
                .setParameter("isActiveUserFarm", true)
                .setParameter("idRole", "2")
                .getSingleResult();
    
            userFarm.setActiveUserFarm(false);
            entityManager.merge(userFarm);
    
            response.put("Error", false);
            response.put("response", "Se eliminó el empleado");
            
            return response;

        } catch (NoResultException e){
            response.put("response", "No se encontró el empleado en esta finca");
            return response;
        }
    }

    @Override
    @Transactional
    public JSONObject addEmployee(String idUser, String idFarm) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if user is already in farm
        String query = "FROM UserFarm WHERE id_User = :idUser AND id_Farm = :idFarm AND is_active_user_farm = :isActiveUserFarm AND id_Role = :idRole";

        try{
            UserFarm userFarm = (UserFarm) entityManager.createQuery(query)
                .setParameter("idUser", idUser)
                .setParameter("idFarm", idFarm)
                .setParameter("isActiveUserFarm", true)
                .setParameter("idRole", "2")
                .getSingleResult();
                
            response.put("response", "El empleado ya está en esta finca");
            return response;
        } catch (NoResultException e){
            //Save userFarm
            UserFarm userFarmToSave = new UserFarm();
            userFarmToSave.setIdUserFarm(UUID.randomUUID().toString());
            userFarmToSave.setIdUser(idUser);
            userFarmToSave.setIdFarm(idFarm);
            userFarmToSave.setIdRole("2");
            userFarmToSave.setActiveUserFarm(true);
    
            entityManager.merge(userFarmToSave);
    
            response.put("error", false);
            response.put("response", "Se agregó el empleado");
            return response;
        }
        
    }

    @Override
    public String getAdminId(String idFarm) {
        String query = "FROM UserFarm WHERE id_farm = :idFarm AND id_role = :idRole AND is_active_user_farm = :isActiveUserFarm";
        try{
            UserFarm userFarm = (UserFarm) entityManager.createQuery(query)
                .setParameter("idFarm", idFarm)
                .setParameter("idRole", "1")
                .setParameter("isActiveUserFarm", true)
                .getSingleResult();
            
            String adminId = userFarm.getIdUser();
            return adminId;
        } catch (NoResultException e) {
            return null;
        }
        
    }

    @Override
    public JSONObject changeAdmin(String adminId, String newAdminId, String idFarm) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        String query = "FROM UserFarm WHERE id_user = :idUser AND id_farm = :idFarm AND id_role = :idRole AND is_active_user_farm = :isActiveUserFarm";
        try{
            UserFarm userFarm = (UserFarm) entityManager.createQuery(query)
                .setParameter("idUser", adminId)
                .setParameter("idFarm", idFarm)
                .setParameter("idRole", "1")
                .setParameter("isActiveUserFarm", true)
                .getSingleResult();

            userFarm.setIdUser(newAdminId);
            entityManager.merge(userFarm);
            response.put("error", false);
            response.put("response", "Se cambió el administrador");
            return response;
        } catch (NoResultException e){
            response.put("response", "No se ha podido cambiar el administrador");
            return response;
        }
    }

    @Override
    @Transactional
    public String getUserFarmIdByEmployee(String idEmployee, String farmId) {
        String query = "FROM UserFarm WHERE id_user = :idUser AND id_farm = :idFarm AND id_role = :idRole AND is_active_user_farm = :isActiveUserFarm";
        try{
            UserFarm userFarm = (UserFarm) entityManager.createQuery(query)
                .setParameter("idUser", idEmployee)
                .setParameter("idFarm", farmId)
                .setParameter("idRole", "2")
                .setParameter("isActiveUserFarm", true)
                .getSingleResult();
            
            String userFarmId = userFarm.getIdUserFarm();
            return userFarmId;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public List<String> listUserFarms(String idUser) {
        String query = "SELECT u.idFarm FROM UserFarm u WHERE id_user = :idUser AND id_role = :idRole AND is_active_user_farm = :isActiveUserFarm";
        try{
            List<String> farmsId = entityManager.createQuery(query)
                .setParameter("idUser", idUser)
                .setParameter("idRole", "1")
                .setParameter("isActiveUserFarm", true)
                .getResultList();
            return farmsId;
        } catch (NoResultException e){
            return null;
        }
    } 
}
