package com.massil.services;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface AppraiseVehicleService {




   Response checkVinAvaliable(String vin, UUID userId);

    /**
     * This method saves th new Appraisal of vehicle and returns the message
     * @param apprCreaPage This is the dto object of ApprCreaPage
     * @param userId This is userId
     * @return message
     */
    Response addAppraiseVehicle(ApprCreaPage apprCreaPage, UUID userId, String apprStatus) throws AppraisalException;



    /**
     * This method sends the CardsPage object which having List of AppraisalVehicleCard and pagination information
     * @param userId This is the user id
     * @param pageNumber This is the page number
     * @param pageSize This is the number of cards per page
     * @return Appraisal card list and page information ie total records and  total pages
     * @author Rupesh khade
     *
     */
    CardsPage findAllAppraisalCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * This method sends the image to ui
     * @param imageName This is the image name coming from ui
     * @return image of a car in byte[]
     * @author Rupesh Khade
     *
     */
    VideoAndImageResponse downloadImageFromFileSystem(String imageName) throws IOException;

    /**
     * This method is uploading the image and storing in local folder
     * @param file This is the image coming from ui as a MultipartFile
     * @return file name OR Image name
     * @author kalyan
     */
    String imageUpload(MultipartFile file) throws AppraisalException, IOException;

    /**
     *This method  sends The ApprCreaPage object to ui, This is edit page in ui
     * @param appraisalId This is AppraisalVehicle id
     * @return ApprCreaPage, This is edit page in ui
     * @autor kalyan
     */
    ApprCreaPage showInEditPage(Long appraisalId) throws AppraisalException, JRException, IOException, JDOMException, JRException, JDOMException;
    /**
     * This method uploads the video file in local folder
     * @author Rupesh Khade
     * @param file
     * @return name of video with uuid
     */
    String videoUpload(MultipartFile file) throws AppraisalException, IOException;

    /**
     *This method sends video file
     * @author Rupesh khade
     * @param videoName
     * @return video file as attachment
     */
    VideoAndImageResponse videoDownload(String videoName) throws IOException;

    /**
     * This method Updates the Appraisal
     * @param page This is ApprCreaPage dto  coming from ui
     * @param apprId This is EAppraiseVehicle I'd
     * @return message
     * @author Kalyan
     */
     Response updateAppraisal(ApprCreaPage page, Long apprId) throws AppraisalException, IOException, JRException, JDOMException;

    /**
     * This method Updates the Draft Appraisal
     * @param page This is ApprCreaPage dto  coming from ui
     * @param apprId This is EAppraiseVehicle I'd
     * @return message
     * @author Kalyan
     */
    Response updateDraftAppraisal(ApprCreaPage page, Long apprId) throws AppraisalException, IOException, JRException, JDOMException;

    /**
     * This method delete the appraisal by setting  valid field as false
     * @param apprRef This is appraisal ref id coming from ui in header
     * @return message
     */
    Response deleteAppraisalVehicle(Long apprRef) throws AppraisalException;




    /**
     * this method will add the appraisal card into the inventory
     * @param apprRef This is appraisal ref id coming from ui in header
     * @return message
     */
    Response moveToInventory(Long apprRef) throws AppraisalException;

    /**
     * this method will make vehicle favorite
     * @param apprRef This is appraisal ref id coming from ui in header
     * @param userId  This is user id coming from ui in header
     * @return message
     */
    Response moveToWishList(Long apprRef,UUID userId) throws AppraisalException;
    /**
     * this method will get all favorite cards of particular user
     * @param userId This is user id coming from ui in header
     * @return CardsPage
     */
    CardsPage findFavoriteVehicle(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * this method will remove vehicle from favorite
     * @param apprRef This is appraisal ref id coming from ui in header
     * @param userId This is user id coming from ui in header
     * @return message
     */

    Response removeVehicleFromFavoritePage(Long apprRef,UUID userId) throws AppraisalException;

 /**
  * This method is generating keyassure vehicle report PDF
  * @param pdfDataDto
  * @return
  */
 String vehReportPdf(PdfDataDto pdfDataDto) throws JRException, IOException, JDOMException;
 Long getTotalVehiclesInSystem();



 byte[] keyAssureReport(Long apprId) throws IOException;

 byte[] servePdf(Long apprId) throws IOException;

 byte[] previewPdf(String name) throws IOException;




}
