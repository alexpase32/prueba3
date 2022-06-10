package usco.agrosoft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import usco.agrosoft.dao.RoleDao;
import usco.agrosoft.models.Role;

@RestController
public class RoleController {

    @Autowired
    private RoleDao roleDao;

    @CrossOrigin
    @RequestMapping(value = "api/role/{nameRole}", method = RequestMethod.GET)
    public JSONObject getRoleByName(@PathVariable String nameRole) {
        JSONObject response = new JSONObject();
        response.put("error", true);
        
        if(nameRole == null || nameRole.equals("")){
            response.put("message", "El nombre del rol es necesario");
            return response;
        }
        Role role = roleDao.getRoleByName(nameRole);
        if(role == null){
            response.put("message", "El rol no existe");
            return response;
        }
        response.put("error", false);
        response.put("response", role);
        return response;
    }
    
}
