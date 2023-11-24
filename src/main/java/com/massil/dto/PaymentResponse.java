package com.massil.dto;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement(name = "nm_response")
public class PaymentResponse {
    private List<TransactionDTO> transaction;
    @XmlElement(name = "transaction")
    public List<TransactionDTO> getTransaction() {
        return transaction;
    }
    public void setTestData(List<TransactionDTO> transaction) {
        this.transaction = transaction;
    }



}
