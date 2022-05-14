package usco.agrosoft.controllers;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import usco.agrosoft.dao.UserDao;
import usco.agrosoft.models.User;
import usco.agrosoft.utils.TestService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@EnableEmailTools
@RestController
@Configuration
public class UserController {
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "api/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Autowired
    private TestService testService;

    @CrossOrigin
    @RequestMapping(value = "api/register", method = RequestMethod.POST)
    public JSONObject registerUser(@RequestBody User user) throws UnsupportedEncodingException {

        // fields validated succesfully
        if (user.getName() == null || user.getName() == "") {
            JSONObject objN = new JSONObject();
            objN.put("error", true);
            objN.put("response", "No se ha podido registrar, el nombre es requerido");
            return objN;
        }
        String nameRegex = "^[a-zA-Z ]+$";
        Pattern patternName = Pattern.compile(nameRegex);
        Matcher matcherName = patternName.matcher(user.getName());
        if (matcherName.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "No se ha podido registrar, Ingrese un nombre valido");
            return objEm;
        }

        if (user.getLastName() == null || user.getLastName() == "") {
            JSONObject objL = new JSONObject();
            objL.put("error", true);
            objL.put("response", "No se ha podido registrar, el apellido es requerido");
            return objL;
        }

        Matcher matcherLastName = patternName.matcher(user.getLastName());
        if (matcherLastName.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "No se ha podido registrar, Ingrese un apellido valido");
            return objEm;
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber() == "" || user.getPhoneNumber().length() != 10) {
            JSONObject objP = new JSONObject();
            objP.put("error", true);
            objP.put("response", "No se ha podido registrar, el telefono debe tener 10 caracteres y es requerido");
            return objP;
        }

        String phoneRegex = "^([0-9])*$";
        Pattern patternPhone = Pattern.compile(phoneRegex);
        Matcher matcherPhone = patternPhone.matcher(user.getPhoneNumber());
        if (matcherPhone.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "No se ha podido registrar, Ingrese un telefono valido valido");
            return objEm;
        }

        if (user.getPassword() == null || user.getPassword() == "") {
            JSONObject objPa = new JSONObject();
            objPa.put("error", true);
            objPa.put("response", "No se ha podido registrar, la contraseña es requerida");
            return objPa;
        }
        //^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$

        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!.@$%^&*-]).{8,}$";
        Pattern patternPassword = Pattern.compile(passwordRegex);
        Matcher matcherPassword = patternPassword.matcher(user.getPassword());
        if (matcherPassword.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "La contraseña debe tener 8 caracteres, minimo un numero, una mayuscula, una minuscula y un simbolo");
            return objEm;
        }



        if (user.getEmail() == null || user.getEmail() == "") {
            JSONObject objE = new JSONObject();
            objE.put("error", true);
            objE.put("response", "No se ha podido registrar, el email es requerido");
            return objE;
        }
        if (user.getIdIdentifier() == 0){
            JSONObject objE = new JSONObject();
            objE.put("error", true);
            objE.put("response", "No se ha podido registrar, el identificador es requerido");
            return objE;
        }
        if (user.getIdIdentifier() < 2 || user.getIdIdentifier() > 247){
            JSONObject objE = new JSONObject();
            objE.put("error", true);
            objE.put("response", "No se ha podido registrar, digite un identificador valido");
            return objE;
        }

        String emailRegex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (matcher.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "No se ha podido registrar, email no valido");
            return objEm;
        }

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setIdUser(UUID.randomUUID().toString());
        user.setPassword(hash);
        user.setTokenUser(UUID.randomUUID().toString());
        user.setEnrollmentDate(LocalDateTime.now());
        user.setActiveUser(true);
        String saved = userDao.register(user);
        if(saved.equals("0")){
            JSONObject objRe = new JSONObject();
            objRe.put("error", true);
            objRe.put("response", "No se ha podido registrar, el usuario ya existe");
            return objRe;
        }else{
            JSONObject objRe = new JSONObject();
            objRe.put("error", false);
            objRe.put("response", "Se ha registrado correctamente");
            return objRe;
        }
    }
    @CrossOrigin
    @RequestMapping(value = "activate/{token}", method = RequestMethod.GET)
    public String activateAccount(@PathVariable String token) {
        if(token == null || token == ""){
            return "Debe enviar un token valido";
        }else{
            String result = userDao.activate(token);
            if(result.equals("0")){
                return "Token invalido o ha caducado, por favor solicitelo nuevamente";
            }else{
                return result;
            }
        }
    }
    
    @CrossOrigin
    @RequestMapping(value = "api/changepassword", method = RequestMethod.POST)
    public JSONObject changePassword(@RequestBody JSONObject data) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String password = (String) data.get("password1");
        String password2 = (String) data.get("password2");
        String token = (String) data.get("tokenUser");
        if(password == null || password == ""){
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "Falta el campo password1");
            return objEm;
        }
        if(password2 == null || password2 == ""){
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "Falta el campo password2");
            return objEm;
        }
        if(token == null || token == ""){
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "Error, envie un token valido");
            return objEm;
        }
        if(password.equals(password2)){
            String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!.@$%^&*-]).{8,}$";
            Pattern patternPassword = Pattern.compile(passwordRegex);
            Matcher matcherPassword = patternPassword.matcher(password);
            if (matcherPassword.find() != true) {
                JSONObject objEm = new JSONObject();
                objEm.put("error", true);
                objEm.put("response", "La contraseña debe tener 8 caracteres, minimo un numero, una mayuscula, una minuscula y un simbolo");
                return objEm;
            }else{
                //data.get("tokenUser")
                if(data.get("tokenUser")==null || data.get("tokenUser") == ""){
                    JSONObject objEm = new JSONObject();
                    objEm.put("error", true);
                    objEm.put("response", "Debe enviar un token valido");
                    return objEm;
                }else{
                    String hash = argon2.hash(1, 1024, 1, password);
                    data.put("hash", hash);
                    String result = userDao.changePassword(data);
                    if(result.equals("0")){
                        JSONObject obj0 = new JSONObject();
                        obj0.put("error", false);
                        obj0.put("response", "Su contraseña se ha restaurado con exito, Redireccionando...");
                        return obj0;
                    }else{
                        JSONObject obj1 = new JSONObject();
                        obj1.put("error", true);
                        obj1.put("response", "Token invalido o ha caducado, por favor solicitelo nuevamente");
                        return obj1;
                    }
                }
            }
        }else{
            JSONObject obj1 = new JSONObject();
            obj1.put("error", true);
            obj1.put("response", "Las contraseñas deben coincidir");
            return obj1;
        }
    }

    @CrossOrigin
    @RequestMapping(value ="api/generate", method = RequestMethod.POST)
    public JSONObject generate(@RequestBody JSONObject data) throws UnsupportedEncodingException{
        //validamos si el email es correcto
        String emailRegex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher((CharSequence) data.get("email"));
        if (matcher.find() != true) {
            JSONObject objEm = new JSONObject();
            objEm.put("error", true);
            objEm.put("response", "El email no es valido");
            return objEm;
        }else{
            String result = userDao.generateToken(data);
            if(result.equals("0")){
                JSONObject obj0 = new JSONObject();
                obj0.put("error", true);
                obj0.put("response", "El email no se encuentra registrado");
                return obj0;
            }else if(result.equals("1")){
                JSONObject obj1 = new JSONObject();
                obj1.put("error", true);
                obj1.put("response", "La cuenta no se encuentra activa, por favor revise su email y activela");
                return obj1;
            }else{
                JSONObject obj2 = new JSONObject();
                obj2.put("error", false);
                obj2.put("response", "Revise su email para cambiar la contraseña");
                obj2.put("userToken", result);
                return obj2;
            }
        }
    }
}
