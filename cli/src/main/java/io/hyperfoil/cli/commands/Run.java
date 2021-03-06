package io.hyperfoil.cli.commands;

import org.aesh.command.CommandDefinition;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.aesh.command.option.Option;

import io.hyperfoil.cli.context.HyperfoilCommandInvocation;
import io.hyperfoil.controller.Client;
import io.hyperfoil.client.RestClientException;

@CommandDefinition(name = "run", description = "Starts benchmark on Hyperfoil Controller server")
public class Run extends BenchmarkCommand {
   @Option(shortName = 'd', description = "Run description")
   String description;

   @Override
   public CommandResult execute(HyperfoilCommandInvocation invocation) throws CommandException {
      Client.BenchmarkRef benchmarkRef = ensureBenchmark(invocation);
      try {
         invocation.context().setServerRun(benchmarkRef.start(description));
         invocation.println("Started run " + invocation.context().serverRun().id());
      } catch (RestClientException e) {
         invocation.error(e);
         throw new CommandException("Failed to start benchmark " + benchmarkRef.name(), e);
      }
      try {
         invocation.executeCommand("status");
      } catch (Exception e) {
         invocation.error(e);
         throw new CommandException(e);
      }
      return CommandResult.SUCCESS;
   }

}
