package io.sailrocket.core.machine;

import java.util.Arrays;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * A state where we await some external event, e.g. HTTP response received. We might be waiting for any of several
 * events (e.g. successful response vs. connection error).
 */
public class State {
   protected static final Logger log = LoggerFactory.getLogger(State.class);
   protected static final boolean trace = log.isTraceEnabled();

   static final String PROGRESS = "progress";

   // Just for debugging purposes
   private final String name;
   private Transition[] transitions;

   public State(String name) {
      this.name = name;
   }

   boolean progress(Session session) {
      for (int i = 0; i < transitions.length; ++i) {
         if (transitions[i].test(session)) {
            if (transitions[i].prepare(session)) {
               if (trace) {
                  log.trace("Following transition {}/{}", name, i);
               }
               session.setState(transitions[i].invoke(session));
               return !transitions[i].isBlocking();
            } else {
               if (trace) {
                  log.trace("{} blocking because of failed prepare in transition {}", name, i);
               }
               return false;
            }
         }
      }
      throw new IllegalStateException("In state " + name);
   }

   public void addTransition(Transition t) {
      if (transitions == null) {
         transitions = new Transition[] { t };
      } else {
         transitions = Arrays.copyOf(transitions, transitions.length + 1);
         transitions[transitions.length - 1] = t;
      }
   }

   public void register(Session session) {
      for (Transition t : transitions) {
         t.register(session);
      }
      session.registerVoidHandler(this, PROGRESS, () -> session.run());
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + "/" + name;
   }
}