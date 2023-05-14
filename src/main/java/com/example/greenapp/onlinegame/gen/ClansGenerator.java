package com.example.greenapp.onlinegame.gen;

import com.example.greenapp.onlinegame.Clan;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.greenapp.onlinegame.Const.*;

public class ClansGenerator {

  private final Random rng;

  public ClansGenerator() {
    this(new Random(829));
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = """
    Yeah it may. That's kind of the point. We want to allow external code to
    control the rng, so that the results are predictable and not repeating
    when creating multiple generators.
    """)
  public ClansGenerator(Random rng) {
    this.rng = rng;
  }

  protected Clan buildClan(int numberOfPlayers, int points) {
    return new Clan(numberOfPlayers, points);
  }

  public final List<Clan> generate() {
    return this.generate(MAX_CLAN_COUNT, MIN_PLAYER_COUNT, MAX_PLAYER_COUNT, MIN_POINTS, MAX_POINTS);
  }

  public final List<Clan> generate(int clanCount, int minNumberOfPlayers, int maxNumberOfPlayers, int minPoints, int maxPoints) {
    var clans = new ArrayList<Clan>(clanCount);
    for (int i = 0; i < clanCount; i++) {
      var numberOfPlayers = this.nextInt(minNumberOfPlayers, maxNumberOfPlayers);
      var points = this.nextInt(minPoints, maxPoints);
      clans.add(this.buildClan(numberOfPlayers, points));
    }
    Collections.shuffle(clans, this.rng);
    return clans;
  }

  private int nextInt(int min, int max) {
    return min == max ? min : rng.nextInt(min, max + 1);
  }
}
