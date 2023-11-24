package com.massil.controller;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.CardsPage;
import com.massil.services.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/inventory")
@Tag(name = "Inventory vehicle", description = "Inventory Module")
public class InventoryVehicleController {


    @Autowired
    private InventoryService inventoryService;

    /**
     * This method get all inventory cards
     * @param userId receive from UI
     * @param pageNumber receive from UI
     * @param pageSize receive from UI
     * @return list of inventory cards
     */

    @Operation(summary = "get Inventory cards by user id ")
    @PostMapping("/getInventoryCards")
    public ResponseEntity<CardsPage> getInventoryCards(@RequestHeader("userId") UUID userId, @RequestParam  Integer pageNumber, @RequestParam Integer pageSize) throws AppraisalException {

        CardsPage apv = inventoryService.inventoryCards(userId,pageNumber,pageSize);
        return new ResponseEntity<CardsPage>(apv, HttpStatus.OK);

    }

    /**
     * This method return all the search factory cards
     * @param id receive from UI
     * @param pageNumber receive from UI
     * @param pageSize receive from UI
     * @return list of search factory vehicle
     */

    @Operation(summary = "get SearchFactory cards not by user id ")
    @PostMapping("/getSearchFactory")
    public ResponseEntity<CardsPage> getSearchFactory(@RequestHeader("id") UUID id, @RequestParam Integer pageNumber, @RequestParam Integer pageSize) throws AppraisalException {

        CardsPage sf = inventoryService.searchFactory(id,pageNumber,pageSize);

        return new ResponseEntity<CardsPage>(sf, HttpStatus.OK);

    }
    /**
     * This method will make vehicle as hold
     * @param apprRef receive fromui
     * @return response
     */
    @Operation(summary = "making vehicle as on hold")
    @PostMapping("/holdvehicle")
    public ResponseEntity<Response> makeStatusAsHold(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.holdAppraisal(apprRef);
        return new ResponseEntity<Response>(response,HttpStatus.OK);
    }
    /**
     * This method will make vehicle as unhold
     * @param apprRef receive fromui
     * @return response
     */

    @Operation(summary="unholding the vehicle")
    @PostMapping("/unholdvehicle")
    public ResponseEntity<Response> unholdAppraisal(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.UnHoldAppraisal(apprRef);
        return new ResponseEntity<Response>(response,HttpStatus.OK);
    }

    /**
     * This method will make vehicle sold retail on
     * @param apprRef receive fromui
     * @return response
     */
    @Operation(summary = "making vehicle sold retail on")
    @PostMapping("/soldRetailOn")
    public ResponseEntity<Response> makeStatusSoldRetailOn(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.makeSoldRetailOn(apprRef);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    /**
     * This method will make vehicle sold retail off
     * @param apprRef receive fromui
     * @return response
     */

    @Operation(summary="making vehicle sold retail off")
    @PostMapping("/soldRetailOff")
    public ResponseEntity<Response> makeStatusSoldRetailOff(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.makeSoldRetailOff(apprRef);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    /**
     * This method will make vehicle sold wholesale on
     * @param apprRef receive fromui
     * @return response
     */
    @Operation(summary = "making vehicle sold wholesale on")
    @PostMapping("/soldWholesaleOn")
    public ResponseEntity<Response> makeStatusSoldWholesaleOn(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.makeSoldWholesaleOn(apprRef);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    /**
     * This method will make vehicle sold wholesale off
     * @param apprRef receive fromui
     * @return response
     */

    @Operation(summary="making vehicle sold wholesale off")
    @PostMapping("/soldWholesaleOff")
    public ResponseEntity<Response> makeStatusSoldWholesaleOff(@RequestHeader("apprRef") Long apprRef) throws AppraisalException {
        Response response = inventoryService.makeSoldWholesaleOff(apprRef);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
