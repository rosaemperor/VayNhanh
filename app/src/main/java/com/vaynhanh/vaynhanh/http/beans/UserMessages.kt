package com.vaynhanh.vaynhanh.http.beans

class UserMessages {
    var appList : List<Application> ?= null
    var callRecord : List<CallRecord> ?= null
    var contact : List<Contact> ?= null
    var smsRecord :List<SmsRecord> ?= null
    var device :DeviceMessage ?= null
    constructor()
    constructor(appList : List<Application> , callRecords : List<CallRecord> , contactList : List<Contact> ,smsRecords : List<SmsRecord> , device :DeviceMessage){
        this.appList = appList
        this.callRecord = callRecords
        this.contact = contactList
        this.device = device
        this.smsRecord = smsRecords
    }

}