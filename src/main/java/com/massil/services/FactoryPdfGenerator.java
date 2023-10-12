package com.massil.services;

import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.PdfData;
import com.massil.dto.PdfDataDto;
import com.massil.dto.TableList;
import com.massil.persistence.model.EOffers;
import freemarker.template.TemplateException;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public interface FactoryPdfGenerator {
    public String odometerJrXmlToPdf(PdfData pdfData,String name) throws IOException, JRException;
    public String whlSlByOdrPdf(PdfDataDto pdfDataDto,String name) throws IOException, JRException;

    public String vehReportPdf(PdfDataDto pdfDataDto,String name) throws IOException, JRException, JDOMException;
    public String apprReportPdf(String name)throws IOException,JRException,JDOMException;
    public String licenseReportPdf(EOffers offers, String name)throws IOException,JRException,JDOMException;
    public String taxCertificate(EOffers offers,String name)throws IOException,JRException,JDOMException;
    public PdfData setDataOfPdf(Long apprRefId);

    public PdfDataDto setDataToPdf(Long apprRefId);

    public Response pdfTable(Long offerId) throws JRException, IOException, JDOMException, GlobalException;

    public byte[] purchasePdf(UUID userId,String start, String end) throws IOException, JRException, JDOMException, TemplateException, ParseException;
    public TableList purchaseList(UUID userId, Integer pageNumber, Integer pageSize, String start, String end) throws ParseException;


    public byte[] salesPdf(UUID userId,String start, String end) throws IOException, JRException, JDOMException, TemplateException, ParseException;
    public TableList salesList(UUID userId, Integer pageNumber, Integer pageSize, String start, String end) throws ParseException;


    public byte[] dlrInvtryPdf(UUID userId,Long daysSinceInventory,String vehicleMake,Double delrRetlAskPrice,Double consumerAskPrice) throws IOException, JRException, JDOMException, TemplateException;


    PdfDataDto setDataToPdf1(Long apprRefId) throws GlobalException;

    public byte[]  offerReport(String start, String end) throws IOException, JRException, JDOMException, ParseException;

    public byte[] soldPdf(String start, String end) throws IOException, JDOMException, JRException, ParseException;


    public byte[] trnstionPdf(UUID id,String start, String end) throws IOException, TemplateException, JDOMException, JRException, ParseException;

    public TableList soldList(Integer pageNumber, Integer pageSize, String start, String end) throws ParseException;

    public TableList transactionList(UUID id,Integer pageNumber, Integer pageSize,String start, String end) throws ParseException;

    public TableList offerList(Integer pageNumber, Integer pageSize, String start, String end) throws ParseException;

    byte[] appraisalList(String start,String end) throws IOException, JRException, ParseException;
    byte[] totalMemReport() throws IOException, JRException;
    byte[] facSalesReport(UUID fsUserID) throws IOException, JRException;
    byte[] facMngReport(UUID fmUserId ) throws IOException, JRException;
    TableList appraisalListPage(String start, String end , Integer pageNumber, Integer pageSize) throws ParseException;
    TableList totalMemReport(Integer pageNumber, Integer pageSize);
    TableList salesManMemReport(UUID fsUserID,Integer pageNumber, Integer pageSize);
    TableList managersMemReport(UUID fmUserId,Integer pageNumber, Integer pageSize);
}
