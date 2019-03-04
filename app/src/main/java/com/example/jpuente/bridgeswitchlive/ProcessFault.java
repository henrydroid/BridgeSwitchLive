package com.example.jpuente.bridgeswitchlive;

import java.util.ArrayList;

/**
 * Created by jpuente on 3/4/2019.
 */

public class ProcessFault {




    public ArrayList<String> updateArrayList(String s1) {

        ArrayList<String> statusArrayList = new ArrayList<>();


        //Check Group 1 fault
        if (checkGroup1Fault(s1) != null) {

            statusArrayList.add(checkGroup1Fault(s1));
        }
        //Check Group 2 fault
        if (checkGroup2Fault(s1) != null) {

            statusArrayList.add(checkGroup2Fault(s1));
        }
        //Check Low-side FET overcurrent
        if (checkLSOvercurrent(s1) != null) {

            statusArrayList.add(checkLSOvercurrent(s1));
        }
        //Check High-side FET overcurrent
        if (checkHSOvercurrent(s1) != null) {

            statusArrayList.add(checkHSOvercurrent(s1));
        }
        //Check if ready
        if (s1.equals("00000001")) {

            statusArrayList.add(" Device Ready");
        }

        return statusArrayList;

    }




    //Check for system level faults
    public String checkGroup1Fault(String s){

        int i =0;
        String sfault;

        if(s.charAt(0)=='1'){
            i += 4;

        }

        if(s.charAt(1)=='1'){
            i +=2;
        }

        if(s.charAt(2)== '1'){
            i += 1;
        }

        //evaluate fault
        switch (i){

            case 0:{
                //no fault at group 1
                sfault = null;

                break;
            }

            case 1: {
                //HV bus over-voltage fault
                sfault = "HV bus OV";

                break;
            }

            case 2: {
                //HV bus under-voltage fault 100%
                sfault = "HV bus UV100%";

                break;

            }

            case 3:{
                //HV bus under-voltage fault 85%
                sfault = "HV bus UV85%";

                break;
            }

            case 4:{
                //HV bus under-voltage fault 70%
                sfault = "HV bus UV70%";

                break;
            }


            case 5:{
                //HV bus under-voltage fault 55%
                sfault = "HV bus UV55%";

                break;
            }


            case 6:{
                //System Thermal Fault
                sfault = "Sys thermal fault";

                break;
            }

            case 7:{
                //Low-side driver not ready fault
                sfault = "LS Driver Fault";

                break;
            }

            default:{

                sfault = null;
                break;
            }

        }


        return sfault;

    }



    //check internal faults
    public String checkGroup2Fault(String s){

        int i =0;
        String sfault;

        if(s.charAt(3)=='1'){
            i += 2;

        }

        if(s.charAt(4)=='1'){
            i +=1;
        }



        switch (i){

            case 0:{
                sfault = null;
                break;
            }

            case 1: {

                //LS FET Thermal Warning
                sfault = "Thermal Warning";
                break;
            }

            case 2: {

                //LS FET Thermal Shutdown
                sfault = "Thermal Shutdown";
                break;
            }

            case 3:{

                //HS Driver not ready fault
                sfault = "HS Driver Fault";
                break;

            }
            default:{

                sfault = null;
                break;
            }

        }

        return sfault;
    }


    //check for Low-side FET overcurrent fault
    public String checkLSOvercurrent(String s){

        String sfault;

        if(s.charAt(5)== '1'){

            //LS FET overcurrent
            sfault = "LS Over-current";
        }
        else {

            sfault = null;
        }

        return sfault;

    }



    //check for High-side FET overcurrent fault
    public  String checkHSOvercurrent(String s){

        String sfault;

        if(s.charAt(6)== '1'){

            //H FET overcurrent
            sfault = "HS Over-current";
        }
        else {

            sfault = null;
        }

        return sfault;

    }



}
