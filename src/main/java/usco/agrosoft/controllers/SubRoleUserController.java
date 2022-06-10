package usco.agrosoft.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import net.minidev.json.JSONObject;
import usco.agrosoft.dao.SubRoleUserDao;
import usco.agrosoft.models.SubRole;
import usco.agrosoft.models.SubRoleUser;

@EnableEmailTools
@RestController
@RequestMapping("/api")
public class SubRoleUserController {

    @Autowired
    private SubRoleUserDao subRoleUserDao;

    @CrossOrigin
    @RequestMapping(value = "deletesubroleuser", method = RequestMethod.PUT)
    public JSONObject deleteSubRoleUser(@RequestBody SubRoleUser subRoleUser){
        JSONObject response = new JSONObject();
        response.put("error", true);

        String idSubRoleUser = subRoleUser.getIdSubRoleUser();

        //validate if idSubRoleUser is not null or empty
        if(idSubRoleUser == null || idSubRoleUser.equals("")){
            response.put("response", "El campo idSubRoleUser no puede ser vacio");
            return response;
        }

        JSONObject responseDelete = subRoleUserDao.deleteSubRoleUser(idSubRoleUser);

        return responseDelete;
    }

    /*@GetMapping("")
    public List<SubRoleUser> getSubRoles(@RequestBody String idFarm) {

        List<SubRoleUser> subRoles = subRoleUserDao.getSubRoles(idFarm);

        return subRoles;
    }

    @PostMapping("")
    public JSONObject addSubRole() {

        JSONObject response = subRoleUserDao.addSubRole();

        return response;
    }

    @PutMapping("")
    public JSONObject changeSubRole(@RequestBody String idSubRole) {

        JSONObject response = subRoleUserDao.changeSubRole(idSubRole);

        return response;
    }

    @DeleteMapping("")
    public JSONObject deleteSubRole(@RequestBody String idSubRole) {

        JSONObject response = subRoleUserDao.deleteSubRole(idSubRole);

        return response;
    }*/

    

}
