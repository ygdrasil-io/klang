

package jclang;

public class IndexException extends RuntimeException {
    private final int exitCode;

    public IndexException(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public String getMessage() {
        return "Indexing failed with exit code " + exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
