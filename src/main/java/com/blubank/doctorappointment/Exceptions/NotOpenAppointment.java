package com.blubank.doctorappointment.Exceptions;

public class NotOpenAppointment extends  RuntimeException{

    public NotOpenAppointment(){
        super();
    }

    public NotOpenAppointment(String message){
        super(message);
    }


}
