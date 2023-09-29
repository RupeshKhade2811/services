package com.massil.ExceptionHandle;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class GlobalException extends  Exception {

    private transient Object object;
    private String methodName;
    private Exception exception;
    private String message;
    public GlobalException(String message) {
        super(message);
    }


    public GlobalException(Object  object,String methodName,String message,Exception exception) {

        this.object=object.toString();
        this.methodName=methodName;
        this.message=message;
        this.exception=exception;
    }


}
