package com.example.greenapp.onlinegame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "This is a DTO")
public class Players {
  int groupCount;
  Clans clans;

  public Players() {
  }

  public Players(int groupCount, Clans clans) {
    this.groupCount = groupCount;
    this.clans = clans;
  }

  public int getGroupCount() {
    return groupCount;
  }

  public void setGroupCount(int groupCount) {
    this.groupCount = groupCount;
  }

  public Clans getClans() {
    return clans;
  }

  public void setClans(Clans clans) {
    this.clans = clans;
  }
}
