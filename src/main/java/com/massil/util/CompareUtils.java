package com.massil.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.ceil;

@Component
public class CompareUtils {
    Logger log= LoggerFactory.getLogger(CompareUtils.class);

    /**
     * This method compare the old and new values
     * @param oldValue
     * @param newValue
     * @return boolean value
     */
    public Boolean compareValues(String oldValue, String newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }

    /**
     * This method compare the old and new values
     * @param oldValue
     * @param newValue
     * @return boolean value
     */
      public Boolean compareValues(Long oldValue, Long newValue){
          if(null==oldValue && null==newValue){
              return true;
          }
          else if(null!=oldValue&& null!=newValue) {

              return oldValue.equals(newValue);
          }

          return false;
    }

    /**
     * This method compare the old and new values
     * @param oldValue
     * @param newValue
     * @return boolean value
     */

    public Boolean compareValues(Double oldValue, Double newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }
    public Boolean compareValues(UUID oldValue, UUID newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }



    public static Boolean isEmptyMap(Map<?, ?> map) {

        return (map == null || map.isEmpty());
    }
    public static boolean isNotEmptyMap(Map<?, ?> map) {
        return !isEmptyMap(map);
    }

    /**
     * This method check the database values
     * @param dbValue
     * @return val
     */


    public String checkDbVariable(String dbValue) {
        String val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = "Null";
        }
        return val;

    }
    public String checkDbVariable(List<String> dbValue) {
        String val;
        if (null != dbValue  && dbValue.size()>0) {

            val = dbValue.toString();
        } else {
            val = "Null";
        }
        return val;

    }

    /**
     * This method check the database values
     * @param dbValue
     * @return val
     */

    public Long checkDbVariable(Long dbValue) {
        Long val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = 0L;
        }
        return val;

    }

    /**
     * This method check the database values
     * @param dbValue
     * @return val
     */

    public Double checkDbVariable(Double dbValue) {
        Double val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = 0.0;
        }
        return val;
    }

    public List<Date> StringParseToDate(String start,String end) throws ParseException {
        String s1=null;
        String s2=null;
        if(null!=start && null!=end && !end.equals("")){
            s1 =start+" 00:00:00";
            s2 =end+" 23:59:59";
        }else {
            s1 =start+" 00:00:00";
            s2 =start+" 23:59:59";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = dateFormat.parse(s1);
        Date endDate = dateFormat.parse(s2);
        List<Date> dates=new ArrayList<>();
        dates.add(startDate);
        dates.add(endDate);
        return dates;
    }
    public Boolean isDocPresent(String folderPath,String fileName){
        Path folder = Paths.get(folderPath);
        Path filePath = folder.resolve(fileName);
        if (Files.exists(filePath)) {
            log.info("File exists in the folder.");
            return true;
        }
        log.info("File does not exist in the folder.");
        return false;
    }
    public Long calTotalPages(Long totalRecords,Long pageSize){
        double totalpages = ceil((totalRecords/(pageSize*1.00)));
        return (long) totalpages;
    }

}
