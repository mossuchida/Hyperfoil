package io.hyperfoil.core.handlers.http;

import org.kohsuke.MetaInfServices;

import io.hyperfoil.api.config.Name;
import io.hyperfoil.api.connection.HttpRequest;
import io.hyperfoil.api.http.HeaderHandler;
import io.hyperfoil.api.statistics.IntValue;

public class CountHeadersHandler implements HeaderHandler {
   @Override
   public void handleHeader(HttpRequest request, CharSequence header, CharSequence value) {
      IntValue custom = request.statistics().getCustom(request.startTimestampMillis(), header, IntValue::new);
      custom.add(1);
   }

   /**
    * Stores number of occurences of each header in custom statistics (these can be displayed in CLI using the <code>stats -c</code> command).
    */
   @MetaInfServices(HeaderHandler.Builder.class)
   @Name("countHeaders")
   public static class Builder implements HeaderHandler.Builder {
      @Override
      public CountHeadersHandler build() {
         return new CountHeadersHandler();
      }
   }
}
