package com.massil.services.impl;

import com.massil.constants.AppraisalConstants;
import com.massil.dto.ApprFormDto;
import com.massil.dto.ApprFormSelectManyDto;
import com.massil.dto.ConfigDropDown;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EConfigCodes;
import com.massil.repository.AppraiseVehicleRepo;
import com.massil.repository.OffersRepo;
import com.massil.services.ApprFormService;
import com.massil.util.CompareUtils;
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
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApprFormServiceImpl implements ApprFormService {

    Logger log = LoggerFactory.getLogger(ApprFormServiceImpl.class);

    @Autowired
    private AppraiseVehicleRepo appraiseVehicleRepo;
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private AppraiseVehicleServiceImpl appraiseVehicleService;
    @Autowired
    private CompareUtils comUtl;
    @Value("${image_folder_path}")
    private String imageFolderPath;
    @Autowired
    private OffersMapper mapper;

    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;

    @Value("${image_folder_path}")
    private String pllink;

    @Autowired
    private ApplicationContext applicationContext;



    @Override
    public byte[] apprFormPdf(ApprFormDto apprFormDto) throws IOException, JRException, JDOMException {
        ByteArrayOutputStream byteArrayOutputStream=null;
        String fileName = UUID.randomUUID() + AppraisalConstants.pdf;
        List<ApprFormDto> dataList = new ArrayList();
        apprFormDto.setPicLink(this.pllink);
        dataList.add(apprFormDto);

        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        byte[] pdfReport = null;
        Resource resource = applicationContext.getResource("classpath:AppraisalFormJR.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            pdfReport = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(pdfReport);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);

        jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
        if (jasperPrint != null) {
             byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

        }
        return byteArrayOutputStream.toByteArray();
    }




    @Override
    public ApprFormDto setDataToPdf(Long apprRefId) throws IOException, TemplateException {

        EAppraiseVehicle byApprId = appraiseVehicleRepo.getAppraisalById(apprRefId);
        ApprFormDto apprFormPdf = mapper.apprToPdf(byApprId);
        apprFormPdf=mapper.apprToPdfSlctMny(setSelectManyData(byApprId),apprFormPdf);
        return apprFormPdf;
    }


    @Override
    public ApprFormSelectManyDto setSelectManyData(EAppraiseVehicle eAppraiseVehicles){

        Map<String, List<String>> confgMap=null;
        ApprFormSelectManyDto apprFrmPdf = new ApprFormSelectManyDto();


        if (null != eAppraiseVehicles) {

            List<EConfigCodes> apprConfigList = null;

            apprConfigList = appraiseVehicleService.showInEditSetAcCond(eAppraiseVehicles.getTdStatus().getApprVehAcCondn(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetSteroSts(eAppraiseVehicles.getTdStatus().getApprVehStereoSts(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetInteriCond(eAppraiseVehicles.getTdStatus().getApprVehInteriCondn(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetOilCond(eAppraiseVehicles.getTdStatus().getApprVehOilCondn(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetWarnLightsts(eAppraiseVehicles.getTdStatus().getVehDrWarnLightSts(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetBrakingSts(eAppraiseVehicles.getTdStatus().getApprBrakingSysSts(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetEnginePerfor(eAppraiseVehicles.getTdStatus().getApprEnginePer(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetTransmiSts(eAppraiseVehicles.getTdStatus().getApprTransmissionSts(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetSteeringFeel(eAppraiseVehicles.getTdStatus().getSteeringFeel(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetBookAndKeys(eAppraiseVehicles.getTdStatus().getBookAndKeys(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditTireCondition(eAppraiseVehicles.getTdStatus().getApprVehTireCondn(), apprConfigList);
            apprConfigList = appraiseVehicleService.showInEditSetRearWindowDamage(eAppraiseVehicles.getTdStatus().getRearWindow(), apprConfigList);

            if (null != apprConfigList && !apprConfigList.isEmpty()) {
                List<ConfigDropDown> configCodesList = appraisalVehicleMapper.lEConfigCodeToConfigCode(apprConfigList);

                confgMap = configCodesList.stream()
                        .collect(Collectors.groupingBy(ConfigDropDown::getCodeType, Collectors.mapping(ConfigDropDown::getShortDescrip, Collectors.toList())));

                apprFrmPdf.setApprVehInteriCondn(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.INTERIOR_CONDITION)));
                apprFrmPdf.setApprVehOilCondn(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.OIL_CONDITION)));
                apprFrmPdf.setVehDrWarnLightSts(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.DASH_WARN_LIGHTS)));
                apprFrmPdf.setApprVehAcCondn(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.AC_CONDITION)));
                apprFrmPdf.setApprVehStereoSts(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.STEREO_STATUS)));
                apprFrmPdf.setSteeringFeel(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.STEERING_FEEL_STATUS)));
                apprFrmPdf.setBookAndKeys(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.BOOKS_AND_KEYS)));
                apprFrmPdf.setApprBrakingSysSts(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.BRAKE_SYSTEM_FEEL)));
                apprFrmPdf.setApprEnginePer(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.ENGINE_PERFORMANCE)));
                apprFrmPdf.setApprTransmissionSts(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.TRANSMISSION_STATUS)));
                apprFrmPdf.setRearWindow(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.REAR_WINDOW_DAMAGE)));
                apprFrmPdf.setApprVehTireCondn(comUtl.checkDbVariable(confgMap.get(AppraisalConstants.TIRE_CONDITION)));
            }

        }
        return apprFrmPdf;
    }

}
