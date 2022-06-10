package usco.agrosoft.dao;

import usco.agrosoft.models.Role;

public interface RoleDao {
    Role getRoleByName(String nameRole);
}