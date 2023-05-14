package com.example.greenapp.onlinegame.gen;

import com.example.greenapp.onlinegame.Clans;
import com.example.greenapp.onlinegame.Players;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Random;

import static com.example.greenapp.onlinegame.Const.*;

public class PlayersGenerator {
  private final Random rng;
  private final ClansGenerator clansGenerator;

  public PlayersGenerator() {
    this(new Random(904));
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = """
    Yeah it may. That's kind of the point. We want to allow external code to
    control the rng, so that the results are predictable and not repeating
    when creating multiple generators.
    """)
  public PlayersGenerator(Random rng) {
    this.rng = rng;
    this.clansGenerator = new ClansGenerator(rng);
  }

  public Players generate() {
    var groupCount = rng.nextInt(MIN_GROUP_SIZE, MAX_GROUP_SIZE);
    var clans = this.clansGenerator.generate(MAX_CLAN_COUNT, MIN_PLAYER_COUNT, Math.min(groupCount, MAX_PLAYER_COUNT), MIN_POINTS, MAX_POINTS);
    return new Players(groupCount, new Clans(clans));
  }

}
