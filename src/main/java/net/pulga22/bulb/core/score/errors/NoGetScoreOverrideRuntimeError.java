package net.pulga22.bulb.core.score.errors;

public class NoGetScoreOverrideRuntimeError extends RuntimeException {

    public NoGetScoreOverrideRuntimeError() {
        super("The score is null. This maybe is because you haven't override the getScore method.");
    }

}
