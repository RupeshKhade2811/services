package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.Car;
import com.massil.dto.MarketCheckData;
import com.massil.services.MarketCheckApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class MarketCheckApiServiceImpl implements MarketCheckApiService {
    Logger log = LoggerFactory.getLogger(MarketCheckApiServiceImpl.class);

    @Value("${api_key}")
    private String api_key;
    @Value("${market_check_url}")
    private String marketCheckUrl;
    @Value("${market_check_url_suffix}")
    private String urlSuffix;
    @Value("${host}")
    private String host;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public MarketCheckData getMarketCheckData(String  vin) throws AppraisalException ,WebClientResponseException {

        MarketCheckData carSpecs= null;


            if(vin.length()!=17)
                throw new AppraisalException("Invalid vin number");

            WebClient webClient = WebClient.create();

            carSpecs = webClient.get()
                    .uri(marketCheckUrl+vin+urlSuffix+api_key)
                    .header(AppraisalConstants.HOST,host)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .reduce(DataBuffer::write)
                    .map(buffer -> {
                        InputStream inputStream = buffer.asInputStream() ;
                            // Process the input stream as needed
                        Car data1 = null;

                        try {
                            data1 = objectMapper.readValue(inputStream, Car.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if(null!=data1 && null!= data1.getGeneric()){

                                Object usa = ((ArrayList<?>) ((LinkedHashMap<?,?>) data1.getGeneric().get(0)).get("USA")).get(0);
                                LinkedHashMap<?,?> usa1 = (LinkedHashMap<?,?>) usa;
                                MarketCheckData cardata=new MarketCheckData();
                                cardata.setYear(usa1.get(AppraisalConstants.YEAR).toString());
                                cardata.setMake(usa1.get(AppraisalConstants.MAKE).toString());
                                cardata.setModel(usa1.get(AppraisalConstants.MODEL).toString());
                                cardata.setEngine(usa1.get(AppraisalConstants.ENGINE).toString());
                                cardata.setFuelType(usa1.get(AppraisalConstants.FUEL_TYPE).toString());
                                if (usa1.get(AppraisalConstants.ENGINE) == null||usa1.get(AppraisalConstants.ENGINE).equals(""))
                                    cardata.setEngine(cardata.getFuelType());

                                cardata.setTrim(usa1.get(AppraisalConstants.TRIM).toString());
                                cardata.setVehicleType(usa1.get(AppraisalConstants.VEHICLE_TYPE).toString());
                                cardata.setBodyType(usa1.get(AppraisalConstants.BODY_TYPE).toString());
                                cardata.setBodySubtype(usa1.get(AppraisalConstants.BODY_SUBTYPE).toString());
                                cardata.setDrivetrain(usa1.get(AppraisalConstants.DRIVETRAIN).toString());
                                cardata.setTransmission(usa1.get(AppraisalConstants.TRANSMISSION).toString());
                                cardata.setMessage("Getting MarketCheck Data successfully");
                                cardata.setCode(HttpStatus.OK.value());
                                cardata.setStatus(true);
                                return cardata;
                            }
                            else

                              return new MarketCheckData();

                    })
                    .block();

        if(null!=carSpecs) {
            carSpecs.setSeries(carSpecs.getTrim() + " " + carSpecs
                    .getVehicleType() + " " + carSpecs.
                    getDrivetrain() + " " + carSpecs.getBodyType());
        }

            if(null!=carSpecs && null == carSpecs.getYear())
               return getMarketCheckData2(vin);


        return carSpecs;
    }


    public MarketCheckData getMarketCheckData2(String  vin) throws AppraisalException,WebClientResponseException {

        MarketCheckData carSpecs=null;

            WebClient webClient = WebClient.create();

            carSpecs = webClient.get()
                    .uri(marketCheckUrl+vin+urlSuffix+api_key)
                    .header(AppraisalConstants.HOST,host)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .reduce(DataBuffer::write)
                    .map(buffer -> {
                        try (InputStream inputStream = buffer.asInputStream()) {
// Process the input stream as needed
                            return objectMapper.readValue(inputStream, MarketCheckData.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            DataBufferUtils.release(buffer);
                        }
                    })
                    .block();

            if(null==carSpecs)
                throw new AppraisalException("Data not found for given vin number");


        if(null==carSpecs.getBodySubtype()) carSpecs.setBodySubtype(" ");
        if(null==carSpecs.getVehicleType()) carSpecs.setVehicleType(" ");
        if(null==carSpecs.getEngine()||carSpecs.getEngine().equals("")) carSpecs.setEngine(carSpecs.getFuelType());


        carSpecs.setSeries(carSpecs.getTrim()+" "+carSpecs
                .getVehicleType()+" "+carSpecs.
                getDrivetrain()+" "+carSpecs.getBodyType()+" "+carSpecs.getBodySubtype());
        carSpecs.setMessage("Getting Data Successfully");
        carSpecs.setCode(HttpStatus.OK.value());
        carSpecs.setStatus(true);

        return carSpecs;
    }

}
