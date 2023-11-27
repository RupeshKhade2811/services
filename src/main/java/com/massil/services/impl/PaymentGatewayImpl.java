package com.massil.services.impl;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.config.AuditConfiguration;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.PaymentResponse;
import com.massil.dto.Shipment;
import com.massil.dto.TransactionDTO;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.PaymentGatewayService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayImpl implements PaymentGatewayService {

    Logger log = LoggerFactory.getLogger(PaymentGatewayImpl.class);
    @Autowired
    UserRegistrationRepo userRegistrationRepo;

    @Autowired
    PaymentDetailsRepo paymentDetailsRepo;

    @Autowired
    ConfigCodesRepo configCodesRepo;
    @Autowired
    private AuditConfiguration auditConfiguration;

    @Autowired
    RoleMappingRepo mappingRepo;

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private PaymentSubsRepo paymentSubsRepo;

    @Value("${payment_server}")
    private String paymentServer;
    @Value("${payment_port}")
    private String paymentPort;
    @Value("${payment_path}")
    private String paymentPath;


    //for first time payment
    @Override
    @Transactional
    public Response paymentInfo(UUID userId, String token){
        log.info("paymentInfo method triggered");
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        auditConfiguration.setAuditorName(userById.getUserName());
        List<ERoleMapping> roleMapping = userById.getRoleMapping();
        String roleGroup = roleMapping.get(0).getRole().getRoleGroup();
        EConfigCodes paymentGroup=null;
        Double value;
        if(null!=roleGroup && roleGroup.equalsIgnoreCase("P")){
            paymentGroup = configCodesRepo.byCodeGroup(AppraisalConstants.PRIVATE_USER);
            value=Double.parseDouble(paymentGroup.getShortCode());
        }else{
            paymentGroup = configCodesRepo.byCodeGroup(AppraisalConstants.PAYMENT_DEALER);
            EConfigCodes eConfigCodes = configCodesRepo.byCodeGroup(AppraisalConstants.SUBSCRIPTION);

            int endDay = Integer.parseInt(eConfigCodes.getShortCode());
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
            LocalDate date;
            if(Integer.parseInt(todayDate)>endDay){
                date = LocalDate.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue()+1, endDay);
            }else{
                date = LocalDate.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue(), endDay);
            }
            value = calculateDays(LocalDate.now().toString(), date.toString(), paymentGroup.getShortCode());
        }

        String tokenization=paymentGroup.getShortDescrip();
        String plainId=null;

        if(null!=paymentGroup.getLongCode()){
            plainId=paymentGroup.getLongCode();
        }

        return paymentGeteway(value, plainId, token,tokenization,userById);
    }

    public Response paymentGeteway(Double value,String plainId,String token,String tokenization,EUserRegistration userById){
        log.info("paymentGeteway method triggered");
        HashMap retval = new HashMap<>();
        Response response= new Response();
        try {
            retval = doSale(value, token,plainId,tokenization,userById);
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Success\nTransId: " + retval.get("transactionid") + "\n" +"Amount: "+ retval.get("amount"));
            return response;
        } catch (Exception e) {
            response.setCode(HttpStatus.BAD_GATEWAY.value());
            response.setMessage("Error: " +e.getMessage());
            return response;
        }
    }

    public HashMap doSale(Double value,String token,String plainId,String tokenization,EUserRegistration userById )throws Exception  {
        log.info("doSale method triggered");
        HashMap<String,Object> result = new HashMap<>();
        HashMap<String,String> request = new HashMap<>();
        DecimalFormat form = new DecimalFormat("#.00");
        request.put(AppraisalConstants.AMOUNT, form.format(value));
        request.put(AppraisalConstants.TYPE, AppraisalConstants.SALE);
        request.put(AppraisalConstants.PAYMENT_TOKEN,token);
        request.put(AppraisalConstants.ORDER_ID, LocalTime.now().toString());
        if(userById.getRoleMapping().get(0).getRole().getRoleGroup().equalsIgnoreCase("D")){
            request.put(AppraisalConstants.MERCHANT_DEFINED_FIELD_2, AppraisalConstants.SUBSCRIPTION_FEE);
            request.put(AppraisalConstants.MERCHANT_DEFINED_FIELD_1,AppraisalConstants.DEALER);
            request.put(AppraisalConstants.RECURRING,AppraisalConstants.ADD_SUBSCRIPTION);
            request.put(AppraisalConstants.PLAN_ID,plainId);
            request.put(AppraisalConstants.BILLING_METHOD,AppraisalConstants.RECURRING);
            request.put(AppraisalConstants.CUSTOMER_VAULT,AppraisalConstants.ADD_CUSTOMER);
        }else{
            request.put(AppraisalConstants.MERCHANT_DEFINED_FIELD_1,AppraisalConstants.PRIVATEUSER);
        }
        request.put(AppraisalConstants.FIRST_NAME,userById.getUserName());
        request.put(AppraisalConstants.CUSTOMER_RECEIPT,"true");
        request.put(AppraisalConstants.LAST_NAME,userById.getId().toString());
//        request.put(AppraisalConstants.CUSTOMER_VAULT,AppraisalConstants.ADD_CUSTOMER);
        request.put(AppraisalConstants.INVOICING,AppraisalConstants.ADD_INVOICE);
        if(null!=userById.getEmail()){
            request.put(AppraisalConstants.EMAIL,userById.getEmail());
        }

        String dataOut = prepareRequest(request,tokenization);
        String error = "";
        boolean success = true;
        PaymentDetails paymentDetails= new PaymentDetails();
        try {
            HashMap retval = postForm(dataOut);
            result.put("transactionid", retval.get("transactionid"));
            result.put("amount",value);

            String transacId = (String) result.get("transactionid");

            if(userById.getRoleMapping().get(0).getRole().getRoleGroup().equalsIgnoreCase("D") && (null==paymentSubsRepo.findByUserId(userById.getId()))){
                    PaymentSubs paymentSubs= new PaymentSubs();
                    paymentSubs.setCustomerVaultId(retval.get("customer_vault_id").toString());
                    paymentSubs.setUser(userById);
                    paymentSubsRepo.save(paymentSubs);
            }

            paymentDetails.setUser(userById);
            paymentDetails.setEmail(userById.getEmail());
            paymentDetails.setPlanId(plainId);
            paymentDetails.setAmount(value);
            paymentDetails.setTrxSts(AppraisalConstants.PENDING);
            paymentDetails.setTrxMsg(AppraisalConstants.TRANSACTION_PENDING);
            paymentDetails.setTransacId(transacId);
            paymentDetails.setTrxDate(new Date());
            paymentDetails.setSubscription(true);
            updatPymntTbl(paymentDetails);

        } catch (IOException e) {
            success = false;
            error = "Connect error, " + e.getMessage();
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
        }
        if (!success) {
            paymentDetails.setUser(userById);
            paymentDetails.setEmail(userById.getEmail());
            paymentDetails.setPlanId(plainId);
            paymentDetails.setAmount(value);
            paymentDetails.setTrxSts(AppraisalConstants.FAILED);
            paymentDetails.setTrxMsg(error);
            paymentDetails.setTransacId(null);
            updatPymntTbl(paymentDetails);
            throw new Exception(error);
        }
        return result;
    }

    //saving in database
    public String updatPymntTbl( PaymentDetails paymentDetails){
        log.info("updatingPaymentTable method triggered");
        ERoleMapping role = mappingRepo.findByUserId(paymentDetails.getUser().getId());
        if(role.getRole().getRoleGroup().equalsIgnoreCase("P")){
            paymentDetails.setTrxDate(new Date());
            paymentDetails.setTransacId(paymentDetails.getTransacId());
            paymentDetails.setConsumeQuota(0);
            paymentDetails.setTotalQuota(AppraisalConstants.TOTAL_QUOTA);
        }
        paymentDetailsRepo.save(paymentDetails);
        return "success";
    }
    // Utility Functions
    public String prepareRequest(HashMap request,String tokenization) {
        log.info("prepareRequest method triggered");
        if (request.size() == 0) {
            return "";
        }
        request.put("security_key", tokenization);
        Set s = request.keySet();
        Iterator i = s.iterator();
        Object key = i.next();
        StringBuffer buffer = new StringBuffer();
        buffer.append(key).append("=")
                .append(URLEncoder.encode((String) request.get(key)));
        while (i.hasNext()) {
            key = i.next();
            buffer.append("&").append(key).append("=")
                    .append(URLEncoder.encode((String) request.get(key)));
        }
        return buffer.toString();
    }
    protected HashMap postForm(String data) throws Exception {
        log.info("postForm method triggered");
        HashMap result = new HashMap();
        HttpURLConnection postConn;
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        URL post = new URL("https", paymentServer, Integer.parseInt(paymentPort), paymentPath);
        postConn = (HttpURLConnection) post.openConnection();
        postConn.setRequestMethod("POST");
        postConn.setDoOutput(true);
        PrintWriter out = new PrintWriter(postConn.getOutputStream());
        out.print(data);
        out.close();
        BufferedReader in =
                new BufferedReader(new InputStreamReader(postConn.getInputStream()));
        String inputLine;
        StringBuffer buffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        String response = buffer.toString();
        result.put("response", response);
        // Parse Result
        StringTokenizer st = new StringTokenizer(response, "&");
        while (st.hasMoreTokens()) {
            String varString = st.nextToken();
            StringTokenizer varSt = new StringTokenizer(varString, "=");
            if (varSt.countTokens() > 2 || varSt.countTokens() < 1) {
                throw new Exception("Bad variable from processor center: " + varString);
            }
            if (varSt.countTokens() == 1) {
                result.put(varSt.nextToken(), "");
            } else {
                result.put(varSt.nextToken(), varSt.nextToken());
            }
        }
        if (result.get("response") == "") {
            throw new Exception("Bad response from processor center" + response);
        }
        if (!result.get("response").toString().equals("1")) {
            throw new Exception(result.get("responsetext").toString());
        }
        return result;
    }

    //to calculate the days
    public Double calculateDays(String startDateStr,String recurringDate,String amount){
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(recurringDate);
        long noOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        return (Double.parseDouble(amount)/30)*(noOfDays);
    }
    @Override
    public void onceInADay() throws AppraisalException, JAXBException {
        log.info("onceInADay() method started..");
//        EConfigCodes eConfigCodes = configCodesRepo.byCodeGroup("SUBSCRIPTION_DATE");
        LocalDate current = LocalDate.now();
       LocalDate startDate = LocalDate.of(current.getYear(), current.getMonthValue(),current.getDayOfMonth()-1 );
       LocalDate endDate = LocalDate.of(current.getYear(), current.getMonthValue(),current.getDayOfMonth());



        getPaymentDetls(startDate,endDate);
    }
    public Response getPaymentDetls(LocalDate startDate,LocalDate endDate) throws JAXBException, AppraisalException {
        log.info("getPaymentDetls method started");
        // Set the request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(AppraisalConstants.SECURITY_KEY, AppraisalConstants.KEY);
        params.add(AppraisalConstants.START_DATE,startDate.toString());
        params.add(AppraisalConstants.END_DATE,endDate.toString());

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the request entity with headers and parameters
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Send the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AppraisalConstants.PYMNT_RECURNG_URL, requestEntity, String.class);
        PaymentResponse dto=null;
        JAXBContext jaxbContext = JAXBContext.newInstance(PaymentResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if(null!=responseEntity.getBody()){
            dto = (PaymentResponse) unmarshaller.unmarshal(new StringReader(responseEntity.getBody()));

            return trxDtsToDb(dto.getTransaction());
        }else throw new AppraisalException("No record for this dates");
    }

    public Response trxDtsToDb(List<TransactionDTO> response) throws AppraisalException {

        log.info("trxDtsToDb method started");
        int totalPaymentDetails = 0;

            for (int i = 0; i <= response.size(); i = i + 100) {

                if (response.size() - i < 100) {
                    List<TransactionDTO> transactionOnSize = response.subList(i, response.size());
                    List<PaymentDetails> paymentDetails = processRecords(transactionOnSize);
                    totalPaymentDetails += paymentDetails.size();

                } else {
                    List<TransactionDTO> transactionDTOS100 = response.subList(i, i + 99);
                    List<PaymentDetails> paymentDetails = processRecords(transactionDTOS100);
                    totalPaymentDetails += paymentDetails.size();
                }
            }
 			Response res = new Response();
            if (response.size() == totalPaymentDetails) {
                res.setMessage("Data updated successfully");
                res.setCode(HttpStatus.OK.value());
            } else throw new RuntimeException("Data update fail");
        
        return  res;
    }

    @Transactional
    private List<PaymentDetails>  processRecords(List<TransactionDTO> response){

        log.info("processRecords method started");
        Map<Long,String>tranAndStatus= new HashMap<>();
        List<String>trnxId = new ArrayList();

        for(TransactionDTO dto:response) {
            trnxId.add(dto.getTransaction_id().toString());
            tranAndStatus.put(dto.getTransaction_id(),dto.getCondition());
        }

        List<PaymentDetails> byTransactIds = paymentDetailsRepo.findByTransacIds(trnxId);
        if(!byTransactIds.isEmpty()){
            for (PaymentDetails pd:byTransactIds) {
                Long id = Long.parseLong(pd.getTransacId());
                for (Map.Entry<Long, String> map : tranAndStatus.entrySet()) {
                    if(map.getKey().equals(id)){
                        pd.setTrxSts(map.getValue());
                    }
                }
            }
        }

        List<PaymentDetails> paymentDetails = paymentDetailsRepo.saveAll(byTransactIds);

        if(!paymentDetails.isEmpty()){
            log.info("Data updated successfully");
        }
        return paymentDetails;
    }

    @Override
    public void feePaymentService(Shipment shipment, Long shipId) throws Exception {
        log.info("feePaymentService method started");
        EShipment byShipId = shipmentRepo.findByShipId(shipId);
        if(byShipId.getBuyerAgreed().equals(true) && byShipId.getSellerAgreed().equals(true)){

            ERoleMapping buyerRole = mappingRepo.findByUserId(byShipId.getBuyerUserId().getId());
            ERoleMapping sellerRole = mappingRepo.findByUserId(byShipId.getSellerUserId().getId());

            if(!buyerRole.getRole().getRoleGroup().equalsIgnoreCase("P")){

                UUID dealerAdminId=null;

                if(buyerRole.getRole().getRole().equalsIgnoreCase("D1")){
                    dealerAdminId=shipment.getBuyerUserId();
                }else{
                    dealerAdminId=buyerRole.getDealerAdmin();
                }
                PaymentSubs dlrAdminId = paymentSubsRepo.findByUserId(dealerAdminId);
                feePayment(shipment.getBuyerUserId(), Double.parseDouble(configCodesRepo.getFee(AppraisalConstants.BUY_FEE).toString()),dlrAdminId.getCustomerVaultId());
            }
            if(!sellerRole.getRole().getRoleGroup().equalsIgnoreCase("P")){
                UUID dealerAdminId=null;
                if(sellerRole.getRole().getRole().equalsIgnoreCase("D1")){
                    dealerAdminId=shipment.getSellerUserId();
                }else{
                    dealerAdminId=sellerRole.getDealerAdmin();
                }
                PaymentSubs dlrAdminId = paymentSubsRepo.findByUserId(dealerAdminId);
                feePayment(shipment.getSellerUserId(), Double.parseDouble(configCodesRepo.getFee(AppraisalConstants.SALE_FEE).toString()),dlrAdminId.getCustomerVaultId());
            }

        }
        Response response= new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Fee deducted");
    }

    public String feePayment(UUID userId,Double fee,String vaultId) throws Exception {
        log.info("feePayment method triggered");

        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        HashMap result = new HashMap();
        HashMap request = new HashMap();

        DecimalFormat form = new DecimalFormat("#.00");
        request.put(AppraisalConstants.AMOUNT, form.format(fee));
        request.put(AppraisalConstants.TYPE, AppraisalConstants.SALE);
        request.put("source_transaction_id","customer_vault");
        if (userById.getRoleMapping().get(0).getRole().getRoleGroup().equalsIgnoreCase("D")) {
            request.put(AppraisalConstants.MERCHANT_DEFINED_FIELD_2, "shipment");
            request.put(AppraisalConstants.MERCHANT_DEFINED_FIELD_1, AppraisalConstants.DEALER);
        }
        request.put(AppraisalConstants.FIRST_NAME, userById.getUserName());
        request.put(AppraisalConstants.CUSTOMER_RECEIPT, "true");
        request.put(AppraisalConstants.LAST_NAME, userById.getId().toString());
        request.put(AppraisalConstants.CUSTOMER_VAULT, AppraisalConstants.UPDATE_CUSTOMER);
        request.put(AppraisalConstants.INVOICING, AppraisalConstants.ADD_INVOICE);
        request.put(AppraisalConstants.CUSTOMER_VAULT_ID,vaultId);
        if(null!=userById.getEmail()){
            request.put(AppraisalConstants.EMAIL, userById.getEmail());
        }
        EConfigCodes paymentGroup = configCodesRepo.byCodeGroup(AppraisalConstants.PAYMENT_DEALER);
        String data_out = prepareRequest(request, paymentGroup.getShortDescrip());
        String error = "";
        String data_in = "";
        boolean success = true;

        try {
            HashMap retval = postForm(data_out);
            data_in = (String) retval.get("response");
            result.put("transactionid", retval.get("transactionid"));
            return retval.get("transactionid").toString();
        } catch (IOException e) {
            success = false;
            error = "Connect error, " + e.getMessage();
        } catch (Exception e) {
            success = false;
            error = e.getMessage();
        }
        if (!success) {
            throw new Exception(error);
        }
        return "";

    }

    @Override
     public void onceInAMonth() throws AppraisalException, JAXBException, ParseException {
         log.info("onceInAMonth() method started..");
         EConfigCodes eConfigCodes = configCodesRepo.byCodeGroup("SUBSCRIPTION_DATE");
         YearMonth currentYearMonth = YearMonth.now();
         LocalDate startDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), Integer.parseInt(eConfigCodes.getShortCode()));
         LocalDate endDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), Integer.parseInt(eConfigCodes.getShortCode())+1);
         PaymentResponse pymntRspons = pymntDtlsOnceInMonth(startDate.toString(), endDate.toString());
         List<TransactionDTO> transaction = pymntRspons.getTransaction();

         List<PaymentDetails> pymntDts=new ArrayList<>();
             for (TransactionDTO tDto : transaction) {

                 PaymentDetails pymtDtls = new PaymentDetails();

                 pymtDtls.setEmail(tDto.getEmail());
                 pymtDtls.setTrxSts(tDto.getCondition());
                 pymtDtls.setPlanId("KA0001");

                 Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(tDto.getActions().get(0).getDate());
                 SimpleDateFormat sdfDes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                 pymtDtls.setTrxDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(sdfDes.format(date1)));
                 pymtDtls.setAmount(tDto.getActions().get(0).getAmount());
                 pymtDtls.setSubscription(true);
                 pymtDtls.setUser(userRegistrationRepo.findUserById(UUID.fromString(tDto.getLast_name())));

                 pymntDts.add(pymtDtls);
             }
             paymentDetailsRepo.saveAll(pymntDts);
     }
    public PaymentResponse pymntDtlsOnceInMonth(String startDate,String endDate) throws JAXBException, ParseException, AppraisalException {
        // Set the request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(AppraisalConstants.SECURITY_KEY, AppraisalConstants.KEY);
        params.add(AppraisalConstants.SOURCE, AppraisalConstants.RECURRING);
        params.add(AppraisalConstants.START_DATE,startDate);
        params.add(AppraisalConstants.END_DATE,endDate);

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the request entity with headers and parameters
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Send the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AppraisalConstants.PYMNT_RECURNG_URL, requestEntity, String.class);
        PaymentResponse dto=null;
        JAXBContext jaxbContext = JAXBContext.newInstance(PaymentResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if(null!=responseEntity.getBody()){
            dto = (PaymentResponse) unmarshaller.unmarshal(new StringReader(responseEntity.getBody()));

            return dto;
        }else throw new AppraisalException("No record for this dates");
    }




}
