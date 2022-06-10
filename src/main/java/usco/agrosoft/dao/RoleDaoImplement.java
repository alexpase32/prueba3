package usco.agrosoft.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import usco.agrosoft.models.Role;

@Repository
@Transactional
public class RoleDaoImplement implements RoleDao {

    @PersistenceContext
    EntityManager entityManager;

    Role role = new Role();

    @Override
    @Transactional
    public Role getRoleByName(String nameRole) {
        String query = "FROM Role WHERE name_role = :nameRole";
        //To solve transactional error, change the exception type to NoResultException
        try{

            role = (Role) entityManager.createQuery(query)
                    .setParameter("nameRole", nameRole)
                    .getSingleResult();
            
            return role;
        }catch(NoResultException e){
            return null;
        }

    }
}
