package usco.agrosoft.dao;

import usco.agrosoft.models.Farm;

import java.util.List;
import net.minidev.json.JSONObject;

public interface FarmDao {
    List<Farm> getFarms(String listIdFarms, String sorter, String order, String page);

    public JSONObject addFarm(Farm farm);

    Farm getFarm(String idFarm);

    String deleteFarm(String idFarm);

    Long getFarmCount(String listIdFarms);

    boolean verifIdFarm(String idFarm);

    public JSONObject modifyFarm(Farm farm);
    public List<Farm> findFarms(String listIdFarms, String search, String page);

    List<Farm> listFarms(String listIdFarms);
}