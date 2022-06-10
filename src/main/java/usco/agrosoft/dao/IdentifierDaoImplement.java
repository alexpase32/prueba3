package usco.agrosoft.dao;

import org.springframework.stereotype.Repository;
import usco.agrosoft.models.Identifier;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    @Override
    @Transactional
    public Identifier getIdentifier(int idIdentifier) {
        String query = "FROM Identifier WHERE id_identifier = :idIdentifier";

        try {
            Identifier identifier = (Identifier)entityManager.createQuery(query)
                    .setParameter("idIdentifier", idIdentifier)
                    .getSingleResult();
            
            return identifier;
        } catch (NoResultException e) {
            return null;
        }
    }
}
