package usco.agrosoft.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import net.minidev.json.JSONObject;
import usco.agrosoft.dao.FarmDao;
import usco.agrosoft.dao.SubRoleDao;
import usco.agrosoft.models.SubRole;

@EnableEmailTools
@RestController
@RequestMapping("/api")
public class SubRoleController {

    @Autowired
    private SubRoleDao subRoleDao;

    @Autowired
    private FarmDao farmDao;

    @CrossOrigin
    @GetMapping("subroles/{idfarm}/{sorter}/{order}")
    public JSONObject getSubRoles(@PathVariable("idfarm") String idFarm, @PathVariable("sorter") String sorter, @PathVariable("order") String order) {
        JSONObject response = new JSONObject();
        //validate idFarm

        if (farmDao.getFarm(idFarm) == null) {
            response.put("error", true);
            response.put("message", "La granja no existe");
            return response;
        }

        List<SubRole> subRoles = subRoleDao.getSubRolesByIdFarm(idFarm, sorter, order);

        response.put("error", false);
        response.put("response", subRoles);
        return response;
    }

    @CrossOrigin
    @GetMapping("findsubroles/{idFarm}/{search}")
    public JSONObject findSubRoles(@PathVariable("idFarm") String idFarm, @PathVariable("search") String search) {
        JSONObject response = new JSONObject();

        //validate idFarm
        if (farmDao.getFarm(idFarm) == null) {
            response.put("error", true);
            response.put("message", "La granja no existe");
            return response;
        }

        List<SubRole> subRoles = subRoleDao.findSubRole(idFarm, search);

        response.put("error", false);
        response.put("response", subRoles);
        return response;
    }
    

    @CrossOrigin
    @PostMapping("addsubrole")
    public JSONObject addSubRole(@RequestBody SubRole subRole) {

        JSONObject response = subRoleDao.addSubRole(subRole);

        return response;
    }

    @CrossOrigin
    @PutMapping("updatesubrole")
    public JSONObject updateSubRole(@RequestBody SubRole subRole) {

        JSONObject response = new JSONObject();
        response.put("error", true);
      
        // validacion de que los campos existan
        if (subRole.getIdSubRole() == null || subRole.getIdSubRole().equals("")) {
            response.put("response", "falta el campo idSubRole");
            return response;
        }
        if (subRole.getNameSubRole() == null || subRole.getNameSubRole().equals("")) {
            response.put("response", "falta el campo nameSubRole");
            return response;
        }
        if (subRole.getDescriptionSubRole() == null || subRole.getDescriptionSubRole().equals("")) {
            response.put("response", "falta el campo descriptionSubRole");
            return response;
        }

        // validate nameSubRole min 1 max 20
        if (subRole.getNameSubRole().length() < 1 || subRole.getNameSubRole().length() > 20) {
            response.put("response", "El nombre del subRole debe tener entre 1 y 20 caracteres");
            return response;
        }

        // validate descriptionSubRole min 15 max 150
        if (subRole.getDescriptionSubRole().length() < 15 || subRole.getDescriptionSubRole().length() > 150) {
            response.put("response", "La descripción del subRole debe tener entre 15 y 150 caracteres");
            return response;
        }

        JSONObject responseSubRole = subRoleDao.updateSubRoleByIdSubRole(subRole);

        return responseSubRole;
    }


    @CrossOrigin
    @PutMapping("removesubrole/{idsubrole}")
    public JSONObject deleteSubRole(@PathVariable("idsubrole") String idsubrole) {

        JSONObject response = subRoleDao.deleteSubRoleByIdSubRole(idsubrole);

        return response;
    }

}
