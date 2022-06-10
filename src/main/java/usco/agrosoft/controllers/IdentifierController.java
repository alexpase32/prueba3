package usco.agrosoft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import usco.agrosoft.dao.IdentifierDao;
import usco.agrosoft.models.Identifier;

import java.util.List;

@RestController
public class IdentifierController {

    @Autowired
    private IdentifierDao identifierDao;

    @CrossOrigin
    @RequestMapping(value = "api/identifiers", method = RequestMethod.GET)
    public List<Identifier> getUsers() {
        return identifierDao.getIdentifiers();
    }

    @CrossOrigin
    @RequestMapping(value = "api/identifier/{idIdentifier}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable int idIdentifier) {
        JSONObject response = new JSONObject();
        response.put("error", "true");
        Identifier identifier = identifierDao.getIdentifier(idIdentifier);

        if (identifier == null) {
            response.put("response", "Identificador no encontrado");
            return response;
        }
        response.put("error", "false");
        response.put("response", identifier);
        return response;
    }
}