package usco.agrosoft.dao;

import usco.agrosoft.models.User;
import usco.agrosoft.models.UserFarm;

import java.io.UnsupportedEncodingException;
import java.util.*;

import net.minidev.json.JSONObject;

public interface UserDao {

    JSONObject getIdUserByEmail(String email);

    List<User> getUsers();

    String register(User user) throws UnsupportedEncodingException;

    User getUser(String idUser);
    // String oneRegister(User user) throws UnsupportedEncodingException;

    User login(User user) throws UnsupportedEncodingException;

    String activate(String token);

    String generateToken(JSONObject data) throws UnsupportedEncodingException;

    String changePassword(JSONObject data);

    String newPassword(JSONObject data);

    String changeState(User user);

    JSONObject modifyUser(String idUser, int idIdentifier, String phoneNumber, String name, String lastName);

    JSONObject comparePassword(String idUser, String password);

    JSONObject changeEmail(String token, String email) throws UnsupportedEncodingException;

    List<User> getUsersByIdList(String listIdUsers, String sorter, String order, String page);

    boolean verifIdUser(String idUser);
    public List<User> getEmployeeByIdList(String listIdUsers, String search, String page);

    Long getUsersCount(String listIdUsers);
}
