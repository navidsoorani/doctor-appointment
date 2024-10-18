package com.blubank.doctorappointment.Exceptions;

public class InvalidDateTimeDoctorSlot extends  RuntimeException{

    public InvalidDateTimeDoctorSlot(){
        super();
    }

    public InvalidDateTimeDoctorSlot(String message){
        super(message);
    }


}
