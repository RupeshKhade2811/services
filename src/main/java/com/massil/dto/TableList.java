package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TableList extends Response {

    private List<OfferReport> offerList;
    private List<PdfDataDto> soldList;
    private List<PdfDataDto> appraisalList;
    private List<FactoryReport> members;
    private Integer totalPages;
    private Long totalRecords;

    private List<PdfDataDto> salesList;
    private List<PdfDataDto> purchaseList;
    private List<PdfDataDto> dlrInvntryList;

}
