package usco.agrosoft.dao;

import org.springframework.stereotype.Repository;
import usco.agrosoft.models.Identifier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class IdentifierDaoImplement implements IdentifierDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public List<Identifier> getIdentifiers() {
        String query = "FROM Identifier u";
        List<Identifier> result = entityManager.createQuery(query).getResultList();
        return result;
    }
}
