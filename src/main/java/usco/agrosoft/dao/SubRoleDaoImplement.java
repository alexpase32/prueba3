package usco.agrosoft.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.SubRole;

@Repository
@Transactional
public class SubRoleDaoImplement implements SubRoleDao {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    FarmDao farmDao;

    @Override
    public List<SubRole> getSubRolesByIdFarm(String idFarm, String sorter, String order) {

        // get if is active?

        //boolean isActiveSubRole = true;

        String query = "FROM SubRole WHERE id_farm = :idFarm AND is_active_sub_role = :isActiveSubRole ORDER BY " + sorter + " " + order;

        try {
            List<SubRole> resultList = entityManager.createQuery(query)
                    .setParameter("idFarm", idFarm)
                    .setParameter("isActiveSubRole", true)
                    .getResultList();
            entityManager.close();
            return resultList;
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public JSONObject addSubRole(SubRole subRole) {
        // Tendrá que estar regitrado y con la cuenta activa

        JSONObject response = new JSONObject();

        response.put("error", true);

        //generate an idSubRole
        subRole.setIdSubRole(UUID.randomUUID().toString());

        // validate all fields of SubRole exist
        if (subRole.getIdFarm() == null || subRole.getIdFarm().isEmpty()) {
            response.put("response", "Se requiere el idFarm");
            return response;
        }
        if (subRole.getNameSubRole() == null || subRole.getNameSubRole().isEmpty()) {
            response.put("response", "Se requiere el nameSubRole");
            return response;
        }
        if (subRole.getDescriptionSubRole() == null || subRole.getDescriptionSubRole().isEmpty()) {
            response.put("response", "Se requiere la descriptionSubRole");
            return response;
        }

        subRole.setCreatedSubRole(LocalDateTime.now());
        subRole.setActiveSubRole(true);

        // validate nameSubRole min 1 max 20
        if (subRole.getNameSubRole().length() < 1 || subRole.getNameSubRole().length() > 20) {
            response.put("response", "El nombre del subRole debe tener entre 1 y 20 caracteres");
            return response;
        }

        // validate descriptionSubRole min 15 max 150
        if (subRole.getDescriptionSubRole().length() < 15 || subRole.getDescriptionSubRole().length() > 150) {
            response.put("response", "La descripción del subRole debe tener entre 15 y 150 caracteres");
            return response;
        }

        if (!farmDao.verifIdFarm(subRole.getIdFarm())) {
            response.put("response", "El id farm no existe!");
            return response;
        }

        try {
            entityManager.merge(subRole);
            response.put("error", false);
            response.put("response", "subRole creado correctamente!");
        } catch (IllegalArgumentException e) {
            response.put("error", true);
            response.put("response", "no se pudo crear un nuevo subRole");

            return response;
        }

        return response;
    }

    @Override
    public JSONObject updateSubRoleByIdSubRole(SubRole subRole) {

        JSONObject objRe = new JSONObject();

        String query = "FROM SubRole WHERE id_sub_role = :idSubRole";

        try {
            SubRole dbSubRole = (SubRole) entityManager.createQuery(query)
                    .setParameter("idSubRole", subRole.getIdSubRole())
                    .getSingleResult();

            dbSubRole.setNameSubRole(subRole.getNameSubRole());
            dbSubRole.setDescriptionSubRole(subRole.getDescriptionSubRole());
            entityManager.merge(dbSubRole);
            entityManager.close();

            objRe.put("error", false);
            objRe.put("response", "el nombre y descripcion del subrole fue actualizado correctamente");

            return objRe;

        } catch (Exception e) {

            objRe.put("error", true);
            objRe.put("response", "no se pudo actualizar el subrole");
            return objRe;

        }
    }

    @Override
    public JSONObject deleteSubRoleByIdSubRole(String idSubRole) {

        JSONObject objRe = new JSONObject();

        try {

            SubRole subRole = entityManager.find(SubRole.class, idSubRole);
            subRole.setActiveSubRole(false);
            entityManager.merge(subRole);
            entityManager.close();

            objRe.put("error", false);
            objRe.put("response", "subrole eliminado correctamente");
            return objRe;

        } catch (Exception e) {

            objRe.put("error", true);
            objRe.put("response", "no se pudo eliminar el subrole");
            return objRe;

        }

    }
    
    @Override
    @Transactional
    public SubRole getSubRole(String idSubRole) {
        String query = "FROM SubRole WHERE id_sub_role = :idSubRole AND is_active_sub_role = :isActiveSubRole";
        try {
            SubRole subRole = (SubRole) entityManager.createQuery(query)
                    .setParameter("idSubRole", idSubRole)
                    .setParameter("isActiveSubRole", true)
                    .getSingleResult();
            return subRole;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<SubRole> findSubRole(String idFarm, String search) {
        try{
            String query = "FROM SubRole WHERE id_farm = :idFarm AND is_active_sub_role = :isActiveSubRole AND lower(name_sub_role) LIKE lower(:search)";
            List<SubRole> subRoles = entityManager.createQuery(query)
                    .setParameter("idFarm", idFarm)
                    .setParameter("isActiveSubRole", true)
                    .setParameter("search", "%" + search.toLowerCase() + "%")
                    .getResultList();
            return subRoles;
        } catch (NoResultException e) {
            return null;
        }
    }

}
