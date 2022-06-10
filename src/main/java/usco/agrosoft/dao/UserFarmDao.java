package usco.agrosoft.dao;

import java.util.List;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.Farm;
import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;

public interface UserFarmDao {
    UserFarm saveUserFarm(UserFarm userFarm);
    List<UserFarm> getUserFarms(String idUser, String idRole);
    List<UserFarm> getEmployees(String idFarm, String idRole);
    JSONObject deleteUserFarmByFarmId(String idFarm);
    JSONObject deleteEmployee(String idUser, String idFarm);
    JSONObject addEmployee(String idUser, String idFarm);
    String getAdminId(String idFarm);
    JSONObject changeAdmin(String adminId, String newAdminId, String idFarm);
    String getUserFarmIdByEmployee(String idEmployee, String farmId);
    List<String> listUserFarms(String idUser);
    
}
