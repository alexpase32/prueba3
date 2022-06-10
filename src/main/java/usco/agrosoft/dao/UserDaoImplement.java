package usco.agrosoft.dao;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;

import org.springframework.stereotype.Repository;
import usco.agrosoft.utils.TestService;

import java.io.UnsupportedEncodingException;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;

@EnableEmailTools
@Repository
@Transactional
public class UserDaoImplement implements UserDao {
    String activeLink = "https://agrosoft.herokuapp.com/activate/";
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public List<User> getUsers() {
        String query = "FROM User u";
        List<User> result = entityManager.createQuery(query).getResultList();
        return result;
    }

    @Autowired
    private TestService testService;

    @Override
    @Transactional
    public User getUser(String idUser) {
        String query = "FROM User WHERE id_user = :idUser AND is_active_user = :isActiveUser";

        try {
            User result = (User) entityManager.createQuery(query)
                    .setParameter("idUser", idUser)
                    .setParameter("isActiveUser", true)
                    .getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public JSONObject modifyUser(String idUser, int idIdentifier, String phoneNumber, String name, String lastName) {

        JSONObject objRe = new JSONObject();

        String query = "FROM User WHERE id_user = :idUser AND is_active_user = :isActiveUser";

        try {
            User user = (User) entityManager.createQuery(query)
                    .setParameter("idUser", idUser)
                    .setParameter("isActiveUser", true)
                    .getSingleResult();
            System.out.println(user);

            user.setPhoneNumber(phoneNumber);
            user.setIdIdentifier(idIdentifier);
            user.setName(name);
            user.setLastName(lastName);
            entityManager.merge(user);
            entityManager.close();

            objRe.put("error", false);
            objRe.put("response", "El usuario fue modificado correctamente");

            return objRe;

        } catch (Exception e) {
            objRe.put("error", true);
            objRe.put("response", "el usuario no existe");
            return objRe;

        }

    }

    @Override
    public String register(User user) throws UnsupportedEncodingException {
        String query = "FROM User WHERE email = :email";
        List<User> list = entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .getResultList();
        if (list.isEmpty()) {
            entityManager.merge(user);
            String body = "Activa tu cuenta dando click aqui: " + activeLink + user.getTokenUser()
                    + "\n o si estas en la aplicacion copia y pega el siguiente token " + user.getTokenUser();
            testService.sendEmail(user.getEmail(), user.getName() + user.getLastName(), "Activa tu cuenta Agrosoft",
                    body);
            return "1";
        }
        return "0";
    }

    @Override
    public User login(User user) throws UnsupportedEncodingException {
        String query = "FROM User WHERE email = :email AND is_active_user = :isActiveUser";
        List<User> list = entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .setParameter("isActiveUser", true)
                .getResultList();
        if (list.isEmpty()) {
            return null;
        }
        if (!list.get(0).isVerficate()) {
            if (list.get(0).getTokenUser() == null || list.get(0).getTokenUser().equals("")) {
                // generar token y reenviar email.
                String token = UUID.randomUUID().toString();
                User userFound = list.get(0);
                userFound.setTokenUser(token);
                entityManager.merge(userFound);
                String body = "Activa tu cuenta dando click aqui: " + activeLink + token;
                testService.sendEmail(user.getEmail(), user.getName() + user.getLastName(), "Activa tu cuenta Agrosoft",
                        body);
            } else {
                // reenviar email con el token
                String body = "Activa tu cuenta dando click aqui: " + activeLink + list.get(0).getTokenUser();
                testService.sendEmail(user.getEmail(), user.getName() + user.getLastName(), "Activa tu cuenta Agrosoft",
                        body);
            }

        }
        String passwordHashed = list.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (argon2.verify(passwordHashed, user.getPassword())) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public String activate(String token) {

        String query = "FROM User WHERE token_user = :token";

        List<User> users = entityManager.createQuery(query).setParameter("token", token)
                .getResultList();

        if (users.isEmpty()) {
            return "0";
        }

        User userFound = users.get(0);

        userFound.setVerficate(true);
        userFound.setTokenUser("");
        entityManager.merge(userFound);

        return "Cuenta activada exitosamente!";
    }

    @Override
    public String generateToken(JSONObject data) throws UnsupportedEncodingException {
        // 2. validate if email exist
        String query = "FROM User WHERE email = :email";
        List<User> list = entityManager.createQuery(query)
                .setParameter("email", data.get("email"))
                .getResultList();
        if (!list.isEmpty()) {
            // 3. validate if account is activated
            if (list.get(0).isVerficate()) {
                // The account is activated, create token and send email
                User userFound = list.get(0);
                if (list.get(0).getTokenUser() == null || list.get(0).getTokenUser().equals("")) {
                    // generar token y reenviar email.
                    String token = UUID.randomUUID().toString();
                    userFound.setTokenUser(token);
                    entityManager.merge(userFound);
                    String body = "Actualiza tu contraseña dando click aqui: " + data.get("link") + token
                            + "\n o si estas en la aplicacion copia y pega el siguiente token: " + token;
                    testService.sendEmail(userFound.getEmail(), userFound.getName() + userFound.getLastName(),
                            "Cambio de contraseña Agrosoft", body);
                } else {
                    // reenviar email con el token
                    String body = "Actualiza tu contraseña dando click aqui: " + data.get("link")
                            + userFound.getTokenUser()
                            + "\n o si estas en la aplicacion copia y pega el siguiente token: "
                            + userFound.getTokenUser();
                    testService.sendEmail(userFound.getEmail(), userFound.getName() + userFound.getLastName(),
                            "Cambio de contraseña Agrosoft", body);
                }
                return userFound.getTokenUser();
            } else {
                // return 1, the account isn't activated and send the activation email
                User userFound = list.get(0);
                if (list.get(0).getTokenUser() == null || list.get(0).getTokenUser().equals("")) {
                    // generar token y reenviar email.
                    String token = UUID.randomUUID().toString();
                    userFound.setTokenUser(token);
                    entityManager.merge(userFound);
                    String body = "Activa tu cuenta dando click aqui: " + activeLink + token;
                    testService.sendEmail(userFound.getEmail(), userFound.getName() + userFound.getLastName(),
                            "Activa tu cuenta Agrosoft", body);
                } else {
                    // reenviar email con el token
                    String body = "Activa tu cuenta dando click aqui: " + activeLink + list.get(0).getTokenUser();
                    testService.sendEmail(userFound.getEmail(), userFound.getName() + userFound.getLastName(),
                            "Activa tu cuenta Agrosoft", body);
                }
                return "1";
            }
        } else {
            // return 0, the email is not registered
            return "0";
        }
    }

    @Override
    public String changePassword(JSONObject data) {
        // Validate if token exist
        String query = "FROM User WHERE token_user = :tokenUser";
        List<User> list = entityManager.createQuery(query)
                .setParameter("tokenUser", data.get("tokenUser"))
                .getResultList();
        if (!list.isEmpty()) {
            User userFound = list.get(0);
            String hash = (String) data.get("hash");
            userFound.setPassword(hash);
            userFound.setTokenUser("");
            entityManager.merge(userFound);
            return "0";
        } else {
            return "1";
        }

    }

    @Override
    public String newPassword(JSONObject data) {
        // Validate if idUser exists
        String query = "FROM User WHERE id_user = :idUser";
        List<User> listUser = entityManager.createQuery(query)
                .setParameter("idUser", data.get("idUser"))
                .getResultList();
        if (!listUser.isEmpty()) {
            User userFound = listUser.get(0);
            String passwordHashed = userFound.getPassword();
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            if (argon2.verify(passwordHashed, data.get("oldPassword").toString())) {
                if(!userFound.isActiveUser()){
                    return "3";
                }
                if(!userFound.isVerficate()){
                    return "4";
                }
                String hash = (String) data.get("hash");
                userFound.setPassword(hash);
                entityManager.merge(userFound);
                return "0";
            } else {
                return "1";
            }
        } else {
            return "2";
        }
    }

    @Override
    public String changeState(User user) {
        String query = "FROM User WHERE id_user = :idUser";
        List<User> listUsers = entityManager.createQuery(query)
                .setParameter("idUser", user.getIdUser())
                .getResultList();
        if (!listUsers.isEmpty()) {
            User userFound = listUsers.get(0);
            if (userFound.isActiveUser()) {
                userFound.setActiveUser(false);
                String email = '$' + userFound.getEmail();
                userFound.setEmail(email);
                entityManager.merge(userFound);
                return "0";
            } else {
                return "2";
            }
        } else {
            return "1";
        }
    }

    @Override
    public JSONObject comparePassword(String idUser, String password) {
        JSONObject response = new JSONObject();
        response.put("error", true);
        String query = "FROM User WHERE id_user = :idUser AND is_active_user = :isActiveUser";
        List<User> listUsers = entityManager.createQuery(query)
                .setParameter("idUser", idUser)
                .setParameter("isActiveUser", true)
                .getResultList();
        if (listUsers.isEmpty()) {
            response.put("response", "Usuario no encontrado");
            return response;
        }
        User userFound = listUsers.get(0);
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (argon2.verify(userFound.getPassword(), password)) {
            //generate a uuid
            String token = UUID.randomUUID().toString();
            userFound.setTokenUser(token);
            entityManager.merge(userFound);
            response.put("error", false);
            response.put("response", token);
            return response;
        } else {
            response.put("response", "Contraseña incorrecta");
            return response;
        }
    }

    @Override
    public JSONObject changeEmail(String token, String email) throws UnsupportedEncodingException {
        JSONObject response = new JSONObject();
        response.put("error", true);

        String query = "FROM User WHERE token_user = :tokenUser AND is_active_user = :isActiveUser";
        List<User> listUsers = entityManager.createQuery(query)
                .setParameter("tokenUser", token)
                .setParameter("isActiveUser", true)
                .getResultList();
        if (listUsers.isEmpty()) {
            response.put("response", "Token invalido o usuario no encontrado, por favor vuelve a intentarlo");
            return response;
        }
        User userFound = listUsers.get(0);

        if (userFound.getEmail().equals(email)) {
            response.put("response", "El email ingresado es el mismo que el actual");
            return response;
        }

        userFound.setEmail(email);
        userFound.setTokenUser("");
        userFound.setVerficate(false);
        entityManager.merge(userFound);
        response.put("error", false);
        response.put("response", "El email se ha cambiado correctamente. Por favor vuelva a activar su cuenta, revise su email");

        //generate an uui and send email
        String tokenNew = UUID.randomUUID().toString();
        userFound.setTokenUser(tokenNew);
        entityManager.merge(userFound);
        String body = "Activa tu cuenta dando click aqui: " + activeLink + tokenNew;
        testService.sendEmail(userFound.getEmail(), userFound.getName() + userFound.getLastName(),
                "Activa tu cuenta Agrosoft", body);
        
        return response;
    }
    //copiar y pegar
    @Override
    public List<User> getUsersByIdList(String listIdUsers, String sorter, String order, String page) {
        try{
            int pageInt = Integer.parseInt(page);
            String query = "FROM User WHERE id_user IN (" + listIdUsers + ") AND is_active_user = :isActiveUser ORDER BY " + sorter + " " + order;
            List<User> listUsers = entityManager.createQuery(query)
                    .setParameter("isActiveUser", true)
                    .setFirstResult(pageInt * 10)
                    .setMaxResults(10)
                    .getResultList();
            return listUsers;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public List<User> getEmployeeByIdList(String listIdUsers, String search, String page) {
        try{
            String preQuery ="FROM User WHERE id_user IN ("+ listIdUsers + ") AND is_active_user = :isActiveUser AND lower(name) LIKE lower(:search)";
            int pageInt = Integer.parseInt(page);
            List<User> result = (List<User>)entityManager.createQuery(preQuery)
                    .setParameter("search", "%"+search+"%")
                    .setParameter("isActiveUser", true)
                    .setFirstResult(pageInt * 10)
                    .setMaxResults(10)
                    .getResultList();
            return result;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public JSONObject getIdUserByEmail(String email) {
        JSONObject response = new JSONObject();
        response.put("error", true);
        String query = "FROM User WHERE email = :email AND is_active_user = :isActiveUser";
        try{
            User user = (User) entityManager.createQuery(query)
                    .setParameter("email", email)
                    .setParameter("isActiveUser", true)
                    .getSingleResult();
            response.put("error", false);
            response.put("response", user.getIdUser());
            return response;
        } catch (NoResultException e){
            response.put("response", "El email ingresado no existe");
            return response;
        }
    }

    @Override
    public boolean verifIdUser(String idUser) {
        String query = "FROM User WHERE id_user = :idUser AND is_active_user = :isActiveUser";

        try {
            User user = (User) entityManager.createQuery(query)
                    .setParameter("idUser", idUser)
                    .setParameter("isActiveUser", true)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

	@Override
	public Long getUsersCount(String listIdUsers) {
		// TODO this method returns a Long with the total numbers for the list
        try{
            String query ="SELECT COUNT(*) FROM User WHERE id_user IN ("+ listIdUsers + ") AND is_active_user = :isActiveUser";
            Long result = (Long)entityManager.createQuery(query)
                    .setParameter("isActiveUser", true)
                    .getSingleResult();
                return result;
        } catch (NoResultException e){
            return 0l;
        }
	}

}
