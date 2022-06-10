package usco.agrosoft.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import usco.agrosoft.dao.FarmDao;
import usco.agrosoft.dao.RoleDao;
import usco.agrosoft.dao.SubRoleDao;
import usco.agrosoft.dao.SubRoleUserDao;
import usco.agrosoft.dao.UserDao;
import usco.agrosoft.dao.UserFarmDao;
import usco.agrosoft.dao.UserFarmDaoImplement;
import usco.agrosoft.models.Farm;
import usco.agrosoft.models.Role;
import usco.agrosoft.models.ShowEmployee;
import usco.agrosoft.models.ShowFarmToEmployee;
import usco.agrosoft.models.SubRole;
import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;

@RestController
public class UserFarmController {

    UserFarm userFarm = new UserFarm();

    @Autowired
    private FarmDao farmDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserFarmDao userFarmDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SubRoleUserDao subRoleUserDao;

    @Autowired
    private SubRoleDao subRoleDao;

    @CrossOrigin
    @RequestMapping(value = "api/saveuserfarm", method = RequestMethod.POST)
    public JSONObject saveUserFarm(@RequestBody JSONObject userFarmObject) {
        
        JSONObject response = new JSONObject();
        response.put("error", true);

        //Validate data
        if(userFarmObject.get("idUser") == null || userFarmObject.get("idUser").equals("")){
            response.put("response", "Falta el campo idUser");
            return response;
        }
        if(userFarmObject.get("idRole") == null || userFarmObject.get("idRole").equals("")){
            response.put("response", "Falta el campo idRole");
            return response;
        }
        if(userFarmObject.get("idCountry") == null || userFarmObject.get("idCountry").equals("")){
            response.put("response", "Falta el campo idCountry");
            return response;
        }
        if(userFarmObject.get("nameFarm") == null || userFarmObject.get("nameFarm").equals("")){
            response.put("response", "Falta el campo nameFarm");
            return response;
        }
        if(userFarmObject.get("descriptionFarm") == null || userFarmObject.get("descriptionFarm").equals("")){
            response.put("response", "Falta el campo descriptionFarm");
            return response;
        }


        //Create an object of type Farm
        Farm farm = new Farm();
        farm.setIdCountry(userFarmObject.get("idCountry").toString());
        farm.setNameFarm(userFarmObject.get("nameFarm").toString());
        farm.setDescriptionFarm(userFarmObject.get("descriptionFarm").toString());
        farm.setCreatedDate(LocalDateTime.now());

        JSONObject farmResponse = farmDao.addFarm(farm);
        

        if(farmResponse.get("error").equals(true)){
            response.put("response", farmResponse.get("response"));
            return response;
        }

        //create an object of type UserFarm
        userFarm.setIdUserFarm(UUID.randomUUID().toString());
        userFarm.setIdUser(userFarmObject.get("idUser").toString());
        userFarm.setIdRole(userFarmObject.get("idRole").toString());
        userFarm.setIdFarm(farmResponse.get("response").toString());
        userFarm.setActiveUserFarm(true);

        UserFarm userFarmDaoResponse = userFarmDao.saveUserFarm(userFarm);

        if(userFarmDaoResponse == null){
            response.put("response", "Error al guardar la finca");
            return response;
        }

        response.put("error", false);
        response.put("response", "Finca guardada correctamente");
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/userfarms/{idUser}/{nameRole}/{sorter}/{order}/{page}", method = RequestMethod.GET)
    public JSONObject userFarms(@PathVariable String idUser, @PathVariable String nameRole, @PathVariable String sorter, @PathVariable String order, @PathVariable String page) {

        JSONObject response = new JSONObject();
        response.put("error", true);
        
        if(idUser == null || idUser.equals("")){
            response.put("response", "Falta el campo idUser");
            return response;
        }

        if(nameRole == null || nameRole.equals("")){
            response.put("response", "Falta el campo rolName");
            return response;
        }

        Role role = roleDao.getRoleByName(nameRole);

        if(role == null){
            response.put("response", "El rol no existe");
            return response;
        }

        String idRole = role.getIdRole();
        
        List<UserFarm> userFarms = userFarmDao.getUserFarms(idUser, idRole);

        if(userFarms == null || userFarms.isEmpty()){
            response.put("error", false);
            response.put("response", userFarms);
            response.put("maxPage", null);
            return response;
        }
        //create list from userFarms of id_farm
        String listIdFarms = "";
        for(int i = 0; i < userFarms.size(); i++){
            listIdFarms += "'" + userFarms.get(i).getIdFarm() + "'";
            if(i != userFarms.size() - 1){
                listIdFarms += ",";
            }
        }
        //obtain the total pages in getFarmCount
        Long maxPage = farmDao.getFarmCount(listIdFarms) / 10;

        List<Farm> farms = farmDao.getFarms(listIdFarms, sorter, order, page);

        List<ShowFarmToEmployee> showFarms = new ArrayList<>();

        //run farms to load the new model
        for(int i = 0; i < farms.size(); i++){
            ShowFarmToEmployee showFarm = new ShowFarmToEmployee();
            showFarm.setIdFarm(farms.get(i).getIdFarm());
            showFarm.setNameFarm(farms.get(i).getNameFarm());
            showFarm.setDescriptionFarm(farms.get(i).getDescriptionFarm());
            showFarm.setIdCountry(farms.get(i).getIdCountry());
            showFarm.setCreatedDate(farms.get(i).getCreatedDate());
            showFarm.setActiveFarm(farms.get(i).isActiveFarm());
            //add the nameAdmmin
            String adminId = userFarmDao.getAdminId(farms.get(i).getIdFarm());
            User admin = userDao.getUser(adminId);
            showFarm.setNameAdmin(admin.getName() + " " + admin.getLastName());
            showFarms.add(showFarm);
        }

        if(farms == null){
            response.put("response", "Error al obtener las fincas");
            return response;
        }

        response.put("error", false);
        response.put("response", showFarms);
        response.put("maxPage", maxPage);

        return response;
    }
    
    @CrossOrigin
    @RequestMapping(value = "api/employees/{idFarm}/{sorter}/{order}/{page}", method = RequestMethod.GET)
    public JSONObject employees(@PathVariable String idFarm, @PathVariable String sorter, @PathVariable String order, @PathVariable String page) {
        
        JSONObject response = new JSONObject();
        response.put("error", true);
        
        if(idFarm == null || idFarm.equals("")){
            response.put("response", "Falta el campo idFarm");
            return response;
        }

        //get idRole by idName
        String idRole = roleDao.getRoleByName("employee").getIdRole();


        List<UserFarm> userFarms = userFarmDao.getEmployees(idFarm, idRole);

        if(userFarms == null || userFarms.isEmpty()){
            response.put("error", false);
            response.put("response", userFarms);
            return response;
        }

        //create list from userFarms of id_farm
        String listIdUsers = "";
        for(int i = 0; i < userFarms.size(); i++){
            listIdUsers += "'" + userFarms.get(i).getIdUser() + "'";
            if(i != userFarms.size() - 1){
                listIdUsers += ",";
            }
        }

        //obtain the total pages in getUsersCount
        Long maxPage = userDao.getUsersCount(listIdUsers) / 10;


        List<User> users = userDao.getUsersByIdList(listIdUsers, sorter, order ,page);

        //set subRoleId and subRoleName
        List<ShowEmployee> showEmployees = new ArrayList<>();
        for(int i = 0; i < users.size(); i++){
            ShowEmployee showEmployee = new ShowEmployee();
            showEmployee.setIdUser(users.get(i).getIdUser());
            showEmployee.setName(users.get(i).getName());
            showEmployee.setLastName(users.get(i).getLastName());
            showEmployee.setPhoneNumber(users.get(i).getPhoneNumber());
            showEmployee.setEmail(users.get(i).getEmail());
            //get userFarm by idUser and idFarm
            String userFarmId = userFarmDao.getUserFarmIdByEmployee(users.get(i).getIdUser(), idFarm);
            //get subroleId from subroleuserDao
            String subRoleId = subRoleUserDao.getSubRoleId(userFarmId);
            if(subRoleId == null){
                //users.get(i).setPassword("N/A");
                showEmployee.setIdSubRole(null);
                showEmployee.setNameSubRole("N/A");
            }else{
                //get name_sub_role from subroleDao
                String nameSubRole = subRoleDao.getSubRole(subRoleId).getNameSubRole();
                //users.get(i).setPassword(nameSubRole);
                //users.get(i).setTokenUser(subRoleId);
                showEmployee.setIdSubRole(subRoleId);
                showEmployee.setNameSubRole(nameSubRole);
            }
            showEmployees.add(showEmployee);
        }

        if(users == null){
            response.put("response", "Error al obtener los usuarios");
            return response;
        }

        response.put("error", false);
        response.put("response", showEmployees);
        response.put("maxPage", maxPage);

        return response;
    }
    
    @CrossOrigin
    @RequestMapping(value = "api/findemployees/{idFarm}/{search}/{page}", method = RequestMethod.GET)
    public JSONObject findEmployees(@PathVariable String idFarm, @PathVariable String search, @PathVariable String page){
        //this method returns a list of the employees of a farm by a search
        JSONObject response = new JSONObject();
        response.put("error", true);
        if(idFarm == null || idFarm.equals("")){
            response.put("response", "Falta el campo idUserFarm");
            return response;
        }
        String idRole = roleDao.getRoleByName("employee").getIdRole();
        
        List<UserFarm> users = userFarmDao.getEmployees(idFarm, idRole);

        if(users == null || users.isEmpty()){
            response.put("error", false);
            response.put("response", users);
            return response;
        }
        String listIdUsers = "";
        for(int i = 0; i < users.size(); i++){
            listIdUsers += "'" + users.get(i).getIdUser() + "'";
            if(i != users.size() - 1){
                listIdUsers += ",";
            }
        }
        List<User> usersEmployee = userDao.getEmployeeByIdList(listIdUsers, search, page);

        //set password to null
        //set subRoleId and subRoleName
        List<ShowEmployee> showEmployees = new ArrayList<>();
        for(int i = 0; i < usersEmployee.size(); i++){
            ShowEmployee showEmployee = new ShowEmployee();
            showEmployee.setIdUser(usersEmployee.get(i).getIdUser());
            showEmployee.setName(usersEmployee.get(i).getName());
            showEmployee.setLastName(usersEmployee.get(i).getLastName());
            showEmployee.setPhoneNumber(usersEmployee.get(i).getPhoneNumber());
            showEmployee.setEmail(usersEmployee.get(i).getEmail());
            //get userFarm by idUser and idFarm
            String userFarmId = userFarmDao.getUserFarmIdByEmployee(usersEmployee.get(i).getIdUser(), idFarm);
            //get subroleId from subroleuserDao
            String subRoleId = subRoleUserDao.getSubRoleId(userFarmId);
            if(subRoleId == null){
                //users.get(i).setPassword("N/A");
                showEmployee.setIdSubRole(null);
                showEmployee.setNameSubRole("N/A");
            }else{
                //get name_sub_role from subroleDao
                String nameSubRole = subRoleDao.getSubRole(subRoleId).getNameSubRole();
                showEmployee.setIdSubRole(subRoleId);
                showEmployee.setNameSubRole(nameSubRole);
            }
            showEmployees.add(showEmployee);
        }

        response.put("error", false);
        response.put("response", showEmployees);
        return response;
    } 

    @CrossOrigin
    @RequestMapping(value = "api/deleteemployee", method = RequestMethod.PUT)
    public JSONObject deleteEmployee(@RequestBody UserFarm userFarm){

        JSONObject response = new JSONObject();
        response.put("error", true);

        String idUser = userFarm.getIdUser();
        String idFarm = userFarm.getIdFarm();

        if(idUser == null || idUser.equals("")){
            response.put("response", "Falta el campo idUser");
            return response;
        }

        if(idFarm == null || idFarm.equals("")){
            response.put("response", "Falta el campo idFarm");
            return response;
        }

        JSONObject userFarmResponse = userFarmDao.deleteEmployee(idUser, idFarm);

        return userFarmResponse;
    }

    @CrossOrigin
    @RequestMapping(value = "api/addemployee", method = RequestMethod.POST)
    public JSONObject addEmployee(@RequestBody UserFarm userFarm){

        JSONObject response = new JSONObject();
        response.put("error", true);

        String idUser = userFarm.getIdUser();
        String idFarm = userFarm.getIdFarm();

        if(idUser == null || idUser.equals("")){
            response.put("response", "Falta el campo idUser");
            return response;
        }

        if(idFarm == null || idFarm.equals("")){
            response.put("response", "Falta el campo idFarm");
            return response;
        }

        //validate if the idUser exists
        User user = userDao.getUser(idUser);

        if(user == null){
            response.put("response", "El usuario no existe");
            return response;
        }

        //validate if the idFarm exists
        Farm farm = farmDao.getFarm(idFarm);

        if(farm == null){
            response.put("response", "La finca no existe");
            return response;
        }

        JSONObject userFarmResponse = userFarmDao.addEmployee(idUser, idFarm);

        return userFarmResponse;
    }

    @CrossOrigin
    @RequestMapping(value = "api/addsubroleuser", method = RequestMethod.POST)
    public JSONObject addSubRoleUser(@RequestBody JSONObject subRoleUser){
        //this method add or modify subRoleUser
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate idEmployee not null
        if(subRoleUser.get("idUser") == null || subRoleUser.get("idUser").equals("")){
            response.put("response", "Falta el campo idEmployee");
            return response;
        }

        //validate if farmId not null
        if(subRoleUser.get("idFarm") == null || subRoleUser.get("idFarm").equals("")){
            response.put("response", "Falta el campo idFarm");
            return response;
        }

        //validate id idSubRole not null
        if(subRoleUser.get("idSubRole") == null || subRoleUser.get("idSubRole").equals("")){
            response.put("response", "Falta el campo idSubRole");
            return response;
        }

        //assign variables from json
        String idEmployee = subRoleUser.get("idUser").toString();

        //validate if idEmployee exists in User
        User user = userDao.getUser(idEmployee);
        if(user == null){
            response.put("response", "El usuario no existe");
            return response;
        }

        String idFarm = subRoleUser.get("idFarm").toString();

        //validate if farmId exists in Farm
        Farm farm = farmDao.getFarm(idFarm);
        if(farm == null){
            response.put("response", "La finca no existe");
            return response;
        }

        String idSubRole = subRoleUser.get("idSubRole").toString();

        

        //validate if idSubRole exists in SubRole
        SubRole subRole = subRoleDao.getSubRole(idSubRole);

        if(subRole == null){
            response.put("response", "El subRol no existe");
            return response;
        }

        //getUserFarmIdByEmployee and farmId
        String userFarmId = userFarmDao.getUserFarmIdByEmployee(idEmployee, idFarm);

        if(userFarmId == null){
            response.put("response", "El usuario no pertenece a la finca como empleado");
            return response;
        }

        //addSubRoleUser
        JSONObject subRoleUserResponse = subRoleUserDao.addSubRoleUser(userFarmId, idSubRole);

        return subRoleUserResponse;
    }

    @CrossOrigin
    @RequestMapping(value = "api/findfarms/{idUser}/{nameRole}/{search}/{page}", method = RequestMethod.GET)
    public JSONObject findFarms(@PathVariable String idUser, @PathVariable String nameRole, @PathVariable String search, @PathVariable String page){
        JSONObject response = new JSONObject();
        response.put("error", true);        
        UserFarmDaoImplement findFarm = new UserFarmDaoImplement();
        if(idUser == null || idUser.equals("")){
            response.put("response", "Falta el campo idUser");
            return response;
        }
        if(nameRole == null || nameRole.equals("")){
            response.put("response", "Falta el campo rolName");
            return response;
        }
        Role role = roleDao.getRoleByName(nameRole);
        if(role == null){
            response.put("response", "El rol no existe");
            return response;
        }
        String idRole = role.getIdRole();
        if(search == null || search.equals("")){
            response.put("response", "Falta el campo de busqueda");
            return response;
        }
        if(page == null || search.equals("")){
            response.put("response", "Falta el campo de page");
            return response;
        }

        List<UserFarm> userFarms = userFarmDao.getUserFarms(idUser, idRole);

        if(userFarms == null || userFarms.isEmpty()){
            response.put("error", false);
            response.put("response", userFarms);
            response.put("maxPage", null);
            return response;
        }
        //create list from userFarms of id_farm
        String listIdFarms = "";
        
        for(int i = 0; i < userFarms.size(); i++){
            listIdFarms += "'" + userFarms.get(i).getIdFarm() + "'";
            if(i != userFarms.size() - 1){
                listIdFarms += ",";
            }
        }
        response.put("error", false);
        response.put("response", farmDao.findFarms(listIdFarms, search, page));
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/listfarms/{idUser}", method = RequestMethod.GET)
    public JSONObject listFarms(@PathVariable String idUser){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate idUser exists
        User user = userDao.getUser(idUser);

        if(user == null){
            response.put("response", "El usuario no existe");
            return response;
        }

        //getUserFarms as admin
        List<String> userFarms = userFarmDao.listUserFarms(idUser);

        List<Farm> farms = new ArrayList<Farm>();

        if(userFarms == null || userFarms.isEmpty()){
            response.put("response", farms);
            return response;
        }

        //idFarms to String

        String listIdFarms = "";
        for(String userFarm : userFarms){
            listIdFarms += "'" + userFarm + "'";
            if(userFarms.indexOf(userFarm) != userFarms.size() - 1){
                listIdFarms += ",";
            }
        }

        //getFarms by id_farm
        farms = farmDao.listFarms(listIdFarms);

        response.put("error", false);
        response.put("response", farms);

        return response;
    }
}
