package com.example.greenapp.atmservice;

public class Task extends ATM {

  RequestType requestType;

  public Task() {
  }

  public Task(int region, int atmId, RequestType requestType) {
    super(region, atmId);
    this.requestType = requestType;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }
}
