package usco.agrosoft.dao;

import java.util.List;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.SubRole;

public interface SubRoleDao {

    List<SubRole> getSubRolesByIdFarm(String idFarm, String sorter, String order);

    JSONObject addSubRole(SubRole subRole);

    JSONObject updateSubRoleByIdSubRole(
            SubRole SubRole);

    JSONObject deleteSubRoleByIdSubRole(String idSubRole);

    SubRole getSubRole(String idSubRole);

    List<SubRole> findSubRole(String idFarm, String search);

}
