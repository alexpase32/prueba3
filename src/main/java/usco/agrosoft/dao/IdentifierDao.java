package usco.agrosoft.dao;

import usco.agrosoft.models.Identifier;

import java.util.List;

public interface IdentifierDao {
    List<Identifier> getIdentifiers();

    Identifier getIdentifier(int idIdentifier);
}
