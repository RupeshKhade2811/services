package com.massil.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.*;

import com.massil.persistence.model.EAppraiseVehicle;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.ceil;

@Component
public class CompareUtils {

    @Value("${access_key}")
    private String accesskey;

    @Value(("${secret}"))
    private String secret;

    @Value(("${amazonS3_url}"))
    private String amazonS3Url;

    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Value("${image_folder_path}")
    private String imageFolderPath;
    @Value("${video_folder_path}")
    private String videoFolderPath;
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
    public Boolean isDocPresent(String fileName) throws IOException {
     return doesObjectExist1(pdfpath, fileName);
    }
    public Boolean isImagePresent(String fileName) throws IOException {
        return doesObjectExist1(imageFolderPath, fileName);
    }
    public Boolean isVideoPresent(String fileName) throws IOException {
        return doesObjectExist1(videoFolderPath, fileName);
    }

    public byte[] fileDownloadfromBucket(String bucketName,String fileName) throws IOException {
        //object from amazons3
        byte[] responseBytes = new byte[0];
        if(null!= fileName && !fileName.equals("")&&null!=bucketName&&!bucketName.equals("")) {
                //object from amazons3
                ClientConfiguration config = new ClientConfiguration();
                config.setProtocol(Protocol.HTTPS);
                AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accesskey, secret), config);
                S3ClientOptions options = new S3ClientOptions();
                options.setPathStyleAccess(true);
                s3.setS3ClientOptions(options);
                s3.setEndpoint(amazonS3Url);  // ECS IP Address
                if (Boolean.TRUE.equals(s3.doesObjectExist(bucketName, fileName))) {

            S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, fileName));

            S3ObjectInputStream objectContent = s3Object.getObjectContent();
            byte[] byteArray = new byte[(int) s3Object.getObjectMetadata().getContentLength()];
            int bytesRead;

                try {
                    while ((bytesRead = objectContent.read(byteArray)) != -1) {
                        responseBytes = Arrays.copyOf(responseBytes, responseBytes.length + bytesRead);
                        System.arraycopy(byteArray, 0, responseBytes, responseBytes.length - bytesRead, bytesRead);
                    }
                } finally {
                    objectContent.close();
                    s3Object.close();
                }
            }
        }

        return responseBytes;
    }
    public String uploadFileInBucket(File file,String bucketName,String fileName)  {
        if(null!= fileName && !fileName.equals("")&&null!=bucketName&&!bucketName.equals("")) {
            //object to AmazonS3
            ClientConfiguration config = new ClientConfiguration();
            config.setProtocol(Protocol.HTTPS);
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accesskey, secret), config);
            S3ClientOptions options = new S3ClientOptions();
            options.setPathStyleAccess(true);
            s3.setS3ClientOptions(options);
            s3.setEndpoint(amazonS3Url);  //ECS IP Address
            log.info("Listing buckets");
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(request);
        }
        return fileName;
    }

    public Long calTotalPages(Long totalRecords,Long pageSize){
        double totalpages = ceil((totalRecords/(pageSize*1.00)));
        return (long) totalpages;
    }

public Boolean doesObjectExist1(String bucketName, String fileName){
    if(null!= fileName && !fileName.equals("")&&null!=bucketName&&!bucketName.equals("")) {
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accesskey, secret), config);
        S3ClientOptions options =  new S3ClientOptions();
        options.setPathStyleAccess(true);
        s3.setS3ClientOptions(options);
        s3.setEndpoint(amazonS3Url);  // ECS IP Address
        if(Boolean.TRUE.equals(s3.doesObjectExist(bucketName, fileName))) {
            return true;
        }
    }
        return false;
    }

    public Boolean deleteObject(String bucketName, String fileName){
        if(null!= fileName && !fileName.equals("")&&null!=bucketName&&!bucketName.equals("")) {
            ClientConfiguration config = new ClientConfiguration();
            config.setProtocol(Protocol.HTTPS);
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accesskey, secret), config);
            S3ClientOptions options = new S3ClientOptions();
            options.setPathStyleAccess(true);
            s3.setS3ClientOptions(options);
            s3.setEndpoint(amazonS3Url);  // ECS IP Address
            if (Boolean.TRUE.equals(s3.doesObjectExist(bucketName, fileName))) {
                s3.deleteObject(bucketName,fileName);
                return true;
            }
        }
        return false;
    }


}
