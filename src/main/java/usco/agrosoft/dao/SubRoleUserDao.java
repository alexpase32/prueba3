package usco.agrosoft.dao;

import java.util.List;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.SubRoleUser;

public interface SubRoleUserDao {

    String getSubRoleId(String idUserFarm);

    JSONObject addSubRole();

    JSONObject changeSubRole(String idSubRole);

    JSONObject deleteSubRoleUser(String idSubRole);

    JSONObject addSubRoleUser(String userFarmId, String idSubRole);

}
