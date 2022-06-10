package usco.agrosoft.dao;

import java.util.List;

import net.minidev.json.JSONObject;

import usco.agrosoft.models.Request;

public interface RequestDao {
    JSONObject createRequest(Request request);
    Request getRequestById(String idRequest);
    boolean acceptRequest(Request request);
    boolean rejectRequest(Request request);
    boolean cancelRequest(Request request);
    List<Request> getInRequests(String idUser, String page, String stateReq);
    public Long getRequestInCount(String idUser, String stateReq);
    Long getRequestOutCount(String idUser, String stateReq);
    List<Request> getOutRequests(String idUser, String page, String stateReq);
}
