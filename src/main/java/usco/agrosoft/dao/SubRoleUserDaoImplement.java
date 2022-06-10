package usco.agrosoft.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.SubRoleUser;

@Transactional
@Repository
public class SubRoleUserDaoImplement implements SubRoleUserDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public String getSubRoleId(String idUserFarm) {
        String query = "FROM SubRoleUser WHERE idUserFarm = :idUserFarm AND is_active_sub_role_user = :isActiveSubRoleUser";
        try {
            SubRoleUser subRoleUser = (SubRoleUser) entityManager.createQuery(query)
                .setParameter("idUserFarm", idUserFarm)
                .setParameter("isActiveSubRoleUser", true)
                .getSingleResult();
            return subRoleUser.getIdSubRole();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public JSONObject addSubRole() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject changeSubRole(String idSubRole) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject deleteSubRoleUser(String idSubRole) {
        JSONObject response = new JSONObject();
        response.put("error", true);
        String query = "FROM SubRoleUser WHERE id_sub_role_user = :idSubRoleUser AND is_active_sub_role_user = :isActiveSubRoleUser";
        try{
            SubRoleUser subRoleUser = (SubRoleUser) entityManager.createQuery(query)
                .setParameter("idSubRoleUser", idSubRole)
                .setParameter("isActiveSubRoleUser", true)
                .getSingleResult();
            subRoleUser.setActiveSubRoleUser(false);
            entityManager.merge(subRoleUser);
            response.put("error", false);
            response.put("response", "SubRoleUser eliminado");
            return response;
        } catch (NoResultException e){
            response.put("response", "No se encontro el SubRoleUser");
            return response;
        }
    }

    @Override
    public JSONObject addSubRoleUser(String userFarmId, String idSubRole) {
        // TODO Auto-generated method stub
        JSONObject response = new JSONObject();
        response.put("error", true);

        String query = "FROM SubRoleUser WHERE id_user_farm = :idUserFarm AND is_active_sub_role_user = :isActiveSubRoleUser";
        try{
            SubRoleUser subRoleUserFound = (SubRoleUser) entityManager.createQuery(query)
                .setParameter("idUserFarm", userFarmId)
                .setParameter("isActiveSubRoleUser", true)
                .getSingleResult();
            subRoleUserFound.setIdSubRole(idSubRole);
            entityManager.merge(subRoleUserFound);
            response.put("error", false);
            response.put("response", "Se ha modificado el SubRoleUser");
            return response;
        } catch (NoResultException e){
            SubRoleUser subRoleUser = new SubRoleUser();
            subRoleUser.setIdSubRoleUser(UUID.randomUUID().toString());
            subRoleUser.setIdSubRole(idSubRole);
            subRoleUser.setIdUserFarm(userFarmId);
            subRoleUser.setActiveSubRoleUser(true);

            entityManager.merge(subRoleUser);

            response.put("error", false);
            response.put("response", "subRoleUser agreagado correctamente");

            return response;
        }
        
    }

}
