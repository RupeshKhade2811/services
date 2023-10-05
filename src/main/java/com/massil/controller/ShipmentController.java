package com.massil.controller;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import com.massil.services.FactoryPdfGenerator;
import com.massil.services.FilterSpecificationService;
import com.massil.services.ShipmentService;
import com.massil.services.impl.EmailServiceImpl;
import freemarker.template.TemplateException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/shipment")
@Tag(name = "Shipment", description = "Shipment Module")
public class ShipmentController {
    Logger log = LoggerFactory.getLogger(ShipmentController.class);
    @Autowired
    private ShipmentService service;

    @Autowired
    private FactoryPdfGenerator pdfGenerator;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private FilterSpecificationService specService;

    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Autowired
    private FactoryPdfGenerator pdfGeneratorSrvc;

    /**
     * This method sends list of shipment my sold cars cards and pagination information  to ui
     * @param id  This   user id or dealer id coming in header from ui
     * @param pageNo   This is page number given by ui
     * @param pageSize  This is Number of records per page given by ui
     * @return   CardsPage
     */
    @Operation(summary = "get My Sold car cards by user id and dealer id")
    @PostMapping("/getSoldCarCards")
    public ResponseEntity<CardsPage> soldCarCards(@RequestHeader("id") UUID id, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException {
        log.info("GETTING MY SOLD CARS CARDS");
        CardsPage apv = service.mySellsCards(id, pageNo, pageSize);
        return new ResponseEntity<CardsPage>(apv, HttpStatus.OK);

    }

    /**
     * This method sends list of shipment my purchase cars cards and pagination information  to ui
     * @param id  This   user id or dealer id coming in header from ui
     * @param pageNo   This is page number given by ui
     * @param pageSize  This is Number of records per page given by ui
     * @return   CardsPage
     */
    @Operation(summary = "get My purchase car cards by user id and dealer id")
    @PostMapping("/getPurCarCards")
    public ResponseEntity<CardsPage> getMyPurchaseCarCards(@RequestHeader("id") UUID id, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException {
        log.info("GETTING MY PURCHASE CARS CARDS");
        CardsPage apv = service.myBuyerCards(id, pageNo, pageSize);
        return new ResponseEntity<CardsPage>(apv, HttpStatus.OK);

    }


    @PostMapping("/pdf")
    public ResponseEntity<Response> pdfGen(@RequestHeader("offerId") Long offerId) throws IOException, JRException, JDOMException, GlobalException {
        Response response1 = pdfGenerator.pdfTable(offerId);
        return new ResponseEntity<>(response1, HttpStatus.ACCEPTED);

    }
    @PostMapping("/buyerAgreed")
    public ResponseEntity<Response> buyerShipmentDet(@RequestBody Shipment shipment,@RequestHeader ("shipmentId") Long shipId) throws GlobalException, IOException {
        return new ResponseEntity<>(service.buyerAgreedService(shipment,shipId),HttpStatus.OK);
    }
    @PostMapping("/sellerAgreed")
    public ResponseEntity<Response> sellerShipmentDet(@RequestBody Shipment shipment,@RequestHeader ("shipmentId") Long shipId) throws GlobalException, AppraisalException, IOException {
        return new ResponseEntity<>(service.sellerAgreedService(shipment,shipId),HttpStatus.OK);
    }

    @PostMapping("/mailAttach")
    String mailAttach(Long offerId) throws MessagingException,IOException{
        try {
            emailService.sendMailWithAttachment(offerId);
            return "Email Sent!";
        } catch (Exception ex) {
            return "Error in sending email: " + ex;
        }
    }


    @GetMapping("/odometerPdf")
    public ResponseEntity<Resource> vehRepPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }


    @GetMapping("/buyerOrderPdf")
    public ResponseEntity<Resource> buyrOdrPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/vehRepPdf")
    public ResponseEntity<Resource> vehRepotPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/apprReportPdf")
    public ResponseEntity<Resource> apprReportPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/licenseReportPdf")
    public ResponseEntity<Resource> licenseReportPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/taxReportPdf")
    public ResponseEntity<Resource> taxReportPdf(@RequestParam("fileName") String filename)  {
        String filePath = pdfpath+filename;
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment;filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/apprPdf")
    public ResponseEntity<Resource>  apprPdf(@RequestParam String start,@RequestParam String end) throws JRException, IOException, ParseException {
        String  filePath = pdfGenerator.appraisalList(start, end);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/offerReportPdf")
    public ResponseEntity<Resource> offerPdf(@RequestParam String start,@RequestParam String end) throws JRException, IOException, JDOMException, ParseException {

        String  filePath = pdfGeneratorSrvc.offerReport(start, end);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @PostMapping("/offerList")
    public ResponseEntity<TableList> offerList(@RequestParam Integer pageNo, @RequestParam Integer pageSize,@RequestParam String start,@RequestParam String end) throws ParseException {
        TableList tableList = pdfGeneratorSrvc.offerList(pageNo, pageSize,start, end);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }

    @GetMapping("/totalmemReport")
    public ResponseEntity<Resource> totalMemPdf() throws JRException, IOException {

        String  filePath =  pdfGenerator.totalMemReport();
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @GetMapping("/facSalesRep")
    public ResponseEntity<Resource>  facSaleRepPdf(@RequestParam UUID fsUserId) throws JRException, IOException {

        String  filePath =  pdfGenerator.facSalesReport(fsUserId);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @GetMapping("/facMngReport")
    public  ResponseEntity<Resource> facMngRepPdf(@RequestParam UUID fmUserId) throws JRException, IOException {

        String  filePath =  pdfGenerator.facMngReport(fmUserId);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);

    }



    @GetMapping("/soldReportPdf")
    public ResponseEntity<Resource> genSoldReport(@RequestParam String start,@RequestParam String end) throws IOException, JRException, JDOMException, ParseException {

        String  filePath = pdfGeneratorSrvc.soldPdf(start, end);

        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @PostMapping("/soldList")
    public ResponseEntity<TableList> soldList(@RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam String start,@RequestParam String end) throws ParseException {
        TableList tableList = pdfGeneratorSrvc.soldList(pageNo, pageSize,start, end);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }



    @GetMapping("/genDlrInvntryReport")
    public ResponseEntity<Resource> dlrInvntryReport(@RequestParam("userId") UUID userId, @RequestParam(name = "daysSinceInventory",defaultValue = "0") Integer daysSinceInventory,@RequestParam(required = false) String vehicleMake,@RequestParam(name = "consumerAskPrice",defaultValue = "-1") Double consumerAskPrice) throws IOException, TemplateException, JRException, JDOMException {
        String  filePath = pdfGeneratorSrvc.dlrInvtryPdf(userId,daysSinceInventory,vehicleMake,consumerAskPrice);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @PostMapping("/dlrInvntryTableList")
    public ResponseEntity<TableList> getSellingDealer(@RequestBody DlrInvntryPdfFilter filter,@RequestHeader("userId") UUID userId, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        TableList sellingDealerList = specService.sendDlrFilterList(filter,userId,pageNo,pageSize);
        return new ResponseEntity<>(sellingDealerList,HttpStatus.OK);
    }


    @GetMapping("/genSalesReport")
    public ResponseEntity<Resource> genSalesReport(@RequestParam("userId") UUID userId,@RequestParam String start, @RequestParam String end) throws IOException, TemplateException, JRException, JDOMException, ParseException {
        String  filePath = pdfGeneratorSrvc.salesPdf(userId,start,end);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @PostMapping("/saleTableList")
    public ResponseEntity<TableList> saleList(@RequestHeader("userId") UUID userId, @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam String start, @RequestParam String end) throws ParseException {
        TableList tableList = pdfGeneratorSrvc.salesList(userId,pageNo, pageSize, start, end);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }


    @GetMapping("/genPurchaseReport")
    public ResponseEntity<Resource> genPurchaseReport(@RequestParam("userId") UUID userId,@RequestParam String start, @RequestParam String end) throws IOException, TemplateException, JRException, JDOMException, ParseException {

        String  filePath = pdfGeneratorSrvc.purchasePdf(userId,start,end);

        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @PostMapping("/purchaseTableList")
    public ResponseEntity<TableList> purchaseList(@RequestHeader("userId") UUID userId, @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam String start, @RequestParam String end) throws ParseException {
        TableList tableList = pdfGeneratorSrvc.purchaseList(userId,pageNo, pageSize, start, end);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }


    @GetMapping("/transacReportPdf")
    public ResponseEntity<Resource> genTrnsReport(@RequestParam("userId") UUID userId,@RequestParam String start, @RequestParam String end) throws IOException, TemplateException, JRException, JDOMException, ParseException {

        String  filePath = pdfGeneratorSrvc.trnstionPdf(userId,start,end);
        Resource resource = new PathResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +filePath);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @PostMapping("/transacList")
    public ResponseEntity<TableList> transacList(@RequestHeader("userId") UUID userId,@RequestParam Integer pageNo, @RequestParam Integer pageSize,@RequestParam String start, @RequestParam String end) throws ParseException {
        TableList tableList = pdfGeneratorSrvc.transactionList(userId,pageNo, pageSize, start, end);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }






    @Operation(summary = "get all records of Appraisals")
    @PostMapping("/getAppraisalTableList")
    public ResponseEntity<TableList> appraisalListPage(@RequestParam String start,@RequestParam String end, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws ParseException {
        TableList tableList = pdfGenerator.appraisalListPage(start,end,pageNo, pageSize);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }

    @Operation(summary = "get total members")
    @PostMapping("/getTotalMembers")
    public ResponseEntity<TableList> membersListPage(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        TableList tableList = pdfGenerator.totalMemReport(pageNo, pageSize);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
    }
     @Operation(summary = "get total members of salesman")
     @PostMapping("/getSalesManMembers")
     public ResponseEntity<TableList> salesManMembersListPage(@RequestParam UUID fsUserId,@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        TableList tableList = pdfGenerator.salesManMemReport(fsUserId,pageNo, pageSize);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
     }
     @Operation(summary = "get total members of manager")
     @PostMapping("/getManagerMembers")
     public ResponseEntity<TableList> managersMembersListPage(@RequestParam UUID fmUserId,@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        TableList tableList = pdfGenerator.managersMemReport(fmUserId,pageNo, pageSize);
        return new ResponseEntity<>(tableList, HttpStatus.OK);
     }

    @PostMapping("/getSlrDealerList")
    public ResponseEntity<SellingDealerList> getSellingDealer(@RequestBody SellingDealer filter) throws AppraisalException {
        SellingDealerList sellingDealerList = specService.sendSlrDlrList(filter);
        return new ResponseEntity<>(sellingDealerList,HttpStatus.OK);
    }

    @PostMapping("/getSlrFtryMngrList")
    public ResponseEntity<SellingDealerList> getSlrFtryMngrList(@RequestBody SellingDealer filter) throws AppraisalException {
        SellingDealerList sellingDealerList = specService.displayByFtryManagerList(filter);
        return new ResponseEntity<>(sellingDealerList,HttpStatus.OK);
    }

    @PostMapping("/getSlrFtrySlsList")
    public ResponseEntity<SellingDealerList> getSlrFtrySlsList(@RequestBody SellingDealer filter) throws AppraisalException {
        SellingDealerList sellingDealerList = specService.displayByFtrySalesList(filter);
        return new ResponseEntity<>(sellingDealerList,HttpStatus.OK);
    }




}
