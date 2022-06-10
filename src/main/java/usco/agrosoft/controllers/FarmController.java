package usco.agrosoft.controllers;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usco.agrosoft.dao.FarmDao;
import usco.agrosoft.models.Farm;
@RestController
public class FarmController {

    @Autowired
    private FarmDao FarmDao;

    @CrossOrigin
    @RequestMapping(value = "api/getfarm/{idFarm}", method = RequestMethod.GET)
    public JSONObject getFarm(@PathVariable String idFarm) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        if (idFarm == null || idFarm.equals("")){
            response.put("response", "El id de granja no puede estar vacio");
            return response;
        }

        Farm Farm = FarmDao.getFarm(idFarm);

        if (Farm == null){
            response.put("response", "La granja no existe");
            return response;
        }else{
            response.put("error", false);
            response.put("response", Farm);
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/deletefarm", method = RequestMethod.PUT)
    public JSONObject eliminateFarm(@RequestBody Farm farm) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        String idFarm = farm.getIdFarm();

        if (idFarm == null || idFarm.equals("")) {
            response.put("response", "El id de granja no puede estar vacio");
            return response;
        } else {
            String farmDaoResponse = FarmDao.deleteFarm(idFarm);
            if (farmDaoResponse.equals("0")) {
                response.put("response", "No se ha encontrado la granja");
                return response;
            } else {
                response.put("error", false);
                response.put("response", "La granja se ha eliminado correctamente");
                return response;
            }
        }
    }
    
    @CrossOrigin
    @RequestMapping(value = "api/modifyfarm", method = RequestMethod.PUT)
    public JSONObject modifyFarm(@RequestBody Farm farm) {
        String nameFarm = farm.getNameFarm();
        String descriptionFarm = farm.getDescriptionFarm();
        String idFarm = farm.getIdFarm();
        JSONObject response = new JSONObject();
        response.put("error", true);

        if(idFarm == null || idFarm.equals("")){
            response.put("response", "El id de granja no puede estar vacio");
            return response;
        }
        
        if (nameFarm == null || nameFarm.equals("")) {
            response.put("response", "El nombre de granja no puede estar vacio");
            return response;
        }
        if (descriptionFarm == null || descriptionFarm.equals("")) {
            response.put("response", "La descripcion de la granja no puede estar vacia");
            return response;
        }

        //modify farm
        JSONObject farmDaoResponse = FarmDao.modifyFarm(farm);
        
        if(farmDaoResponse.get("error").equals(true)){
            response.put("response", farmDaoResponse.get("response"));
            return response;
        }else{
            response.put("error", false);
            response.put("response", "La granja se ha modificado correctamente");
            return response;
        }
    }

}