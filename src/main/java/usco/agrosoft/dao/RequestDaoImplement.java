package usco.agrosoft.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import net.minidev.json.JSONObject;
import usco.agrosoft.models.Request;

@Repository
@Transactional
public class RequestDaoImplement implements RequestDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public JSONObject createRequest(Request request) {

        JSONObject response = new JSONObject();
        response.put("error", true);

        //validate if request exists
        String query = "FROM Request WHERE id_user_emmiter = :idUserEmmiter AND id_farm = :idFarm AND id_role = :idRole AND id_user_receiver = :idUserReceiver AND is_active_request = :isActiveRequest AND id_state = :idState AND is_active_request = :isActiveRequest";
        try{
            Request requestExists = (Request) entityManager.createQuery(query)
            .setParameter("idUserEmmiter", request.getIdUserEmmiter())
                    .setParameter("idFarm", request.getIdFarm())
                    .setParameter("idRole", request.getIdRole())
                    .setParameter("idUserReceiver", request.getIdUserReceiver())
                    .setParameter("isActiveRequest", request.isActiveRequest())
                    .setParameter("idState", request.getIdState())
                    .setParameter("isActiveRequest", request.isActiveRequest())
                    .getSingleResult();
            
            response.put("response", "La solicitud ya existe");
            return response;
            
        } catch (NoResultException e){
            try{
                entityManager.merge(request);
                response.put("error", false);
                response.put("response", "Solicitud enviada con exito");
                return response;
            }catch(Exception ex){
                response.put("response", "Error al enviar la solicitud");
                return response;
            }
        }  
    }

    @Override
    @Transactional
    public Request getRequestById(String idRequest){
        String query = "From Request WHERE id_request = :idRequest AND is_active_request = :isActiveRequest";
        try{
            Request request = (Request) entityManager.createQuery(query)
                .setParameter("idRequest", idRequest)
                .setParameter("isActiveRequest", true)
                .getSingleResult();

            return request;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    @Transactional
    public boolean acceptRequest(Request request){
        try{
            request.setIdState("2");
            entityManager.merge(request);
            return true;
        } catch (NoResultException e){
            return false;
        }
    }

    @Override
    @Transactional
    public boolean rejectRequest(Request request) {
        try{
            request.setIdState("3");
            entityManager.merge(request);
            return true;
        } catch (NoResultException e){
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelRequest(Request request) {
        try{
            request.setActiveRequest(false);
            entityManager.merge(request);
            return true;
        } catch (NoResultException e){
            return false;
        }
    }

    @Override
    public List<Request> getInRequests(String idUser, String page, String stateReq) {
        int pageInt = Integer.parseInt(page);
        String preQuery = "";

        if(stateReq.equals("1")){
            preQuery ="FROM Request WHERE id_user_receiver = :idUserReceiver AND is_active_request = :isActiveRequest AND id_state = '1' ORDER BY created_date DESC";
        }else{
            preQuery ="FROM Request WHERE id_user_receiver = :idUserReceiver AND is_active_request = :isActiveRequest AND (id_state = '2' OR id_state = '3') ORDER BY created_date DESC";
        }
        //get all requests where idUserReceiver = idUser and is_active_request = true
        try{
            List<Request> requests = entityManager.createQuery(preQuery)
                .setParameter("idUserReceiver", idUser)
                .setParameter("isActiveRequest", true)
                .setFirstResult(pageInt * 10)
                .setMaxResults(10)
                .getResultList();
            return requests;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public Long getRequestInCount(String idUser, String stateReq) {
        String preQuery = "";
        if(stateReq.equals("1")){
            preQuery ="SELECT COUNT(*) FROM Request WHERE id_user_receiver = :idUserReceiver AND is_active_request = :isActiveRequest AND id_state = '1'";
        }else{
            preQuery ="SELECT COUNT(*) FROM Request WHERE id_user_receiver = :idUserReceiver AND is_active_request = :isActiveRequest AND (id_state = '2' OR id_state = '3')";
        }
        try{
            Long result = (Long)entityManager.createQuery(preQuery)
                    .setParameter("idUserReceiver", idUser)
                    .setParameter("isActiveRequest", true)
                    .getSingleResult();
            return result;
        } catch (NoResultException e){
            return 0l;
        }
    }

    @Override
    public Long getRequestOutCount(String idUser, String stateReq) {
        String preQuery ="";
        if(stateReq.equals("1")){
            preQuery ="SELECT COUNT(*) FROM Request WHERE id_user_emmiter = :idUserEmmiter AND is_active_request = :isActiveRequest AND id_state = '1'"; 
        }else{
            preQuery ="SELECT COUNT(*) FROM Request WHERE id_user_emmiter = :idUserEmmiter AND is_active_request = :isActiveRequest AND (id_state = '2' OR id_state = '3')";
        }
        try{                                                                          
            Long result = (Long)entityManager.createQuery(preQuery)
                    .setParameter("idUserEmmiter", idUser)
                    .setParameter("isActiveRequest", true)
                    .getSingleResult();
            return result;
        } catch (NoResultException e){
            return 0l;
        }
    }

    @Override
    public List<Request> getOutRequests(String idUser, String page, String stateReq) {
        int pageInt = Integer.parseInt(page);

        String preQuery = "";

        if(stateReq.equals("1")){
            preQuery ="FROM Request WHERE id_user_emmiter = :idUserEmmiter AND is_active_request = :isActiveRequest AND id_state = '1' ORDER BY created_date DESC";
        }else{
            preQuery ="FROM Request WHERE id_user_emmiter = :idUserEmmiter AND is_active_request = :isActiveRequest AND (id_state = '2' OR id_state = '3') ORDER BY created_date DESC";
        }

        //get all requests where idUserEmmiter = idUser and is_active_request = true
        try{
            List<Request> requests = entityManager.createQuery(preQuery)
                .setParameter("idUserEmmiter", idUser)
                .setParameter("isActiveRequest", true)
                .setFirstResult(pageInt * 10)
                .setMaxResults(10)
                .getResultList();
            return requests;
        } catch (NoResultException e){
            return null;
        }
    }

    
}
