package net.pulga22.bulb.core.score.errors;

public class NotInitializedRuntimeError extends RuntimeException {

    public NotInitializedRuntimeError() {
        super("The component has been not added to a GreekoScoreboard.");
    }

}
