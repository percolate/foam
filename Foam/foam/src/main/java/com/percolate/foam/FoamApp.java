package com.percolate.foam;

/**
 * Interface required for <code>Application</code> classes to implement in order to use Foam.
 */
public interface FoamApp {

    /**
     * Return stored instance of {@link FoamMain}.
     * @return instance of {@link FoamMain}.
     */
    FoamMain getFoamMain();

}
