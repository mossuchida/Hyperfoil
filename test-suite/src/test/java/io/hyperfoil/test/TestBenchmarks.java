package io.hyperfoil.test;

import static io.hyperfoil.core.builders.StepCatalog.SC;

import java.util.concurrent.TimeUnit;

import io.hyperfoil.api.config.Benchmark;
import io.hyperfoil.api.config.BenchmarkBuilder;
import io.hyperfoil.api.http.HttpMethod;

public class TestBenchmarks {
   public static BenchmarkBuilder addTestSimulation(BenchmarkBuilder builder, int users, int port) {
      return builder
            .http()
            .host("localhost").port(port)
            .sharedConnections(10)
            .endHttp()
            .addPhase("test").always(users)
            .duration("5s")
            .scenario()
            .initialSequence("test")
            .step(SC).httpRequest(HttpMethod.GET)
            .path("test")
            .sla()
            .addItem()
            .meanResponseTime(10, TimeUnit.MILLISECONDS)
            .limits().add(0.99, TimeUnit.MILLISECONDS.toNanos(100)).end()
            .errorRatio(0.02)
            .window(3000, TimeUnit.MILLISECONDS)
            .endSLA()
            .endList()
            .endStep()
            .endSequence()
            .endScenario()
            .endPhase();
   }

   public static Benchmark testBenchmark(int agents, int port) {
      BenchmarkBuilder benchmarkBuilder = BenchmarkBuilder.builder().name("test");
      for (int i = 0; i < agents; ++i) {
         benchmarkBuilder.addAgent("agent" + i, "localhost", null);
      }
      addTestSimulation(benchmarkBuilder, agents, port);
      return benchmarkBuilder.build();
   }
}
