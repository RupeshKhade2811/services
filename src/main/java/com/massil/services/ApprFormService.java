package com.massil.services;


import com.massil.dto.ApprFormDto;
import com.massil.dto.ApprFormSelectManyDto;
import com.massil.persistence.model.EAppraiseVehicle;
import freemarker.template.TemplateException;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;


import java.io.IOException;

public interface ApprFormService {

    public String apprFormPdf(ApprFormDto apprFormDto) throws IOException, JRException, JDOMException, JRException, JDOMException;

    public ApprFormDto setDataToPdf(Long apprRefId) throws IOException, TemplateException;

    public ApprFormSelectManyDto setSelectManyData(EAppraiseVehicle eAppraiseVehicles);
}
