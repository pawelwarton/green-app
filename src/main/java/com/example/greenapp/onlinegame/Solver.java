package com.example.greenapp.onlinegame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.greenapp.onlinegame.Const.MIN_PLAYER_COUNT;

public class Solver {

  public List<? extends List<Clan>> solve(Players players) {
    ArrayList<Clan>[] buckets = new ArrayList[players.groupCount + 1];
    for (var clan : players.clans) {
      var bucket = buckets[clan.numberOfPlayers];
      if (bucket == null) {
        bucket = buckets[clan.numberOfPlayers] = new ArrayList<>();
      }
      bucket.add(clan);
    }
    var pointsComparator = Comparator.comparingInt(Clan::getPoints)
      .thenComparing(Comparator.comparingInt(Clan::getNumberOfPlayers).reversed());
    for (var bucket : buckets) {
      if (bucket != null) {
        bucket.sort(pointsComparator);
      }
    }
    var tree = new Tree(buckets);
    var clansCount = players.clans.size();
    var groupSize = players.groupCount;
    var groups = new ArrayList<ArrayList<Clan>>();
    for (int i = 0; i < clansCount; ) {
      var remainingSize = groupSize;
      var group = new ArrayList<Clan>();
      while (MIN_PLAYER_COUNT <= remainingSize) {
        var clan = tree.max(0, remainingSize + 1);
        if (clan == null) {
          break;
        }
        i++;
        group.add(clan);
        remainingSize -= clan.getNumberOfPlayers();
        tree.remove(clan);
      }
      if (group.isEmpty()) {
        break;
      }
      groups.add(group);
    }
    return groups;
  }

  static class Tree {
    private final int n;
    private final ArrayList<Clan>[] data;

    Tree(ArrayList<Clan>[] buckets) {
      int n = buckets.length;
      ArrayList<Clan>[] data = new ArrayList[n * 2];
      System.arraycopy(buckets, 0, data, n, n);
      for (int i = n - 1; i > 0; i--) {
        data[i] = max(data[2 * i], data[2 * i + 1]);
      }
      this.n = n;
      this.data = data;
    }

    public void remove(Clan clan) {
      var i = n + clan.numberOfPlayers;
      var bucket = data[i];
      bucket.remove(bucket.size() - 1);

      while (i > 1) {
        i >>= 1;
        data[i] = max(data[2 * i], data[2 * i + 1]);
      }
    }

    public Clan max(int from, int until) {
      from += n;
      until += n;
      ArrayList<Clan> max = null;

      while (from < until) {
        if ((from & 1) == 1) {
          max = max(max, data[from]);
          from++;
        }
        if ((until & 1) == 1) {
          until--;
          max = max(max, data[until]);
        }
        from >>= 1;
        until >>= 1;
      }

      return max == null || max.isEmpty() ? null : max.get(max.size() - 1);
    }

    static ArrayList<Clan> max(ArrayList<Clan> left, ArrayList<Clan> right) {
      if (right == null || right.isEmpty()) {
        return left;
      }
      if (left == null || left.isEmpty()) {
        return right;
      }
      var leftBest = left.get(left.size() - 1);
      var rightBest = right.get(right.size() - 1);
      if (leftBest.points == rightBest.points) {
        return leftBest.numberOfPlayers < rightBest.numberOfPlayers ? left : right;
      }
      return leftBest.points > rightBest.points ? left : right;
    }
  }

}
