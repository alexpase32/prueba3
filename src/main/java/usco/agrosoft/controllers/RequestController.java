package usco.agrosoft.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import usco.agrosoft.dao.FarmDao;
import usco.agrosoft.dao.RequestDao;
import usco.agrosoft.dao.UserDao;
import usco.agrosoft.dao.UserFarmDao;
import usco.agrosoft.models.Farm;
import usco.agrosoft.models.Request;
import usco.agrosoft.models.ShowRequest;
import usco.agrosoft.models.User;

@RestController
public class RequestController {

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FarmDao farmDao;

    @Autowired
    private UserFarmDao userFarmDao;

    @CrossOrigin
    @RequestMapping(value = "api/createrequest", method = RequestMethod.POST)
    public JSONObject createRequest(@RequestBody JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate the required fields
        if(data.get("idUserEmmiter") == null || data.get("idUserEmmiter").equals("")){
            response.put("response", "El id del usuario emisor es requerido");
            return response;
        }

        if(data.get("emailUserReceiver") == null || data.get("emailUserReceiver").equals("")) {
            response.put("response", "El email del usuario receptor es requerido");
            return response;
        }

        if(data.get("idFarm") == null || data.get("idFarm").equals("")) {
            response.put("response", "El id de la granja es requerido");
            return response;
        }

        if(data.get("idRole") == null || data.get("idRole").equals("")) {
            response.put("response", "El id del rol es requerido");
            return response;
        }

        //create variables to save the data
        //create the request_id
        String idRequest = UUID.randomUUID().toString();
        String idUserEmmiter = data.get("idUserEmmiter").toString();

        //validate id idUserEmmiter exists
        boolean idUserRes = userDao.verifIdUser(idUserEmmiter);

        if(!idUserRes) {
            response.put("response", "El usuario emisor no está registrado");
            return response;
        }

        //get the idUserReceiver from the email
        String emailUserReceiver = data.get("emailUserReceiver").toString();
        JSONObject userDaoResponse = userDao.getIdUserByEmail(emailUserReceiver);

        if(userDaoResponse.get("error").equals(true)) {
            response.put("response", userDaoResponse.get("response"));
            return response;
        }

        String idUserReceiver = userDaoResponse.get("response").toString();
        
        String idFarm = data.get("idFarm").toString();

        //validate if idFarm exists

        boolean idFarmRes = farmDao.verifIdFarm(idFarm);

        if(!idFarmRes) {
            response.put("response", "La granja no existe");
            return response;
        }

        String idRole = data.get("idRole").toString();

        //create the request
        Request request = new Request();
        request.setIdRequest(idRequest);
        request.setIdUserEmmiter(idUserEmmiter);
        request.setIdFarm(idFarm);
        request.setIdUserReceiver(idUserReceiver);
        request.setIdState("1");
        request.setIdRole(idRole);
        request.setAdmin(true);
        request.setActiveRequest(true);
        request.setCreatedDate(LocalDateTime.now());
        
        //save the request
        JSONObject requestDaoResponse = requestDao.createRequest(request);

        if(requestDaoResponse.get("error").equals(true)) {
            response.put("response", requestDaoResponse.get("response"));
            return response;
        }
        
        return requestDaoResponse;
    }

    /*@CrossOrigin
    @RequestMapping(value = "api/acceptrequest/{idRequest}", method = RequestMethod.GET)
    public JSONObject acceptRequest(@PathVariable String idRequest){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //get request by id
        Request request = requestDao.getRequestById(idRequest);
        
        if(request == null){
            response.put("response", "El id de solicitud es invalido");
            return response;
        }

        //get adminId by farmId
        String adminId = userFarmDao.getAdminId(request.getIdFarm());
        

        if(adminId == null){
            response.put("response", "No se ha podido aceptar la solicitud");
            return response;
        }

        //if the request is for employees
        if(request.getIdRole().equals("2")){
            if(adminId.equals(request.getIdUserEmmiter())){
                
                //if the request is from an admin to an user to be an employee
                JSONObject userFarmDaoRespone = userFarmDao.addEmployee(request.getIdUserReceiver(), request.getIdFarm());

                if(userFarmDaoRespone.get("error").equals(true)){
                    response.put("response", userFarmDaoRespone.get("response"));
                    requestDao.cancelRequest(request);
                    return response;
                }
                //change state of the request
                boolean acceptResponse = requestDao.acceptRequest(request);
                if(acceptResponse){
                    response.put("error", false);
                    response.put("response", "Solicitud aceptada");
                    return response;
                }else{
                    response.put("response", "No se ha podido aceptar la solicitud");
                    return response;
                }
            }else{
                //if the request if from an user to an admin to be an employee in your farm
                JSONObject userFarmDaoRespone = userFarmDao.addEmployee(request.getIdUserEmmiter(), request.getIdFarm());
                if((boolean) userFarmDaoRespone.get("error")){
                    response.put("response", userFarmDaoRespone.get("response"));
                    //detele request
                    return response;
                }
                //change state of the request
                boolean acceptResponse = requestDao.acceptRequest(request);
                if(acceptResponse){
                    response.put("error", false);
                    response.put("response", "Solicitud aceptada");
                    return response;
                }else{
                    response.put("response", "No se ha podido aceptar la solicitud");
                    return response;
                }
            }
        }else{
            if(adminId.equals(request.getIdUserEmmiter())){
                //if the request is from an admin to an user to be an admin
                //change the admin of the farm
                JSONObject userFarmDaoRespone = userFarmDao.changeAdmin(request.getIdUserEmmiter(), request.getIdUserReceiver(), request.getIdFarm());
                if((boolean) userFarmDaoRespone.get("error")){
                    response.put("response", userFarmDaoRespone.get("response"));
                    return response;
                }
                //set oldAdmin to employee of the farm
                userFarmDao.addEmployee(request.getIdUserEmmiter(), request.getIdFarm());

                //removeEmployee from the farm
                userFarmDao.deleteEmployee(request.getIdUserReceiver(), request.getIdFarm());

                //change state of the request
                boolean acceptResponse = requestDao.acceptRequest(request);

                if(acceptResponse){
                    response.put("error", false);
                    response.put("response", "Solicitud aceptada");
                    return response;
                }else{
                    response.put("response", "No se ha podido aceptar la solicitud");
                    return response;
                }
            }else{
                JSONObject userFarmDaoRespone = userFarmDao.changeAdmin(request.getIdUserReceiver(), request.getIdUserEmmiter(), request.getIdFarm());
                if((boolean) userFarmDaoRespone.get("error")){
                    response.put("response", userFarmDaoRespone.get("response"));
                    return response;
                }
                //set oldAdmin to employee of the farm
                userFarmDao.addEmployee(request.getIdUserReceiver(), request.getIdFarm());

                //removeEmployee from the farm
                userFarmDao.deleteEmployee(request.getIdUserEmmiter(), request.getIdFarm());

                //change state of the request
                boolean acceptResponse = requestDao.acceptRequest(request);
                
                if(acceptResponse){
                    response.put("error", false);
                    response.put("response", "Solicitud aceptada");
                    return response;
                }else{
                    response.put("response", "No se ha podido aceptar la solicitud");
                    return response;
                }
            }
        }
    }*/

    @CrossOrigin
    @RequestMapping(value = "api/acceptrequest/{idRequest}", method = RequestMethod.GET)
    public JSONObject acceptRequest(@PathVariable String idRequest){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //get request by id
        Request request = requestDao.getRequestById(idRequest);
        
        if(request == null){
            response.put("response", "El id de solicitud es invalido");
            return response;
        }
        //if the request is for employees
        if(request.getIdRole().equals("2")){
            //if the request is from an admin to an user to be an employee
            JSONObject userFarmDaoRespone = userFarmDao.addEmployee(request.getIdUserReceiver(), request.getIdFarm());

            if(userFarmDaoRespone.get("error").equals(true)){
                response.put("response", userFarmDaoRespone.get("response"));
                requestDao.cancelRequest(request);
                return response;
            }
            //change state of the request
            boolean acceptResponse = requestDao.acceptRequest(request);
            if(acceptResponse){
                response.put("error", false);
                response.put("response", "Solicitud aceptada");
                return response;
            }else{
                response.put("response", "No se ha podido aceptar la solicitud");
                return response;
            }
        }else{
            //if he request is from an admin to an user to be an admin
            //change the admin of the farm
            JSONObject userFarmDaoRespone = userFarmDao.changeAdmin(request.getIdUserEmmiter(), request.getIdUserReceiver(), request.getIdFarm());

            if((boolean) userFarmDaoRespone.get("error")){
                response.put("response", userFarmDaoRespone.get("response"));
                return response;
            }

            //set oldAdmin to employee of the farm
            userFarmDao.addEmployee(request.getIdUserEmmiter(), request.getIdFarm());

            //removeEmployee from the farm
            userFarmDao.deleteEmployee(request.getIdUserReceiver(), request.getIdFarm());

            //change state of the request
            boolean acceptResponse = requestDao.acceptRequest(request);

            if(acceptResponse){
                response.put("error", false);
                response.put("response", "Solicitud aceptada");
                return response;
            }else{
                response.put("response", "No se ha podido aceptar la solicitud");
                return response;
            }
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/rejectrequest/{idRequest}", method = RequestMethod.GET)
    public JSONObject rejectRequest(@PathVariable String idRequest){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //get request by id
        Request request = requestDao.getRequestById(idRequest);

        if(request == null){
            response.put("response", "El id de solicitud es invalido");
            return response;
        }

        //change state of the request
        boolean rejectResponse = requestDao.rejectRequest(request);

        if(rejectResponse){
            response.put("error", false);
            response.put("response", "Solicitud rechazada");
            return response;
        }else{
            response.put("response", "No se ha podido rechazar la solicitud");
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/cancelrequest/{idRequest}", method = RequestMethod.GET)
    public JSONObject cancelRequest(@PathVariable String idRequest){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //get request by id
        Request request = requestDao.getRequestById(idRequest);

        if(request == null){
            response.put("response", "El id de solicitud es invalido");
            return response;
        }

        //change state of the request
        boolean cancelResponse = requestDao.cancelRequest(request);

        if(cancelResponse){
            response.put("error", false);
            response.put("response", "Solicitud cancelada");
            return response;
        }else{
            response.put("response", "No se ha podido cancelar la solicitud");
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/getinrequests/{stateReq}/{idUser}/{page}", method = RequestMethod.GET)
    public JSONObject getInRequests1(@PathVariable String stateReq,@PathVariable String idUser, @PathVariable String page){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if idUser exists in User
        User user = userDao.getUser(idUser);
        if(user == null){
            response.put("response", "El id de usuario es invalido");
            return response;
        }

        Long maxPage = requestDao.getRequestInCount(idUser, stateReq) / 10;

        //get requests by idUser
        List<Request> requests = requestDao.getInRequests(idUser, page, stateReq);

        if(requests == null){
            response.put("response", "No se han encontrado solicitudes");
            return response;
        }

        //create a list of show requests for each request
        List<ShowRequest> showRequests = new ArrayList<>();
        
        for(Request request : requests){
            ShowRequest showRequest = new ShowRequest();

            showRequest.setIdRequest(request.getIdRequest());

            String state = "";
            switch(Integer.parseInt(request.getIdState())){
                case 1:
                    state = "Pendiente";
                    break;
                case 2:
                    state = "Aceptada";
                    break;
                case 3:
                    state = "Rechazada";
                    break;
            }

            showRequest.setStateRequest(state);

            String typeRequest = "";
            switch(Integer.parseInt(request.getIdRole())){
                case 1:
                    typeRequest = "Administrador";
                    break;
                case 2:
                    typeRequest = "Empleado";
                    break;
            }
            showRequest.setTypeRequest(typeRequest);

            User userA = userDao.getUser(request.getIdUserEmmiter());

            showRequest.setName(userA.getName() + " " + userA.getLastName());

            showRequest.setEmail(userA.getEmail());

            Farm farm = farmDao.getFarm(request.getIdFarm());

            showRequest.setIdFarm(request.getIdFarm());

            showRequest.setNameFarm(farm.getNameFarm());

            showRequest.setDescriptionFarm(farm.getDescriptionFarm());

            showRequest.setCreatedDate(request.getCreatedDate());

            showRequests.add(showRequest);
        }

        response.put("error", false);
        response.put("response", showRequests);
        response.put("maxPage", maxPage);
        return response;
    }

    //duplicate of getInRequests for getOutRequests
    @CrossOrigin
    @RequestMapping(value = "api/getoutrequests/{stateReq}/{idUser}/{page}", method = RequestMethod.GET)
    public JSONObject getOutRequests(@PathVariable String stateReq, @PathVariable String idUser, @PathVariable String page){
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if idUser exists in User
        User user = userDao.getUser(idUser);
        if(user == null){
            response.put("response", "El id de usuario es invalido");
            return response;
        }

        Long maxPage = requestDao.getRequestOutCount(idUser, stateReq) / 10;

        //get requests by idUser
        List<Request> requests = requestDao.getOutRequests(idUser, page, stateReq);

        if(requests == null){
            response.put("response", "No se han encontrado solicitudes");
            return response;
        }

        //create a list of show requests for each request
        List<ShowRequest> showRequests = new ArrayList<>();
        for(Request request : requests){
            ShowRequest showRequest = new ShowRequest();

            showRequest.setIdRequest(request.getIdRequest());

            String state = "";
            switch(Integer.parseInt(request.getIdState())){
                case 1:
                    state = "Pendiente";
                    break;
                case 2:
                    state = "Aceptada";
                    break;
                case 3:
                    state = "Rechazada";
                    break;
            }
            showRequest.setStateRequest(state);

            String typeRequest = "";
            switch(Integer.parseInt(request.getIdRole())){
                case 1:
                    typeRequest = "Administrador";
                    break;
                case 2:
                    typeRequest = "Empleado";
                    break;
            }
            showRequest.setTypeRequest(typeRequest);

            User userA = userDao.getUser(request.getIdUserReceiver());

            showRequest.setName(userA.getName() + " " + userA.getLastName());

            showRequest.setEmail(userA.getEmail());

            Farm farm = farmDao.getFarm(request.getIdFarm());

            showRequest.setIdFarm(request.getIdFarm());

            showRequest.setNameFarm(farm.getNameFarm());

            showRequest.setDescriptionFarm(farm.getDescriptionFarm());

            showRequest.setCreatedDate(request.getCreatedDate());

            showRequests.add(showRequest);
        }

        response.put("error", false);
        response.put("response", showRequests);
        response.put("maxPage", maxPage);
        return response;
    }
}
