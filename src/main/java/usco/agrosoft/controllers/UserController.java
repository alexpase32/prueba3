package usco.agrosoft.controllers;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import net.minidev.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import usco.agrosoft.dao.FarmDao;
import usco.agrosoft.dao.IdentifierDao;
import usco.agrosoft.dao.UserDao;
import usco.agrosoft.dao.UserFarmDao;
import usco.agrosoft.models.Identifier;
import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;
import usco.agrosoft.utils.TestService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableEmailTools
@RestController
@Configuration
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdentifierDao identifierDao;

    String passwordError = "La contraseña debe tener 8 caracteres, minimo un numero, una mayuscula, una minuscula y un simbolo";

    @RequestMapping(value = "api/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Autowired
    private TestService testService;

    @Autowired
    private UserFarmDao userFarmDao;

    @Autowired
    private FarmDao farmDao;

    @CrossOrigin
    @RequestMapping(value = "api/user/{idUser}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable String idUser) throws ParseException, JsonProcessingException {
        JSONObject response = new JSONObject();
        response.put("error", true);
        if (idUser == null || idUser.equals("")) {
            response.put("response", "Debe enviar un ID");
            return response;
        }

        User user = userDao.getUser(idUser);

        if (user == null) {
            response.put("response", "El usuario no existe");
            return response;
        }
        user.setPassword(null);

        response.put("error", false);
        response.put("response", user);
        return response;

    }

    @CrossOrigin
    @RequestMapping(value = "api/register", method = RequestMethod.POST)
    public JSONObject registerUser(@RequestBody User user) throws UnsupportedEncodingException {
        JSONObject response = new JSONObject();
        response.put("error", true);
        // fields validated succesfully
        if (user.getName() == null || user.getName().equals("")) {
            response.put("response", "No se ha podido registrar, el nombre es requerido");
            return response;
        }
        String nameRegex = "^[a-zA-Z ]+$";
        Pattern patternName = Pattern.compile(nameRegex);
        Matcher matcherName = patternName.matcher(user.getName());
        if (matcherName.find() != true) {
            response.put("response", "No se ha podido registrar, Ingrese un nombre valido");
            return response;
        }

        if (user.getLastName() == null || user.getLastName().equals("")) {
            response.put("response", "No se ha podido registrar, el apellido es requerido");
            return response;
        }

        Matcher matcherLastName = patternName.matcher(user.getLastName());
        if (matcherLastName.find() != true) {
            response.put("response", "No se ha podido registrar, Ingrese un apellido valido");
            return response;
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().equals("") || user.getPhoneNumber().length() < 9 || user.getPhoneNumber().length() > 15) {
            response.put("response", "No se ha podido registrar, el numero de telefono es requerido y debe tener entre 9 y 15 digitos");
            return response;
        }

        String phoneRegex = "^([0-9])*$";
        Pattern patternPhone = Pattern.compile(phoneRegex);
        Matcher matcherPhone = patternPhone.matcher(user.getPhoneNumber());
        if (matcherPhone.find() != true) {
            response.put("response", "No se ha podido registrar, Ingrese un numero de telefono valido");
            return response;
        }

        if (user.getPassword() == null || user.getPassword().equals("")) {
            response.put("response", "No se ha podido registrar, la contraseña es requerida");
            return response;
        }

        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!.@$%^&*-]).{8,}$";
        Pattern patternPassword = Pattern.compile(passwordRegex);
        Matcher matcherPassword = patternPassword.matcher(user.getPassword());
        if (matcherPassword.find() != true) {
            response.put("response", passwordError);
            return response;
        }

        if (user.getEmail() == null || user.getEmail().equals("")) {
            response.put("response", "No se ha podido registrar, el email es requerido");
            return response;
        }
        if (user.getIdIdentifier() == 0) {
            response.put("response", "No se ha podido registrar, el identificador es requerido");
            return response;
        }
        if (user.getIdIdentifier() < 2 || user.getIdIdentifier() > 247) {
            response.put("response", "No se ha podido registrar, el identificador es invalido");
            return response;
        }

        String emailRegex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (matcher.find() != true) {
            response.put("response", "No se ha podido registrar, Ingrese un email valido");
            return response;
        }

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setIdUser(UUID.randomUUID().toString());
        user.setPassword(hash);
        user.setTokenUser(UUID.randomUUID().toString());
        user.setEnrollmentDate(LocalDateTime.now());
        user.setActiveUser(true);
        String saved = userDao.register(user);
        if (saved.equals("0")) {
            response.put("response", "No se ha podido registrar, el usuario ya existe");
            return response;
        } else {
            response.put("error", false);
            response.put("response", "Usuario registrado con exito");
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "activate/{token}", method = RequestMethod.GET)
    public String activateAccount(@PathVariable String token) {
        if (token == null || token.equals("")) {
            return "Debe enviar un token valido";
        } else {
            String result = userDao.activate(token);
            if (result.equals("0")) {
                return "Token invalido o ha caducado, por favor solicitelo nuevamente";
            } else {
                return result;
            }
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/changepassword", method = RequestMethod.POST)
    public JSONObject changePassword(@RequestBody JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        
        if (data.get("password1") == null || data.get("password1").equals("")) {
            response.put("response", "No se ha podido cambiar la contraseña, falta el campo password1");
            return response;
        }
        if (data.get("password2") == null || data.get("password2").equals("")) {
            response.put("response", "No se ha podido cambiar la contraseña, falta el campo password2");
            return response;
        }
        if (data.get("tokenUser") == null || data.get("tokenUser").equals("")) {
            response.put("response", "No se ha podido cambiar la contraseña, falta el campo tokenUser");
            return response;
        }

        String password = (String) data.get("password1");
        String password2 = (String) data.get("password2");
        String token = (String) data.get("tokenUser");

        if (password.equals(password2)) {
            String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!.@$%^&*-]).{8,}$";
            Pattern patternPassword = Pattern.compile(passwordRegex);
            Matcher matcherPassword = patternPassword.matcher(password);
            if (matcherPassword.find() != true) {
                response.put("response", passwordError);
                return response;
            } else {
                String hash = argon2.hash(1, 1024, 1, password);
                data.put("hash", hash);
                String result = userDao.changePassword(data);
                if (result.equals("0")) {
                    response.put("error", false);
                    response.put("response", "Contraseña cambiada con exito, redireccionando...");
                    return response;
                } else {
                    response.put("response", "Token invalido o ha caducado, por favor solicitelo nuevamente");
                    return response;
                }
            }
        } else {
            response.put("response", "Las contraseñas no coinciden");
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/generate", method = RequestMethod.POST)
    public JSONObject generate(@RequestBody JSONObject data) throws UnsupportedEncodingException {
        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if email is null
        if (data.get("email") == null || data.get("email").equals("")) {
            response.put("response", "Falta el campo email");
            return response;
        }

        // validate if email is valid
        String emailRegex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher((CharSequence) data.get("email"));
        if (matcher.find() != true) {
            response.put("response", "Ingrese un email valido");
            return response;
        } else {
            String result = userDao.generateToken(data);
            if (result.equals("0")) {
                response.put("response", "El email no se encuentra registrado");
                return response;
            } else if (result.equals("1")) {
                response.put("response", "La cuenta no se encuentra activa, por favor revise su email y activela");
                return response;
            } else {
                response.put("error", false);
                response.put("response", "Revise su email para cambiar la contraseña");
                response.put("userToken", result);
                return response;
            }
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/newpassword", method = RequestMethod.PUT)
    public JSONObject newPassword(@RequestBody JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        if (data.get("oldPassword") == null || data.get("oldPassword").equals("")) {
            response.put("response", "Falta el campo oldPassword");
            return response;
        }

        if (data.get("password1") == null || data.get("password1").equals("")) {
            response.put("response", "Falta el campo password1");
            return response;
        }
        if (data.get("password2") == null || data.get("password2").equals("")) {
            response.put("response", "Falta el campo password2");
            return response;
        }
        if (data.get("idUser") == null || data.get("idUser").equals("")) {
            response.put("response", "Falta el campo idUser");
            return response;
        }

        String password1 = (String) data.get("password1");
        String password2 = (String) data.get("password2");

        if (password1.equals(password2)) {
            String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!.@$%^&*-]).{8,}$";
            Pattern patternPassword = Pattern.compile(passwordRegex);
            Matcher matcherPassword1 = patternPassword.matcher(password1);
            if (matcherPassword1.find() != true) {
                response.put("response", passwordError);
                return response;
            } else {
                String hash = argon2.hash(1, 1024, 1, password1);
                data.put("hash", hash);

                String result = userDao.newPassword(data);

                if (result.equals("0")) {
                    response.put("error", false);
                    response.put("response", "Contraseña restaurada con exito");
                    return response;
                } else if (result.equals("1")) {
                    response.put("response", "La contraseña anterior no coincide");
                    return response;
                } else if (result.equals("2")) {
                    response.put("response", "Id de usuario invalido o no existe, por favor intentelo nuevamente");
                    return response;
                } else if (result.equals("3")) {
                    response.put("response", "Error, el usuario no se encuentra activo");
                    return response;
                } else {
                    response.put("response", "Error, el usuario no está verificado");
                    return response;
                }
            }
        } else {
            response.put("response", "Las contraseñas no coinciden");
            return response;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "api/changestate", method = RequestMethod.PUT)
    public JSONObject changeState(@RequestBody User user) {
        JSONObject response = new JSONObject();
        response.put("error", true);

        String id_user = user.getIdUser();

        if (id_user == null || id_user.equals("")) {
            response.put("response", "Falta el campo idUser");
            return response;
        }
        String saved = userDao.changeState(user);
        if (saved.equals("1")) {
            response.put("response", "No se ha encontrado el usuario");
            return response;
        } else if (saved.equals("2")) {
            response.put("response", "El usuario ya se encuentra inactivo");
            return response;
        } else {
            //delete farms of user as admin
            //go to UserFarmDao and get all farms of user as admin
            List<UserFarm> userFarms = userFarmDao.getUserFarms(id_user, "1");
            for (UserFarm userFarm : userFarms) {
                //delete farm
                farmDao.deleteFarm(userFarm.getIdFarm());
            }

            //delete userFarms of user as employee
            //go to UserFarmDao and get all farms of user as employee
            List<UserFarm> userFarms2 = userFarmDao.getUserFarms(id_user, "2");
            for (UserFarm userFarm : userFarms2) {
                //delete UserFarm
                userFarmDao.deleteEmployee(id_user, userFarm.getIdFarm());
            }


            response.put("error", false);
            response.put("response", "El usuario se ha eliminado con exito");
            return response;
        } 
    }

    @CrossOrigin
    @RequestMapping(value = "api/modifyuser", method = RequestMethod.PUT)
    public JSONObject modifyuser(@RequestBody User user) {

        String idUser = user.getIdUser();
        String name = user.getName();
        String lastName = user.getLastName();
        Integer idIdentifier = user.getIdIdentifier();
        String phoneNumber = user.getPhoneNumber();

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------");
        System.out.println(idUser);
        System.out.println(idIdentifier);
        System.out.println(phoneNumber);

        JSONObject objRe = new JSONObject();
        objRe.put("error", true);

        if (idIdentifier == 0) {
            objRe.put("response", "hace falta el indicativo");
            return objRe;
        }
        if(name == null || name.equals("")){
            objRe.put("response", "hace falta el nombre");
            return objRe;
        }
        if(lastName == null || lastName.equals("")){
            objRe.put("response", "hace falta el apellido");
            return objRe;
        }

        if (idUser == null || idUser == "") {
            objRe.put("response", "hace falta el id");
            return objRe;
        }
        if (phoneNumber == null || phoneNumber == "") {
            objRe.put("response", "hace falta el numero de telefono");
            return objRe;
        }

        String regex = "^\\d+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (!matcher.find()) {
            objRe.put("response", "el telefono debe contener digitos del 0 al 9");
            return objRe;
        }

        if (phoneNumber.length() < 9 || phoneNumber.length() > 15) {
            objRe.put("response", "el telefono debe constar de 9 a 15 digitos");
            return objRe;
        }
        if(name.length() < 1 || name.length() > 20){
            objRe.put("response", "el nombre debe contener de 1 a 20 caracteres");
            return objRe;
        }
        if(lastName.length() < 1 || lastName.length() > 20){
            objRe.put("response", "el apellido debe contener de 1 a 20 caracteres");
            return objRe;
        }

        //String idIdentifierString = String.valueOf(idIdentifier);

        Identifier identifier = identifierDao.getIdentifier(idIdentifier);

        if (identifier == null) {
            objRe.put("response", "el identificador del pais es incorrecto");
            return objRe;
        }

        objRe = userDao.modifyUser(idUser, idIdentifier, phoneNumber, name, lastName);

        return objRe;
    }

    @CrossOrigin
    @RequestMapping(value = "api/comparepassword", method = RequestMethod.POST)
    public JSONObject comparePassword(@RequestBody User user) {

        String idUser = user.getIdUser();
        String password = user.getPassword();

        JSONObject objRe = new JSONObject();
        objRe.put("error", true);

        if (idUser == null || idUser == "") {
            objRe.put("response", "hace falta el id de usuario");
            return objRe;
        }
        if (password == null || password == "") {
            objRe.put("response", "hace falta la contraseña");
            return objRe;
        }

        JSONObject userDaoRespone = userDao.comparePassword(idUser, password);

        return userDaoRespone;
    }

    @CrossOrigin
    @RequestMapping(value = "api/changeemail", method = RequestMethod.POST)
    public JSONObject changeEmail(@RequestBody User user) throws UnsupportedEncodingException {

        String token = user.getTokenUser();
        String email = user.getEmail();
        String emailRegex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

        JSONObject response = new JSONObject();
        response.put("error", true);

        if (token == null || token.equals("")) {
            response.put("response", "hace falta el token");
            return response;
        }
        if (token == null || token.equals("")) {
            response.put("response", "hace falta el email");
            return response;
        }

        if (!email.matches(emailRegex) || email.length() > 60) {
            response.put("response", "el email no es valido");
            return response;
        }   

        JSONObject userResponse = userDao.getIdUserByEmail(email);
        
        if(!(boolean) userResponse.get("error")){
            response.put("response", "El email introducido ya está registrado");
            return response;
        }

        JSONObject userDaoRespone = userDao.changeEmail(token, email);

        return userDaoRespone;
    }
}
