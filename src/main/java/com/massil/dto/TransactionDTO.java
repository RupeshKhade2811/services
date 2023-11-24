package com.massil.dto;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;


import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDTO  {

    @JacksonXmlProperty(localName = "transaction_id")
    private Long transaction_id;
    @JacksonXmlProperty(localName = "condition")
    private String condition;
    @JacksonXmlProperty(localName = "last_name")
    private String last_name;
    @JacksonXmlProperty(localName = "email")
    private String email;
    @JacksonXmlProperty(localName = "customerid")
    private Integer customerId;

    private List<Action>actions;
    @XmlElement(name = "action")
    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
