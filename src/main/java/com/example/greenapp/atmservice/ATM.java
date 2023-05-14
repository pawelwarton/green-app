package com.example.greenapp.atmservice;

public class ATM {
  int region;
  int atmId;

  public ATM() {
  }

  public ATM(int region, int atmId) {
    this.region = region;
    this.atmId = atmId;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }

  public int getAtmId() {
    return atmId;
  }

  public void setAtmId(int atmId) {
    this.atmId = atmId;
  }
}
