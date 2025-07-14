package de.uol.pgdoener.th1.application.service.datatable.helper;

import org.springframework.stereotype.Component;

@Component
public class SimilarityChecker {

    private final double THRESHOLD_VALUE = 0.8;

    public boolean check(String name1, String name2) {
        double distance = getCharDistance(name1, name2);
        int maxLength = Math.max(name1.length(), name2.length());

        if (maxLength == 0) {
            return true;
        }

        double similarity = 1.0 - (distance / maxLength);
        return similarity > THRESHOLD_VALUE;
    }

    //private methods//

    private record CharDistanceResult(double distance, boolean isHardError) {
    }

    private double getCharDistance(String s1, String s2) {
        double dp = 0.0;
        int maxLength = Math.max(s1.length(), s2.length());

        int consecutiveHardErrors = 0;

        for (int i = 0; i < maxLength; i++) {
            char c1 = i < s1.length() ? s1.charAt(i) : ' ';
            char c2 = i < s2.length() ? s2.charAt(i) : ' ';

            CharDistanceResult result = smartCharDistance(c1, c2);

            if (result.isHardError()) {
                consecutiveHardErrors++;
                dp += applyPenalty(result.distance(), consecutiveHardErrors);
            } else {
                consecutiveHardErrors = 0;
                dp += result.distance();
            }
        }

        return dp;
    }

    private CharDistanceResult smartCharDistance(char c1, char c2) {
        if (c1 == c2) {
            return new CharDistanceResult(0.0, false);
        }

        if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
            return new CharDistanceResult(0.5, false);
        }

        if (c1 == ' ' || c2 == ' ') {
            return new CharDistanceResult(1.0, false);
        }

        return new CharDistanceResult(1.0, true);
    }

    /**
     * Berechnet die reduzierte Strafe basierend auf der Anzahl aufeinanderfolgender Fehler.
     *
     * @param basePenalty       Der Basisstrafwert (z. B. 1.0 für vollen Fehler)
     * @param consecutiveErrors Anzahl der Fehler in Folge (1 = erster Fehler)
     * @return Angepasste Strafe, ggf. reduziert bei mehreren Folgefehlern
     */
    private double applyPenalty(double basePenalty, int consecutiveErrors) {
        double penaltyFactor = Math.max(0.5, 1.0 - (consecutiveErrors - 1) * 0.1);
        return basePenalty * penaltyFactor;
    }

}
