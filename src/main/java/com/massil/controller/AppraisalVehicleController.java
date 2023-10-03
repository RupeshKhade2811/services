package com.massil.controller;
//@author: Rupesh Khade,kalyan

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.repository.AppraiseVehicleRepo;
import com.massil.responseHandler.ApiResponseHandler;
import com.massil.services.*;
import com.massil.services.impl.AppraiseVehicleServiceImpl;
import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/appraisal")
@Tag(name = "Appraisal vehicle",description = "Appraisal Module")
public class AppraisalVehicleController {
    Logger log = LoggerFactory.getLogger(AppraisalVehicleController.class);
    @Value("${file_size}")
    private Long fileSize;
    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Autowired
    private FilterSpecificationService filterSpec;

    @Autowired
    private AppraiseVehicleService service;

    @Autowired
    private AppraiseVehicleServiceImpl service1;

    @Autowired
    private EmailService emailService;

    @Autowired
    public MarketCheckApiService marketCheckService;

    @Autowired
    private ApprFormService apprForm;
    @Autowired
    private AppraiseVehicleRepo repo;


    /**
     * This method creates new Appraisal of vehicles
     * @param apprCreaPage This is model of AppraisalCreation page coming from ui
     * @param userId       This  User id coming in header from ui
     * @return Response class
     */
  //  @Operation(value = "Add Appraisal", response = Response.class)
    @PostMapping("/addAppraiseVehicle")
    public ResponseEntity<Response> addAppraiseVehicle(@RequestBody @Validated ApprCreaPage apprCreaPage, @RequestHeader("userId") UUID userId) throws AppraisalException {


            if ((null != apprCreaPage) && (null != userId)) {
                log.debug("Creating Appraisal {}", apprCreaPage);
                Response response  = service.addAppraiseVehicle(apprCreaPage, userId, AppraisalConstants.CREATED);
                log.info(response.getMessage());
                return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

            } else throw new AppraisalException(AppraisalConstants.INVALID_USER_ID);

    }

   @Operation(summary = "draft Appraisal",description ="use to draft the appraisal")
    @PostMapping("/draftApprVeh")
    public ResponseEntity<Response> draftApprVeh(@RequestBody ApprCreaPage apprCreaPage, @RequestHeader("userId") UUID userId) throws AppraisalException {


            if ((null != apprCreaPage&&null!=apprCreaPage.getVinNumber()) && (null != userId)) {
                Response response = service.addAppraiseVehicle(apprCreaPage, userId, AppraisalConstants.DRAFT);
                return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
            } else throw new AppraisalException("Invalid user id");



    }

    /**
     * This method updates the vehicle details
     * @param apprCreaPage This is model of AppraisalCreation page coming from ui
     * @param id           This is Appraisal ref id coming in header from ui
     * @return Response class
     */
    @Operation(summary = "update Appraisal by AppraisalRef id ")
    @PostMapping("/updateAppraiseVehicle")
    public ResponseEntity<Response> updateAppraiseVehicle(@Validated @RequestBody ApprCreaPage apprCreaPage, @RequestHeader("id") Long id) throws AppraisalException, IOException, JRException, JDOMException {
        log.info("Updating Appraisal vehicle");

                Response response = service.updateAppraisal(apprCreaPage, id);
                log.debug("Object coming from ApprCreaPage for update {} ", apprCreaPage);
                return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * This method updates the vehicle details
     * @param apprCreaPage This is model of AppraisalCreation page coming from ui
     * @param id           This is Appraisal ref id coming in header from ui
     * @return Response class
     */
    @Operation(summary = "update Draft Appraisal by AppraisalRef id ")
    @PostMapping("/updateDraftAppraiseVehicle")
    public ResponseEntity<Response> updateDraftAppraiseVehicle( @RequestBody ApprCreaPage apprCreaPage, @RequestHeader("id") Long id) throws AppraisalException, IOException, JRException, JDOMException {
        log.info("Updating Appraisal vehicle");

                Response response = service.updateDraftAppraisal(apprCreaPage, id);
                log.debug("Object coming from ApprCreaPage for update {}", apprCreaPage);
                return new ResponseEntity<>(response, HttpStatus.OK);

    }



    /**
     * This method sends list of Appraisal vehicle cards to ui
     * @param userId   This  User id coming in header from ui
     * @param pageNo   This is page number given by ui
     * @param pageSize This is Number of records per page given by ui
     * @return CardsPage
     */
   @Operation(summary = "get Appraisals cards by user id ")
    @PostMapping("/getAppraisalsCards")
    public ResponseEntity<CardsPage> getAppraisalsCards(@RequestHeader("userId") UUID userId, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException {
        log.info("GETTING APPRAISAL CARDS");
        CardsPage apv = service.findAllAppraisalCards(userId, pageNo, pageSize);
        return new ResponseEntity<>(apv, HttpStatus.OK);
    }

    /**
     * This method sends the image of a car base on image name
     * @param pic1 This is the name of image send by ui
     * @return byte[]
     */
    @Operation(summary = "get Image by image name ")
    @GetMapping(value = "/getpic1",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImageFromFileSystem(@RequestParam String pic1) throws IOException , NoSuchFileException {
        log.info("DOWNLOADING IMAGE FROM FILE");
        VideoAndImageResponse response = service.downloadImageFromFileSystem(pic1);

            return new ResponseEntity<>(response.getImageBytes(), HttpStatus.OK);
    }

    /**
     * This method saves the image coming from ui and returns the uuid.jpg that is file name
     * @param file MultipartFile data coming from ui
     * @return file name in Response class
     */
    @Operation(summary = "Upload Image and Returns image name")
    @PostMapping("/uploadImage")
    public ResponseEntity<Response> uploadImage(@RequestBody MultipartFile file) throws AppraisalException, IOException {
        log.info("RUNNING THIS METHOD FOR UPLOADING THE IMAGE");

            if (null != file) {
                String map = service.imageUpload(file);
                Response response = new Response();
                response.setCode(HttpStatus.OK.value());
                response.setMessage(map);
                response.setStatus(true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else throw new AppraisalException("image cann't be empty");

    }

    /**
     * This method sends the Appraisal Creation page to ui in Edit page
     * @param appraisalId This is Appraisal ref id coming in header from ui
     * @return Response class
     */
    @Operation(summary = "showing data to UI in editing page")
    @PostMapping("/showToUi")
    public ResponseEntity<ApprCreaPage> showInEdit(@RequestHeader("AppraisalId") Long appraisalId) throws AppraisalException, JRException, IOException, JDOMException {
        log.info("Showing Appraisal to UI");
        ApprCreaPage page = service.showInEditPage(appraisalId);
        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * This method saves the video in local folder and returns the file name
     * @param file MultipartFile data coming from ui
     * @return Response class
     */
    @Operation(summary = "Upload Video and Returns Video name")
    @PostMapping("/uploadVideo")
    public ResponseEntity<Response> uploadVideo(@RequestBody MultipartFile file) throws AppraisalException, IOException {
        log.info("Multipart object for uploading video");
            if (null != file && file.getSize()<fileSize) {
                String map = service.videoUpload(file);
                Response response = new Response();
                response.setCode(HttpStatus.OK.value());
                response.setMessage(map);
                response.setStatus(true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else throw new AppraisalException("video size must be 1MB to 100MB ");

    }

    /**
     * This method sends the video to ui
     * @param filename This is file name coming from ui
     * @return byte[]
     */
    @Operation(summary = "Download Video")
    @GetMapping(value = "/downloadVideo")
    public ResponseEntity<byte[]> downloadVideo(@RequestParam("filename") String filename) throws IOException {

            log.info("This method is used to Download Video");
            VideoAndImageResponse response = service.videoDownload(filename);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.builder("inline").filename(filename).build());
                return new ResponseEntity<>(response.getVideoBytes(), headers, HttpStatus.OK);
    }

    /**
     * This method delete the Appraisal cards
     * @param apprRef This is Appraisal ref id coming in header from ui
     * @return Response class
     */
    @Operation(summary = "Delete Appraisal  by Appraisal Id")
    @PostMapping("/deleteAppraisal")
    public ResponseEntity<Response> deleteAppraisal(@RequestParam Long apprRef) throws AppraisalException {
        log.info("DELETING APPRAISAL");
        return new ResponseEntity<>(service.deleteAppraisalVehicle(apprRef), HttpStatus.OK);
    }


    /**
     * This method checks the given vin number and userId is available or not
     * @param userId
     * @param vin
     * @return
     */

    @Operation(summary = "Check Vehicle Available")
    @PostMapping("/checkVehicleAvailable")
    public ResponseEntity<Response> checkVinAvailable(@RequestHeader("userId") UUID userId,@RequestParam String vin) throws AppraisalException {


            if ((null != vin) && (null != userId)) {

                Response response  = service.checkVinAvaliable(vin,userId);
                log.info(response.getMessage());
                return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

            } else throw new AppraisalException("Invalid user id");



    }

    /**
     * This method sends details of vehicle using vinNumber
     * @param vin This is vin number of vehicle
     * @return MarketCheckData
     */
    @PostMapping("/getvehicleinfo")
    public ResponseEntity<MarketCheckData> vehicleInformation(@RequestParam("vin") @Valid @Min(17) String vin) throws AppraisalException, WebClientResponseException {
        log.info("THIS METHOD SHOWS VEHICLE INFO BY PROVIDING VIN NUMBER");
        MarketCheckData marketCheckData = marketCheckService.getMarketCheckData(vin.toUpperCase());
        return new ResponseEntity<>(marketCheckData, HttpStatus.OK);
    }


    /**
     * This method moves the appraised vehicles to inventory
     * @param apprRef This is appraisal reference id
     * @return Response
     */
    @Operation(summary = "Add to Inventory by Appraisal Id")
    @PostMapping("/moveToInventory")
    public ResponseEntity<Response> moveToInvntry(@RequestParam Long apprRef) throws AppraisalException {
        Response response = service.moveToInventory(apprRef);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method moves the appraised vehicles to inventory
     * @param apprRef This is appraisal reference id
     * @param userId  this is session userId
     * @return Response
     */
    @Operation(summary= "Adding vehicle as favorite")
    @PostMapping("/moveToWishList")
    public ResponseEntity<Response> movToWishList(@RequestParam Long apprRef, @RequestHeader("userId") UUID userId) throws AppraisalException {
        Response response = service.moveToWishList(apprRef, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * This method will get all the favorite vehicles
     * @param userId session userId
     * @return CardsPage
     */
    @Operation(summary = "It shows All the cards who is wish listed as favorite")
    @PostMapping("/getFavoriteCards")
    public ResponseEntity<CardsPage> findAllFavoriteVehicle(@RequestHeader("userId") UUID userId, @RequestParam @Min(1) Integer pageNumber, @RequestParam @Min(1) Integer pageSize) throws AppraisalException {
        CardsPage wishListed = service.findFavoriteVehicle(userId, pageNumber, pageSize);
        return new ResponseEntity<>(wishListed, HttpStatus.OK);
    }

    /**
     * This method moves the appraised vehicles to inventory
     * @param apprId This is appraisal reference id
     * @param userId session userId
     * @return Response
     */
    @Operation(summary = "It will remove the favorite vehicle")
    @PostMapping("/removeFavorite")
    public ResponseEntity<Response> removeFromFavoritesPage(@RequestParam Long apprId, @RequestHeader("userId") UUID userId) throws AppraisalException {
        Response response = service.removeVehicleFromFavoritePage(apprId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method is used to send email to user after Appraisal creation
     * @param userId getting from ui
     * @return message
     */
    @PostMapping("/sendingEmail")
    public ResponseEntity<Response> sendingCreateEmail(@RequestHeader("userId") UUID userId) throws AppraisalException, TemplateException, MessagingException, IOException, MessagingException {
        Response response = emailService.sendCreationEmail(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method will show appraisal vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */

    @PostMapping("/appraisalfilter")
    public ResponseEntity<CardsPage> appraisalFilter(@RequestBody FilterParameters filter, @RequestHeader("userId") UUID userId,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws AppraisalException {
        return new ResponseEntity<>(filterSpec.filterAppraisalVehicle(filter,userId,pageNo,pageSize),HttpStatus.OK);
    }


    /**
     * This method will show Inventory vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/inventoryfilter")
    public ResponseEntity<CardsPage> inventryFilter(@RequestBody FilterParameters filter, @RequestHeader("userId") UUID userId,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws AppraisalException {
        return new ResponseEntity<>(filterSpec.filterInventoryVehicle(filter,userId,pageNo,pageSize),HttpStatus.OK);
    }

    /**
     * This method will show search factory vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/searchfactoryfilter")
    public ResponseEntity<CardsPage> searchFacFilter(@RequestBody FilterParameters filter, @RequestHeader("userId") UUID userId,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws AppraisalException {
        return new ResponseEntity<>(filterSpec.filterSearchFactoryVehicle(filter,userId,pageNo,pageSize),HttpStatus.OK);
    }

    @GetMapping("/apprFormPdf")
    public ResponseEntity<Resource> vehRepPdf(@RequestParam("apprId") Long apprId) throws IOException, TemplateException, JRException, JDOMException {
        String  filePath = apprForm.apprFormPdf(apprForm.setDataToPdf(apprId));

        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= AppraisalForm.pdf" );
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/keyAssureDownload")
    public ResponseEntity<Resource> keyAssureVehReport(@RequestParam ("apprId") Long apprId) throws JRException, IOException, JDOMException {

        EAppraiseVehicle appraisalById = repo.getAppraisalById(apprId);
        String filePath = pdfpath+appraisalById.getTdStatus().getKeyAssureFiles();
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= keyAssureVehicleReport.pdf" );
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/keyAssurePreview")
    public ResponseEntity<byte[]> servePdf(@RequestParam ("apprId") Long apprId) throws IOException {
        EAppraiseVehicle appraisalById = repo.getAppraisalById(apprId);

        File file = new File(pdfpath, appraisalById.getTdStatus().getKeyAssureFiles());

        if (file.exists()) {
            InputStream inputStream = new FileInputStream(file);
            byte[] pdfBytes = IOUtils.toByteArray(inputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.parse("inline; filename=" + appraisalById.getTdStatus().getKeyAssureFiles()));
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/pdfViewer")
    public ResponseEntity<byte[]> previewPdf(@RequestParam ("fileName") String name) throws IOException {
        File file = new File(pdfpath, name);
        if (file.exists()) {
            InputStream inputStream = new FileInputStream(file);
            byte[] pdfBytes = IOUtils.toByteArray(inputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.parse("inline; filename=" + name));
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("/getTotalVehiclesInSystem")
    public ResponseEntity<Response> getTotalVehiclesInSystem(){
        Long vehicles = service.getTotalVehiclesInSystem();
        Response response=new Response();
        response.setTotalVehicles(vehicles);
        response.setStatus(true);
        response.setCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
