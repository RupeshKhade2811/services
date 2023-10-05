package com.massil.services.impl;


import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.FactoryPdfGenerator;
import com.massil.services.FilterSpecificationService;
import com.massil.util.CompareUtils;
import com.massil.util.DealersUser;
import freemarker.template.TemplateException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;


@Service
public class FactoryPdfGeneratorImpl implements FactoryPdfGenerator {
    @Autowired
    private AppraiseVehicleRepo appraiseVehicleRepo;
    @Autowired
    private AppraisalFormViewRepo appraiseVehiViewRepo;
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private ShipmentRepo shipmentRepo;
    @Autowired
    private OffersMapper mapper;
    @Autowired
    private AppraisalVehicleMapper vehicleMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private FileStatusRepo fileRepo;
    @Autowired
    private TransReportRepo transReportRepo;
    @Autowired
    private OfferQuotesRepo offerQuotesRepo;
    @Autowired
    private OfferPdfRepo offerPdfRepo;
    @Autowired
    private TotalMembersRepo totalMembersRepo;
    @Autowired
    private CompareUtils compareUtils;
    @Autowired
    private MembersByFactorySalesmenRepo salesmenRepo;
    @Autowired
    private MembersByFactoryManagerRepo managerRepo;
    @Autowired
    private TroubleCodesRepo troubleCodesRepo;
    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Value("${image_folder_path}")
    private String pclink;
    @Autowired
    private DealersUser dealersUser;
    @Autowired
    private FilterSpecificationService filterSpecificationService;
    @Autowired
    private DlrSalesRepo dlrSalesRepo;
    @Autowired
    private TestDriveMeasurementsRepo testRepo;
    @Autowired
    private PreStartMeasurementRepo preStartRepo;

    Logger log = LoggerFactory.getLogger(FactoryPdfGeneratorImpl.class);


    @Override
    public Response pdfTable(Long offerId) throws JRException, IOException, JDOMException, GlobalException {

        List<EFileStatus> fileData = fileRepo.findByAppId(offerId);
        PdfDto pdfDto= new PdfDto();
        EFileStatus save=null;
        EFileStatus save1=null;
        EFileStatus save2=null;
        EFileStatus save3=null;
        EFileStatus save4=null;
        EFileStatus save5=null;
        List<EFileStatus>list=null;
        List<PdfList>list1=null;

        EOffers offer = offersRepo.findByOfferId(offerId);
        if (fileData.isEmpty()) {
            String name = UUID.randomUUID() + AppraisalConstants.pdf;
            String name1 = UUID.randomUUID() + AppraisalConstants.pdf;
            String name2 = UUID.randomUUID() + AppraisalConstants.pdf;
            String name3 = UUID.randomUUID() + AppraisalConstants.pdf;
            String name4 = UUID.randomUUID() + AppraisalConstants.pdf;
            String name5 = UUID.randomUUID() + AppraisalConstants.pdf;

            EFileStatus eFileStatus = new EFileStatus();
            eFileStatus.setOffers(offer);
            eFileStatus.setAppraisalRef(offer.getAppRef());
            eFileStatus.setModule(AppraisalConstants.ODOMETER);
            eFileStatus.setFileName(name);
            eFileStatus.setStatus(AppraisalConstants.PENDING);
            save = fileRepo.save(eFileStatus);

            EFileStatus eFileStatus1 = new EFileStatus();
            eFileStatus1.setOffers(offer);
            eFileStatus1.setAppraisalRef(offer.getAppRef());
            eFileStatus1.setFileName(name1);
            eFileStatus1.setModule(AppraisalConstants.WHOLE_SALE_BUY_ORDER);
            eFileStatus1.setStatus(AppraisalConstants.PENDING);
            save1 = fileRepo.save(eFileStatus1);

            EFileStatus eFileStatus2 = new EFileStatus();
            eFileStatus2.setOffers(offer);
            eFileStatus2.setAppraisalRef(offer.getAppRef());
            eFileStatus2.setFileName(name2);
            eFileStatus2.setModule(AppraisalConstants.VEHICLE_REPORT);
            eFileStatus2.setStatus(AppraisalConstants.PENDING);
            save2 = fileRepo.save(eFileStatus2);

            EFileStatus eFileStatus3 = new EFileStatus();
            eFileStatus3.setOffers(offer);
            eFileStatus3.setAppraisalRef(offer.getAppRef());
            eFileStatus3.setFileName(name3);
            eFileStatus3.setModule(AppraisalConstants.APPRAISAL_REPORT);
            eFileStatus3.setStatus(AppraisalConstants.PENDING);
            save3 = fileRepo.save(eFileStatus3);

            EFileStatus eFileStatus4 = new EFileStatus();
            eFileStatus4.setOffers(offer);
            eFileStatus4.setAppraisalRef(offer.getAppRef());
            eFileStatus4.setFileName(name4);
            eFileStatus4.setModule(AppraisalConstants.LICENSE_REPORT);
            eFileStatus4.setStatus(AppraisalConstants.PENDING);
            save4 = fileRepo.save(eFileStatus4);

            EFileStatus eFileStatus5 = new EFileStatus();
            eFileStatus5.setOffers(offer);
            eFileStatus5.setAppraisalRef(offer.getAppRef());
            eFileStatus5.setFileName(name5);
            eFileStatus5.setModule(AppraisalConstants.TAX_FORM);
            eFileStatus5.setStatus(AppraisalConstants.PENDING);
            save5 = fileRepo.save(eFileStatus5);


            Map<Integer,String> nameMap= new TreeMap<>();
            nameMap.put(1,name);
            nameMap.put(2,name1);
            nameMap.put(3,name2);
            nameMap.put(4,name3);
            nameMap.put(5,name4);
            nameMap.put(6,name5);

            pdfCreation(offer.getAppRef().getId(), offer,nameMap);
            list= new ArrayList<>();
            list.add(save);
            list.add(save1);
            list.add(save2);
            list.add(save3);
            list.add(save4);
            list.add(save5);


            list1=new ArrayList<>();
            for (EFileStatus e: list) {
                PdfList pdfList= new PdfList();
                pdfList.setId(e.getId());
                pdfList.setFileName(e.getFileName());
                pdfList.setModule(e.getModule());
                pdfList.setStatus(e.getStatus());
                list1.add(pdfList);
            }

        }
        else  {

            if(fileData.size()!=6){
                String name3 = UUID.randomUUID() + AppraisalConstants.pdf;
                EFileStatus eFileStatus3 = new EFileStatus();
                eFileStatus3.setOffers(offer);
                eFileStatus3.setAppraisalRef(offer.getAppRef());
                eFileStatus3.setFileName(name3);
                eFileStatus3.setModule(AppraisalConstants.APPRAISAL_REPORT);
                eFileStatus3.setStatus(AppraisalConstants.PENDING);
                save3 = fileRepo.save(eFileStatus3);
                fileData.add(save3);

                String name4 = UUID.randomUUID() + AppraisalConstants.pdf;
                EFileStatus eFileStatus4 = new EFileStatus();
                eFileStatus4.setOffers(offer);
                eFileStatus4.setAppraisalRef(offer.getAppRef());
                eFileStatus4.setFileName(name4);
                eFileStatus4.setModule(AppraisalConstants.LICENSE_REPORT);
                eFileStatus4.setStatus(AppraisalConstants.PENDING);
                save4 = fileRepo.save(eFileStatus4);
                fileData.add(save4);

                String name5 = UUID.randomUUID() + AppraisalConstants.pdf;
                EFileStatus eFileStatus5 = new EFileStatus();
                eFileStatus5.setOffers(offer);
                eFileStatus5.setAppraisalRef(offer.getAppRef());
                eFileStatus5.setFileName(name5);
                eFileStatus5.setModule(AppraisalConstants.TAX_FORM);
                eFileStatus5.setStatus(AppraisalConstants.PENDING);
                save5 = fileRepo.save(eFileStatus5);
                fileData.add(save5);
                
            }


            Map<Integer,String> nameMap= new TreeMap<>();
            for (EFileStatus fileStatus:fileData) {
                if(fileStatus.getModule().equals(AppraisalConstants.ODOMETER)){
                    nameMap.put(1,fileStatus.getFileName());
                }
                if(fileStatus.getModule().equals(AppraisalConstants.WHOLE_SALE_BUY_ORDER)){
                    nameMap.put(2,fileStatus.getFileName());
                }
                if(fileStatus.getModule().equals(AppraisalConstants.VEHICLE_REPORT)){
                    nameMap.put(3,fileStatus.getFileName());
                }
                if(fileStatus.getModule().equals(AppraisalConstants.APPRAISAL_REPORT)){
                    nameMap.put(4,fileStatus.getFileName());
                }
                if(fileStatus.getModule().equals(AppraisalConstants.LICENSE_REPORT)){
                    nameMap.put(5,fileStatus.getFileName());
                }
                if(fileStatus.getModule().equals(AppraisalConstants.TAX_FORM)){
                    nameMap.put(6,fileStatus.getFileName());
              }
            }

            list1 = new ArrayList<>();
            for (EFileStatus e : fileData) {
                PdfList pdfList = new PdfList();
                if (e.getStatus().equals("success") && Boolean.TRUE.equals(compareUtils.isDocPresent(pdfpath,e.getFileName()))) {
                    pdfList.setId(e.getId());
                    pdfList.setFileName(e.getFileName());
                    pdfList.setModule(e.getModule());
                    pdfList.setStatus(e.getStatus());
                    list1.add(pdfList);
                }else {
                    Map<Integer, String> firstEntryMap = new TreeMap<>();
                    switch (e.getModule()){
                        case AppraisalConstants.ODOMETER:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().iterator().next();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                            pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                        case AppraisalConstants.WHOLE_SALE_BUY_ORDER:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().stream().skip(1).findFirst().get();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                           pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                        case AppraisalConstants.VEHICLE_REPORT:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().stream().skip(2).findFirst().get();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                            pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                        case AppraisalConstants.APPRAISAL_REPORT:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().stream().skip(3).findFirst().get();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                            pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                        case AppraisalConstants.LICENSE_REPORT:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().stream().skip(4).findFirst().get();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                            pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                        case AppraisalConstants.TAX_FORM:{
                            Map.Entry<Integer, String> firstEntry = nameMap.entrySet().stream().skip(5).findFirst().get();
                            firstEntryMap.put(firstEntry.getKey(), firstEntry.getValue());
                            pdfCreation(offer.getAppRef().getId(), offer,firstEntryMap);
                            pdfList.setId(e.getId());
                            pdfList.setModule(e.getModule());
                            pdfList.setFileName(e.getFileName());
                            pdfList.setStatus(e.getStatus());
                            break;
                        }
                    }
                    list1.add(pdfList);

                }
            }
        }
        pdfDto.setPdflist(list1);
        pdfDto.setCode(HttpStatus.OK.value());
        pdfDto.setMessage("pdf file list");
        pdfDto.setStatus(true);

        return pdfDto;


    }




    @Async
    public String pdfCreation(Long appRef,EOffers offer,Map<Integer,String> names) throws IOException, GlobalException {
        PdfData pdfData = setDataOfPdf(appRef);
        String pdf1 = odometerJrXmlToPdf(pdfData, names.get(1));

        PdfDataDto pdfDataDto = setDataToPdf(appRef);

        String pdf2 = whlSlByOdrPdf(pdfDataDto,names.get(2));
        String pdf3 = vehReportPdf(setDataToPdf1(appRef),names.get(3));
        String pdf4 = apprReportPdf(names.get(4));
        String pdf5 = licenseReportPdf(offer,names.get(5));
        String pdf6 = taxCertificate(offer,names.get(6));



        if (null!=pdf1){
            EFileStatus odometer =fileRepo.getFileRecrd(offer.getId(),"Odometer");
            if (null!=odometer){
                odometer.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(odometer);
        }
        if (null!=pdf2){
            EFileStatus buyerOrder = fileRepo.getFileRecrd(offer.getId(),"Buyer Order");
            if (null!=buyerOrder){
                buyerOrder.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(buyerOrder);
        }

        if (null!=pdf3){
            EFileStatus vehReport = fileRepo.getFileRecrd(offer.getId(),"Veh Report");
            if (null!=vehReport){
                vehReport.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(vehReport);
        }
        if (null!=pdf4){
            EFileStatus apprReport = fileRepo.getFileRecrd(offer.getId(),"Appr Report");
            if (null!=apprReport){
                apprReport.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(apprReport);
        }
        if (null!=pdf5){
            EFileStatus licenseReport = fileRepo.getFileRecrd(offer.getId(),"License Report");
            if (null!=licenseReport){
                licenseReport.setFileName(pdf5);
                licenseReport.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(licenseReport);
        }
        if (null!=pdf6){
            EFileStatus taxForm = fileRepo.getFileRecrd(offer.getId(),"Tax Form");
            if (null!=taxForm){
                taxForm.setFileName(pdf6);
                taxForm.setStatus(AppraisalConstants.SUCCESS);
            }
            fileRepo.save(taxForm);
        }
        return AppraisalConstants.PDF_CREATED_SUCCESSFULLY;

    }

    @Override
    public String odometerJrXmlToPdf(PdfData pdfData,String name) throws IOException {
        if (null != pdfData && null != name) {
            List<PdfData> dataList = new ArrayList<>();
            dataList.add(pdfData);
            try {
                JRBeanCollectionDataSource beanDatasource = new JRBeanCollectionDataSource(dataList);
                Resource resource = resourceLoader.getResource("classpath:odometer.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath + name);
            } catch (JRException exception) {
                log.error(exception.getMessage());
                return null;
            }
            return name;
        }
        return null;

    }
    @Override
    public String  whlSlByOdrPdf(PdfDataDto pdfDataDto,String name) throws IOException {
        if(null!=pdfDataDto && null!=name) {

            List<PdfDataDto> dataList = new ArrayList<>();
            pdfDataDto.setPicLink(this.pclink);
            dataList.add(pdfDataDto);
            try {
                JRBeanCollectionDataSource beanDatasource = new JRBeanCollectionDataSource(dataList);
                Resource resource = resourceLoader.getResource("classpath:whlSaleBuyerOdr.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath + name);
            } catch (JRException exception) {
                log.error(exception.getMessage());
                return null;
            }
            return name;
        }
        return null;
    }

    @Override
    public String vehReportPdf(PdfDataDto pdfDataDto, String name) throws IOException {
        if(null!=pdfDataDto && null!=name) {
            List<PdfDataDto> dataList = new ArrayList<>();
            dataList.add(pdfDataDto);
            try {
                JRBeanCollectionDataSource beanDatasource = new JRBeanCollectionDataSource(dataList);
                Resource resource = resourceLoader.getResource("classpath:vehicleReport.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath + name);
            } catch (JRException exception) {
                log.error(exception.getMessage());
                return null;
            }

            return name;
        }
        return null;
    }

    @Override
    public String apprReportPdf(String name) throws IOException, JRException ,JDOMException{

        ApprFormDto apprFormDto=new ApprFormDto();
        if(null!=name) {
            List<ApprFormDto> dataList=new ArrayList();
            dataList.add(apprFormDto);
            try {
                JRBeanCollectionDataSource beanDatasource = new JRBeanCollectionDataSource(dataList);
                Resource resource = resourceLoader.getResource("classpath:AppraisalFormJR.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath + name);
            } catch (JRException exception) {
                log.error(exception.getMessage());
                return null;
            }
            return name;
        }
        return  null;
    }

    @Override
    public String licenseReportPdf(EOffers offers,String name) throws IOException, JRException, JDOMException {
        if(null!=name) {
            try{
                name=offers.getSellerUserId().getDealer().getDealerLicense();
            }catch (Exception exception) {
                log.error(exception.getMessage());
                return null;
            }
            return name;
        }
        return null;
    }

    @Override
    public String taxCertificate(EOffers offers, String name) throws IOException, JRException, JDOMException {
        if(null!=name) {
            try{
                name=offers.getSellerUserId().getDealer().getTaxCertificate();
            }catch (Exception exception) {
                log.error(exception.getMessage());
                return null;
            }
            return name;
        }
        return null;
    }

    @Override
    public PdfData setDataOfPdf(Long apprRefId) {

        EAppraiseVehicle byApprId = appraiseVehicleRepo.getAppraisalById(apprRefId);
        EOffers offersByApprId = offersRepo.findByApprId(apprRefId,AppraisalConstants.BUYERACCEPTED,AppraisalConstants.SELLERACCEPTED);
        PdfData data = mapper.EAppraisalToPdfData(byApprId);
        data = mapper.offersToPdfData(offersByApprId, data);
        return data;
    }
    @Override
    public PdfDataDto setDataToPdf(Long apprRefId)  {

        EAppraiseVehicle byApprId = appraiseVehicleRepo.getAppraisalById(apprRefId);
        EOffers offers = offersRepo.findByApprId(apprRefId,AppraisalConstants.BUYERACCEPTED,AppraisalConstants.SELLERACCEPTED);
        EShipment shipment=shipmentRepo.findByApprId(apprRefId);
        PdfDataDto pdfDataDto = mapper.apprToPdfData(byApprId);
        pdfDataDto = mapper.offersToPdf(offers,shipment,pdfDataDto);
        return pdfDataDto;
    }
    @Override
    public PdfDataDto setDataToPdf1(Long apprRefId) throws GlobalException {

        EAppraiseVehicle byApprId = appraiseVehicleRepo.getAppraisalById(apprRefId);

        List<OBD2_TestDriveMeasurements> testDriveMeas = testRepo.getTestDrMeasByApprRef(apprRefId);
        OBD2_PreStartMeasurement preStart = preStartRepo.getPreStartMeasByApprRef(apprRefId);

        PdfDataDto pdfDataDto = mapper.apprToPdfData(byApprId);
        PdfDataDto pdfDataDto1 = mapper.preStartToPdfDataDto(preStart,pdfDataDto);

        if (null!=testDriveMeas&&null!=preStart) {

            List<TestDriveMes> testDriveMes = mapper.lOBD2TolTestDrMeas(testDriveMeas);

            pdfDataDto1.setTestDriveMes(testDriveMes);
            pdfDataDto1.setTrCodesSummary(setTroubleCodesSummary(pdfDataDto1));
            pdfDataDto1.setDataSummary(getDataSummary(apprRefId));
            System.out.println(getDataSummary(apprRefId));
            if (!preStart.getScannedVin().equals(null) && (byApprId.getVinNumber().equals(preStart.getScannedVin()))) {
                pdfDataDto1.setCheckVin("Yes");
            } else {
                pdfDataDto1.setCheckVin("No");
            }
            return pdfDataDto1;
        }

        return  pdfDataDto;
    }

    public String setTroubleCodesSummary(PdfDataDto pdfDataDto){
        String troubleMessage ="";
        String troubleMessage1 ="";
        if (null!=pdfDataDto.getCurrentTroubleCodes()&&!pdfDataDto.getCurrentTroubleCodes().equals("")) {
            String[] currntCodes = pdfDataDto.getCurrentTroubleCodes().split(",");
            for (String cc:currntCodes) {
                ETroubleCodes byTroubleCodes = troubleCodesRepo.findByTroubleCodes(cc);
                if (null!=byTroubleCodes) {
                    troubleMessage += "* Current Trouble Codes: " + byTroubleCodes.getTroubleMessage() + "\n";
                }
            }
        }else {
            troubleMessage = "*** No Cuurent Trouble Codes \n";
        }
        if (null!=pdfDataDto.getPendingTroubleCodes()&&!pdfDataDto.getPendingTroubleCodes().equals("")) {
            String[] pendngCodes = pdfDataDto.getPendingTroubleCodes().split(",");
            for (String pc:pendngCodes) {
                ETroubleCodes byTroubleCodes = troubleCodesRepo.findByTroubleCodes(pc);
                if (null!=byTroubleCodes) {
                    troubleMessage1 += "* Pending Trouble Codes: " + byTroubleCodes.getTroubleMessage();
                }
            }
        }else {
            troubleMessage1 = "***No Pending Trouble Codes";
        }
        String code=troubleMessage+troubleMessage1;
        return code;
    }

    public String getDataSummary(Long apprRef){
        EAppraiseVehicle byApprId = appraiseVehicleRepo.getAppraisalById(apprRef);
        List<OBD2_TestDriveMeasurements> testDriveMeas = byApprId.getTestDriveMeas();
        OBD2_PreStartMeasurement preStartMeasByApprRef = preStartRepo.getPreStartMeasByApprRef(byApprId.getId());
        PdfDataDto pdfDataDto = mapper.apprToPdfData(byApprId);
        List<TestDriveMes> testDriveMes = mapper.lOBD2TolTestDrMeas(testDriveMeas);
        String data="";
        String data1="";
        if (null!=vinCheck(byApprId,data)) {
            data1=vinCheck(byApprId, data);
        }
        if (null!=engineTempCheck(byApprId,data)) {
            data1 += engineTempCheck(byApprId, data);
        }
        if (null!=preStartVoltageCheck(preStartMeasByApprRef.getBatteryVoltage(),data)) {
            data1 += preStartVoltageCheck(preStartMeasByApprRef.getBatteryVoltage(), data);
        }
        if (null!=fuelPressureCheck(preStartMeasByApprRef.getFuelPressure(),data)) {
            data1 += fuelPressureCheck(preStartMeasByApprRef.getFuelPressure(), data);
        }
        if (null!=lastMemoryClearCheck(preStartMeasByApprRef.getWarmUps(),preStartMeasByApprRef.getTimeSince(),preStartMeasByApprRef.getMileSince(),data,pdfDataDto)) {
            data1 += lastMemoryClearCheck(preStartMeasByApprRef.getWarmUps(),preStartMeasByApprRef.getTimeSince(),preStartMeasByApprRef.getMileSince(), data,pdfDataDto);
        }
        if (null!=runningVoltageCheck(byApprId,data)) {
            data1 += runningVoltageCheck(byApprId, data);
        }
        if (null!=oxygenSensorsCheck(byApprId,data)) {

            data1 += oxygenSensorsCheck(byApprId, data);
        }
        return data1;
    }

    private String vinCheck(EAppraiseVehicle byApprId, String data) {

        OBD2_PreStartMeasurement preStartMeasByApprRef = preStartRepo.getPreStartMeasByApprRef(byApprId.getId());

        if ( null!= preStartMeasByApprRef.getScannedVin() && (byApprId.getVinNumber().equals(preStartMeasByApprRef.getScannedVin()))) {
                data = "* VIN: \n   --Scanned body vin number matched ECU vin. \n   --This is an indicator that the vehicle is original and has not been tampered with.\n";
            } else if (preStartMeasByApprRef.getScannedVin().equals(null)) {
                data = "* VIN: \n   --Vehicle’s engine management system lacks encoding of vehicle Vin: \n     " + byApprId.getVinNumber() + ". \n   --This is mainly due to age of vehicle.\n";
            } else {
                data = "* VIN: \n   --The ECU vin number does not match the scanned body vin. \n   --This could be the result of an engine swap or at worst a solen vehicle.\n";
            }

        return data;
    }
    private String engineTempCheck(EAppraiseVehicle byApprId, String data){
       int count=0;
       int count1=0;
        List<OBD2_TestDriveMeasurements> testDriveMeas = byApprId.getTestDriveMeas();
        for(OBD2_TestDriveMeasurements test:testDriveMeas){
            if (null!=test.getEngineTempTest()&&!test.getEngineTempTest().equals("")) {
                String[] s = test.getEngineTempTest().split(" ");
                Double value = Double.parseDouble(s[0]);
                if (value >= 195 && value <= 220) {
                    count++;
                } else if (value > 220) {
                    count1++;
                }
            }
        }
        if (count>0&&count1==0){
            data="* Engine Temperature: \n   --Engine temp reading were within normal operating range.\n";
        } else if (count1>0&&count==0) {
            data="* Engine Temperature: \n   --Engine is operating above normal operating range. \n   --Cooling system may need further diagnosis.\n";
        }
        return data;
    }

    private String preStartVoltageCheck(String batteryVoltage, String data){

        if (batteryVoltage != null && !batteryVoltage.isEmpty()) {
            String[] s = batteryVoltage.split(" ");
            double value = Double.parseDouble(s[0]);
            if (value >= 12.2 && value <= 12.7) {
                data = "* PreStart Battery Voltage: \n   --Battery voltage is reading in the normal range. \n   --Indicating a good battery.\n";
            } else if (value >= 11.9 && value < 12.2) {
                data = "* PreStart Battery Voltage: \n   --Battery voltage Low. \n   --This indicate the Battery is in need of replacement in the near future.\n";
            } else if (value < 11.9) {
                data = "* PreStart Battery Voltage: \n   --Battery voltage extremely low. \n   --Battery is on the verge of failure.\n";
            }
        }
        return data;
    }

    private String fuelPressureCheck(String fuelPressure, String data){

        if (fuelPressure != null && !fuelPressure.isEmpty()) {
            String[] s = fuelPressure.split(" ");
            double value = Double.parseDouble(s[0]);
            if (value < 30) {
                data = "* Fuel Pressure: \n   --Fuel pressure is low, vehicle needs to be checked.\n";
            } else if (value > 80) {
                data = "* Fuel Pressure: \n   --Fuel pressure is high, vehicle needs to be checked.\n";
            } else if (value >= 30 && value <= 80) {
                data = "* Fuel Pressure: \n   --Fuel pressure is within normal range.\n";
            }
        }
        return data;
    }

    private String lastMemoryClearCheck(String warmup,String timeSince,String mileSince,String data,PdfDataDto pdfDataDto){
        if ((warmup != null && !warmup.isEmpty())&&(timeSince != null && !timeSince.isEmpty())&&(mileSince != null && !mileSince.isEmpty())) {

            double warmups = Double.parseDouble(warmup);
            String[] s = timeSince.split(" ");
            double timeSin = Double.parseDouble(s[0]);
            String[] s1 = mileSince.split(" ");
            double mileSin = Double.parseDouble(s1[0]);
            if ((warmups <= 5 && timeSin <= 3000 && mileSin <= 15) && (pdfDataDto.getCurrentTroubleCodes() == null && pdfDataDto.getPendingTroubleCodes() == null)) {
                data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                        " warm ups.\n   --This was done Very recently. Ask for reason and service invoice.\n";
            } else if ((warmups <= 5 && timeSin <= 3000 && mileSin <= 15) && (null != pdfDataDto.getPendingTroubleCodes() && pdfDataDto.getCurrentTroubleCodes() == null)) {
                data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                        " warm ups.\n   --This was done Very recently. \n   --There is a DTC pending and will light the check " +
                        "engine light shortly. \n   --Vehicle has an issue.\n";
            } else if ((warmups <= 5 && timeSin <= 3000 && mileSin <= 15) && (null != pdfDataDto.getPendingTroubleCodes() && null != pdfDataDto.getCurrentTroubleCodes())) {
                data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                        " warm ups.\n   --Check engine light on.\n" +
                        "\n   --This was done Very recently. \n   --There is a DTC pending and current. Check engine " +
                        "light should be on.\n   --Vehicle currently has an issue, attempts were made to repair " +
                        "or cover-up issue.\n   --See “trouble code summary” for issue.\n";
            } else if ((warmups > 50 && timeSin > 4500 && (mileSin > 300 && mileSin < 1500)) && (null == pdfDataDto.getPendingTroubleCodes() && null == pdfDataDto.getCurrentTroubleCodes())) {
                data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                        " warm ups.\n   --Vehicle most likely was serviced, resulting in the memory clear.\n   --Both time, miles " +
                        "and cycles have been long enough to be confident in the repair.\n   --May want to " +
                        "enquire of the owner what was repaired.\n";
            } else if ((warmups > 50 && timeSin > 4500 && mileSin > 1500) && null == pdfDataDto.getPendingTroubleCodes() && null == pdfDataDto.getCurrentTroubleCodes()) {
                data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin/60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                        " warm ups.\n";
            } else {
                boolean b = (warmups > 5 && warmups < 50) && (timeSin > 3000 && timeSin < 45000) && (mileSin > 15 && mileSin < 300);
                if (b && (null == pdfDataDto.getPendingTroubleCodes() && null == pdfDataDto.getCurrentTroubleCodes())) {
                    data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                            " warm ups.\n   --Vehicle most likely was serviced, resulting in the memory clear.\n   --Both time, miles " +
                            "and cycles have been long enough to be confident in the repair.\n   --May want to " +
                            "enquire of the owner what was repaired.\n";
                } else if (b && (null != pdfDataDto.getPendingTroubleCodes() || null != pdfDataDto.getCurrentTroubleCodes())) {
                    data = "* Last Memory Clear: \n   --Last memory clear was " + (timeSin / 60) + " hours ago, " + mileSin + " miles ago and " + warmups +
                            " warm ups.\n   --Vehicle most likely was serviced, resulting in the memory clear.\n   --However, Dtc’s are " +
                            "currently showing.\n   --This indicates that either a new issue has arisen or the original " +
                            "issue was not correctly repaired.\n   --Vehicle needs service.\n";
                }
            }
        }


        return data;
    }
    private String runningVoltageCheck(EAppraiseVehicle byApprId, String data){
        int count=0;
        int count1=0;
        int count2=0;
        int count3=0;
        List<OBD2_TestDriveMeasurements> testDriveMeas = byApprId.getTestDriveMeas();
        for (OBD2_TestDriveMeasurements test:testDriveMeas){
            if (null!=test.getVoltage()&&!test.getVoltage().equals("")) {
                double value = Double.parseDouble(test.getVoltage().replace("V", ""));
                if (value >= 13.9 && value < 15) {
                    count++;
                } else if (value > 12 && value < 13.9) {
                    count1++;
                } else if (value < 12) {
                    count2++;
                } else if (value > 16) {
                    count3++;
                }
            }
        }
        if (count==16){
            data="* Running Voltage: \n   --Alternator is operating with in normal specs\n";
        }
        else if (count1==4&&count1<12){
            data="* Running Voltage: \n   --The alternator has recorded measurements to low and outside of normal " +
                    "operating specs.\n";
        } else if (count2 == 0) {
            data="* Running Voltage: \n   --Charging system issue present. Needs servicing. Voltage too low\n";
        } else if (count3 == 0) {
            data="* Running Voltage: \n   --Charging system issue present. Needs servicing. Voltage too High\n";
        } else if (count1>=12) {
            data="* Running Voltage: \n   --Alternator output is low. Replacement needed.\n";
        }
        return data;
    }

    private String oxygenSensorsCheck(EAppraiseVehicle byApprId, String data){
        List<OBD2_TestDriveMeasurements> testDriveMeas = byApprId.getTestDriveMeas();
        int count=0;
        int count1=0;
        int count2=0;
        int count3=0;
        int count4=0;
        for (OBD2_TestDriveMeasurements test:testDriveMeas){
            if ((null!=test.getUpstbk1()&&!test.getUpstbk1().equals(""))&&(null!=test.getUpstbk2()&&!test.getUpstbk2().equals(""))&&(null!=test.getDwnstbk1()&&!test.getDwnstbk1().equals(""))&&(null!=test.getDwnstbk2()&&!test.getDwnstbk2().equals(""))) {
                Double upstbk1 = Double.parseDouble(test.getUpstbk1());
                Double upstbk2 = Double.parseDouble(test.getUpstbk2());
                Double dwnstbk1 = Double.parseDouble(test.getDwnstbk1());
                Double dwnstbk2 = Double.parseDouble(test.getDwnstbk2());
                if ((upstbk1 > 0.44 && upstbk1 < 0.46) && (upstbk2 > 0.44 && upstbk2 < 0.46)) {
                    count++;
                }
                if ((upstbk1 > 0.1 && upstbk1 < 0.52) && (upstbk2 > 0.1 && upstbk2 < 0.52)) {
                    count1++;
                }
                if ((upstbk1 > 0.45 && upstbk1 < 0.9) && (upstbk2 > 0.45 && upstbk2 < 0.9)) {
                    count2++;
                }
                if ((upstbk1 > 0.24 && upstbk1 < 0.76) && (upstbk2 > 0.24 && upstbk2 < 0.76)) {
                    count3++;
                }
                if (dwnstbk1 > 0.3 && dwnstbk2 > 0.3) {
                    count4++;
                }
            }
        }
        if (count==16){
            data="* Oxygen Sensors Check: \n   --Upstream Bk is faulty and needs replacement.\n";
        } else if (count1==16) {
            data="* Oxygen Sensors Check: \n   --Upstream Bk shows a lean condition.\n   --This could indicate a vacuum leak, dirty " +
                    "Maf or fuel system issue.\n";
        } else if (count2==16) {
            data="* Oxygen Sensors Check: \n   --Upstream Bk X shows a rich condition.\n   --This could be caused by a leaking injector" +
                    "dirty Maf or worn piston rings.\n";
        }else if (count3==16) {
            data="* Oxygen Sensors Check: \n   --Upsteam BK X is indicating normal engine operation\n";
        }
        if (count4==16){
            data+="* Oxygen Sensors Check: \n    --Dwnstream Bk indicates a possible bad catalytic converter.";
        }else {
            data+="* Oxygen Sensors Check: \n    --Dwnstream Bk is indicating normal catalytic converter operation";
        }
        return data;
    }

    public String appraisalList(String start,String end) throws IOException, JRException, ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start, end);
        List<AppraisalFormView> appraisalList=appraiseVehiViewRepo.findAllByDate(dates.get(0),dates.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lApprFormViewToPdfDataDto(appraisalList);
        JRBeanCollectionDataSource beanDatasource=new JRBeanCollectionDataSource(pdfDataDtos);
        Resource resource = resourceLoader.getResource("classpath:AppraisalForm.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
        String name = UUID.randomUUID() + AppraisalConstants.pdf;
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath+name );
        return pdfpath+name;

    }
    @Override
    public TableList appraisalListPage(String start,String end ,Integer pageNumber, Integer pageSize) throws ParseException {

        List<Date> dates = compareUtils.StringParseToDate(start, end);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<AppraisalFormView> appraisalList=null;
        Page<AppraisalFormView> all = appraiseVehiViewRepo.findAllByDate(dates.get(0),dates.get(1),pageable);
        appraisalList= all.toList();
        List<PdfDataDto> pdfDataDtos = mapper.lApprFormViewToPdfDataDto(appraisalList);
        TableList tableList=new TableList();
        tableList.setAppraisalList(pdfDataDtos);
        tableList.setTotalPages(all.getTotalPages());
        tableList.setTotalRecords(all.getTotalElements());
        tableList.setStatus(true);
        tableList.setCode(HttpStatus.OK.value());
        return tableList;
    }
    @Override
    public String offerReport(String start, String end) throws IOException, JRException, JDOMException, ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start,end);
        List<OfferPdf> offerList = offerPdfRepo.getOfferList(dates.get(0),dates.get(1));
        List<OfferReport> offerReports = mapper.lOfferPdfToOfferReport(offerList);
        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:OfferReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(offerReports);
        Map<String, Object> parameters = new HashMap<>();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {

            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;

    }

    @Override
    public TableList offerList(Integer pageNumber, Integer pageSize, String start, String end) throws ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start,end);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OfferPdf> offerList = offerPdfRepo.getOffer(dates.get(0), dates.get(1),pageable);
        List<OfferReport> offerReports = mapper.lOfferPdfToOfferReport(offerList.toList());
        TableList tableList= new TableList();
        tableList.setOfferList(offerReports);
        tableList.setTotalPages(offerList.getTotalPages());
        tableList.setTotalRecords(offerList.getTotalElements());
        tableList.setMessage("List found successfully");
        tableList.setCode(HttpStatus.OK.value());
        tableList.setStatus(true);

        return tableList;
    }


    public String totalMemReport() throws IOException, JRException {
        List<TotalMembersView> membersViews = totalMembersRepo.findAll();
        List<FactoryReport> members = vehicleMapper.totalMembersToFactoryRpt(membersViews);
        JRBeanCollectionDataSource beanDatasource=new JRBeanCollectionDataSource(members);
        Resource resource = resourceLoader.getResource("classpath:totalMember.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
        String name = UUID.randomUUID() + AppraisalConstants.pdf;
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath+name );
        return pdfpath+name;
    }
    @Override
    public TableList totalMemReport(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<TotalMembersView> membersViews = totalMembersRepo.findAll(pageable);
        List<TotalMembersView> membersViewsList = membersViews.toList();
        List<FactoryReport> members = vehicleMapper.totalMembersToFactoryRpt(membersViewsList);
        TableList tableList=new TableList();
        tableList.setMembers(members);
        tableList.setTotalPages(membersViews.getTotalPages());
        tableList.setTotalRecords(membersViews.getTotalElements());
        tableList.setStatus(true);
        tableList.setCode(HttpStatus.OK.value());
        return tableList;
    }
    @Override
    public String facSalesReport(UUID fsUserId) throws IOException, JRException {
        List<MembersByFactorySalesmen> factorySalesmen = salesmenRepo.findByFactorySalesman(fsUserId);
        List<FactoryReport> members = vehicleMapper.membersToFactoryRpt(factorySalesmen);
        JRBeanCollectionDataSource beanDatasource=new JRBeanCollectionDataSource(members);
        Resource resource = resourceLoader.getResource("classpath:factorySalesRep.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
        String name = UUID.randomUUID() + AppraisalConstants.pdf;
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath+name );
        return pdfpath+name;
    }
    @Override
    public TableList salesManMemReport(UUID fsUserId,Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MembersByFactorySalesmen> membersViews = salesmenRepo.findByFactorySalesman(fsUserId,pageable);
        List<MembersByFactorySalesmen> membersViewsList = membersViews.toList();
        List<FactoryReport> members = vehicleMapper.membersToFactoryRpt(membersViewsList);
        TableList tableList=new TableList();
        tableList.setMembers(members);
        tableList.setTotalPages(membersViews.getTotalPages());
        tableList.setTotalRecords(membersViews.getTotalElements());
        tableList.setStatus(true);
        tableList.setCode(HttpStatus.OK.value());
        return tableList;
    }

    @Override
    public String facMngReport(UUID fmUserId ) throws IOException, JRException {

        List<MembersByFactoryManager> managerRepoAll = managerRepo.findByFactoryManager(fmUserId);
        List<FactoryReport> members = vehicleMapper.managersMembersToFactoryRpt(managerRepoAll);
        JRBeanCollectionDataSource beanDatasource=new JRBeanCollectionDataSource(members);
        Resource resource = resourceLoader.getResource("classpath:factoryMngReport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDatasource);
        String name = UUID.randomUUID() + AppraisalConstants.pdf;
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfpath+name );
        return pdfpath+name;
    }
    @Override
    public TableList managersMemReport(UUID fmUserId,Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MembersByFactoryManager> membersViews = managerRepo.findByFactoryManager(fmUserId,pageable);
        List<MembersByFactoryManager> membersViewsList = membersViews.toList();
        List<FactoryReport> members = vehicleMapper.managersMembersToFactoryRpt(membersViewsList);
        TableList tableList=new TableList();
        tableList.setMembers(members);
        tableList.setTotalPages(membersViews.getTotalPages());
        tableList.setTotalRecords(membersViews.getTotalElements());
        tableList.setStatus(true);
        tableList.setCode(HttpStatus.OK.value());
        return tableList;
    }


    @Override
    public String soldPdf(String start, String end) throws IOException, JDOMException, JRException, ParseException {
        List<Date> dateList=compareUtils.StringParseToDate(start,end);
        List<TransactionReport> tranList = transReportRepo.getSoldList(dateList.get(0),dateList.get(1));
        List<PdfDataDto> pdfDataDtos1 = mapper.lTransReportToPdfDataDto(tranList);
        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:SoldReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(pdfDataDtos1);
        Map<String, Object> parameters = new HashMap();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {

            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;

    }

    @Override
    public TableList soldList(Integer pageNumber, Integer pageSize,String start, String end) throws ParseException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Date> dateList=compareUtils.StringParseToDate(start,end);
        Page<TransactionReport> tranList = transReportRepo.soldTable(dateList.get(0),dateList.get(1),pageable);
        List<PdfDataDto> pdfDataDtos = mapper.lTransReportToPdfDataDto(tranList.toList());
        TableList tableList= new TableList();
        tableList.setSoldList(pdfDataDtos);
        tableList.setTotalPages(tranList.getTotalPages());
        tableList.setTotalRecords(tranList.getTotalElements());
        tableList.setMessage("List found successfully");
        tableList.setCode(HttpStatus.OK.value());
        tableList.setStatus(true);

        return tableList;
    }


    @Override
    public String trnstionPdf(UUID id,String start, String end) throws IOException, JDOMException, JRException, ParseException {
        List<Date>dateList=compareUtils.StringParseToDate(start,end);
        List<TransactionReport> tranList = transReportRepo.getTranList(id,dateList.get(0),dateList.get(1));
        List<PdfDataDto> pdfDataDtos1 = mapper.lTransReportToPdfDataDto(tranList);
        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:TransactionReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(pdfDataDtos1);
        Map<String, Object> parameters = new HashMap();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {

            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;

    }
    @Override
    public TableList transactionList(UUID id,Integer pageNumber, Integer pageSize, String start, String end) throws ParseException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Date>dateList=compareUtils.StringParseToDate(start,end);
        Page<TransactionReport> tranList = transReportRepo.tranListTable(id,pageable,dateList.get(0),dateList.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lTransReportToPdfDataDto(tranList.toList());
        TableList tableList= new TableList();
        tableList.setSoldList(pdfDataDtos);
        tableList.setTotalPages(tranList.getTotalPages());
        tableList.setTotalRecords(tranList.getTotalElements());
        tableList.setMessage("List found successfully");
        tableList.setCode(HttpStatus.OK.value());
        tableList.setStatus(true);

        return tableList;
    }



    @Override
    public String dlrInvtryPdf(UUID userId,Integer daysSinceInventory,String vehicleMake, Double consumerAskPrice) throws IOException, JRException, JDOMException, TemplateException {

        DlrInvntryPdfFilter dlrInvntryPdfFilter=new DlrInvntryPdfFilter();
        dlrInvntryPdfFilter.setVehicleMake(vehicleMake);
        dlrInvntryPdfFilter.setConsumerAskPrice(consumerAskPrice);
        dlrInvntryPdfFilter.setDaysSinceInventory(daysSinceInventory);
        TableList tableList = filterSpecificationService.sendDlrFilterListPdf(dlrInvntryPdfFilter,userId);
        List<PdfDataDto> dlrInvntryList = tableList.getDlrInvntryList();
        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:DlrInvntryReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dlrInvntryList);
        Map<String, Object> parameters = new HashMap<>();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {

            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;
    }





    @Override
    public String purchasePdf(UUID userId,String start, String end) throws IOException, JRException,  JDOMException, ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start, end);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        List<DlrSalesView> appraisalList= dlrSalesRepo.findByPrchase(allUsersUnderDealer,dates.get(0),dates.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lDlrSalesViewToPdfDataDto(appraisalList);
        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;

        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:PrchseReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(pdfDataDtos);
        Map<String, Object> parameters = new HashMap();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {
            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;
    }

    @Override
    public TableList purchaseList(UUID userId, Integer pageNumber, Integer pageSize, String start, String end) throws ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start, end);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        Page<DlrSalesView> prchsList = dlrSalesRepo.prchaseListTable(allUsersUnderDealer,pageable,dates.get(0),dates.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lDlrSalesViewToPdfDataDto(prchsList.toList());
        TableList tableList= new TableList();
        tableList.setPurchaseList(pdfDataDtos);
        tableList.setTotalPages(prchsList.getTotalPages());
        tableList.setTotalRecords(prchsList.getTotalElements());
        tableList.setMessage("List found successfully");
        tableList.setCode(HttpStatus.OK.value());
        tableList.setStatus(true);
        return tableList;
    }



    @Override
    public String salesPdf(UUID userId,String start, String end) throws IOException, JRException, JDOMException, TemplateException, ParseException {

        List<Date> dates = compareUtils.StringParseToDate(start, end);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        List<DlrSalesView> appraisalList=dlrSalesRepo.findBySellerUserIdSold(allUsersUnderDealer,dates.get(0),dates.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lDlrSalesViewToPdfDataDto(appraisalList);

        String fileName=pdfpath+ UUID.randomUUID() + AppraisalConstants.pdf;
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:SalesReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(pdfDataDtos);
        Map<String, Object> parameters = new HashMap<>();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {

            JasperExportManager.exportReportToPdfFile(jasperPrint,fileName);
        }
        return  fileName;
    }

    @Override
    public TableList salesList(UUID userId, Integer pageNumber, Integer pageSize, String start, String end) throws ParseException {
        List<Date> dates = compareUtils.StringParseToDate(start, end);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        Page<DlrSalesView> saleList = dlrSalesRepo.saleListTable(allUsersUnderDealer,pageable,dates.get(0),dates.get(1));
        List<PdfDataDto> pdfDataDtos = mapper.lDlrSalesViewToPdfDataDto(saleList.toList());
        TableList tableList= new TableList();
        tableList.setSalesList(pdfDataDtos);
        tableList.setTotalPages(saleList.getTotalPages());
        tableList.setTotalRecords(saleList.getTotalElements());
        tableList.setMessage("List found successfully");
        tableList.setCode(HttpStatus.OK.value());
        tableList.setStatus(true);
        return tableList;
    }
}
