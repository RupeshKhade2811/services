package com.massil.controller;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import com.massil.services.ConfigCodesService;
import com.massil.services.RoleService;
import com.massil.services.impl.IndexingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/configcodes")
@Tag(name = "Configcodes", description = "operation on configCode")

public class ConfigCodesController {

    Logger log = LoggerFactory.getLogger(ConfigCodesController.class);


    @Autowired
    private ConfigCodesService configCodesService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private IndexingService indexingService;

    /**
     * This method saves the config codes
     * @author Rupesh
     * @param configCodes
     * @return
     */

    @Operation(summary = "Add Config Codes in Database")
    @PostMapping("/addConfigCode")
    public ResponseEntity<Response> saveConfigCode(@RequestBody List<ConfigDropDown> configCodes){
        log.info("This method adds Config codes in the DB table");
        String s = configCodesService.addConfigCode(configCodes);
        Response response =new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(s);
        response.setStatus(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * This method sends configCodes list and Dealer user list to ui
     * @param userId This is dealerId in header
     * @author Yogesh Kumar V
     * @return
     */
    @PostMapping("/dropDowns")
    public ResponseEntity<Response> getAllDropDowns(@RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("DROP DOWNS IS ACTIVATED");
        AppraisalConfigs dropDownList = configCodesService.getAppraisalConfigs(userId);
        return new ResponseEntity<>(dropDownList,HttpStatus.OK) ;
    }


    /**
     * This method saves roles
     * @return
     */
        @Operation(summary = "Add roles in Database")
        @PostMapping("/addRole")
        public ResponseEntity<Response> addRoles(@RequestBody Role role){
            log.info("Roles adding method is activated");
            Response response = roleService.addRole(role);
            return new ResponseEntity<>(response,HttpStatus.OK);

    }

    /**
     * This method is used to update ConfigCodes in database
     * @param configCodes
     * @param codeId
     * @return
     * @throws GlobalException
     */
    @Operation(summary = "This method is used to update ConfigCodes in database")
    @PostMapping("/updateconfig")
    public ResponseEntity<Response> updateConfigCode(@RequestBody ConfigDropDown configCodes,@RequestHeader("codeId") Long codeId) throws GlobalException {
            return new ResponseEntity<>(configCodesService.updateConfig(configCodes,codeId),HttpStatus.OK);
    }

    /**
     * This method is used to delete ConfigCodes in database"
     * @param codeId
     * @return
     * @throws GlobalException
     */

    @Operation(summary = "This method is used to delete ConfigCodes in database")
    @PostMapping("/deleteconfig")
    public ResponseEntity<Response> deleteConfigCode(@RequestHeader("codeId") Long codeId) throws GlobalException {
        return new ResponseEntity<>(configCodesService.deleteConfig(codeId),HttpStatus.OK);
    }

    /**
     * This method is used to update role of the user
     * @param role
     * @param roleId
     * @return
     * @throws GlobalException
     */

    @Operation(summary = "This method is used to update Role in database")
    @PostMapping("/updaterole")
    public ResponseEntity<Response> updateUserRole(@RequestBody Role role,@RequestHeader("roleId") Long roleId) throws GlobalException {
        return new ResponseEntity<>(roleService.updateRole(role,roleId),HttpStatus.OK);
    }

    /**
     * This method is used to delete Role in the database
     * @param roleId
     * @return
     * @throws GlobalException
     */
    @Operation(summary = "This method is used to delete role in database")
    @PostMapping("/deleterole")
    public ResponseEntity<Response> deleteUserRole(@RequestHeader("roleId") Long roleId) throws GlobalException {
        return new ResponseEntity<>(roleService.deleteRole(roleId),HttpStatus.OK);
    }

    @Operation(summary = "This method is used to send all filter parameters in dropdowns")
    @PostMapping("/getAllFilterParams")
    public ResponseEntity<FilterDropdowns> getAllFilterParams(@RequestBody FilterParameters filter, @RequestHeader("userId") UUID userId,@RequestParam("module") String module) throws GlobalException, AppraisalException {
        return new ResponseEntity<>(configCodesService.sendFilterParams(filter,userId, module),HttpStatus.OK);
    }
    @Operation(summary= "This method is used to send Appr filter parameters in dropdowns")
    @PostMapping("/getApprFilterParams")
    public ResponseEntity<FilterDropdowns> getAllFilterParams(@RequestBody FilterParameters filter, @RequestHeader("userId") UUID userId) throws GlobalException, AppraisalException {
        return new ResponseEntity<>(configCodesService.appraisalDropdown(filter,userId),HttpStatus.OK);
    }

    @Operation(summary = "This method is used to show drop downs for Role")
    @PostMapping("/roleDropDowns")
    public ResponseEntity<RoleDropDowns> getRoleDropDowns(){
        return new ResponseEntity<>(roleService.getRoleList(),HttpStatus.OK);
    }

    @GetMapping("/indexingService")
    public ResponseEntity<String> indexingService() throws InterruptedException {
       indexingService.initiateIndexing();
        return new ResponseEntity<>("Successful",HttpStatus.OK);

    }


}
