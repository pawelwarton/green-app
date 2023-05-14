package com.example.greenapp.onlinegame;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.algorithms.Algorithms;
import com.example.greenapp.algorithms.ArrayIterator;
import com.example.greenapp.annotations.Unsafe;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import static com.example.greenapp.onlinegame.Const.MAX_CLAN_COUNT;

public class Clans implements Iterable<Clan> {
  private final Clan[] clans = new Clan[MAX_CLAN_COUNT];
  private int size;

  public Clans() {
  }

  public Clans(Collection<Clan> clans) {
    this.size = clans.size();
    clans.toArray(this.clans);
  }

  /**
   * This method might create a new clan object or reuse an old object.
   * Those old objects should not be stored elsewhere.
   */
  @Unsafe
  public void add(int numberOfPlayers, int points) {
    if (size == MAX_CLAN_COUNT) {
      throw new BadRequestException("Too many clans");
    }
    var clan = clans[size++];
    if (clan == null) {
      clan = clans[size - 1] = new Clan();
    }
    clan.setNumberOfPlayers(numberOfPlayers);
    clan.setPoints(points);
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void clear() {
    size = 0;
  }

  public void sort(Comparator<? super Clan> c) {
    Algorithms.sort(this.clans, 0, this.size, c);
  }

  @Override
  public Iterator<Clan> iterator() {
    return new ArrayIterator<>(clans, 0, size);
  }

}
