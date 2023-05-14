package com.example.greenapp.onlinegame;

public class Clan {
  int numberOfPlayers;
  int points;

  public Clan() {
  }

  public Clan(int numberOfPlayers, int points) {
    this.numberOfPlayers = numberOfPlayers;
    this.points = points;
  }

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }

  public void setNumberOfPlayers(int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  @Override
  public String toString() {
    return "{numberOfPlayers=" + numberOfPlayers + ",points=" + points + '}';
  }
}
