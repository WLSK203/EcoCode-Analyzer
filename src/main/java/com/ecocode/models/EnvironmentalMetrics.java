package com.ecocode.models;

/**
 * Converts carbon emissions to real-world environmental equivalents
 * for better understanding of environmental impact
 */
public class EnvironmentalMetrics {
    private double carbonEmissionsGrams;
    private double equivalentDrivingMeters;
    private double equivalentTreesNeeded;
    private double equivalentSmartphoneCharges;
    private double equivalentPlasticBags;

    // Constants for conversions
    private static final double CO2_PER_KM_DRIVING = 120.0; // grams CO2 per km (average car)
    private static final double CO2_ABSORBED_PER_TREE_YEAR = 21_000.0; // grams CO2 per year
    private static final double CO2_PER_SMARTPHONE_CHARGE = 8.0; // grams CO2
    private static final double CO2_PER_PLASTIC_BAG = 10.0; // grams CO2

    public EnvironmentalMetrics() {
    }

    /**
     * Calculate all environmental equivalents based on carbon emissions
     * @param carbonGrams Total carbon emissions in grams
     */
    public void calculateEquivalents(double carbonGrams) {
        this.carbonEmissionsGrams = carbonGrams;
        
        // Driving distance equivalent (in meters)
        this.equivalentDrivingMeters = (carbonGrams / CO2_PER_KM_DRIVING) * 1000;
        
        // Trees needed to offset (fraction of tree-year)
        this.equivalentTreesNeeded = carbonGrams / CO2_ABSORBED_PER_TREE_YEAR;
        
        // Smartphone charges equivalent
        this.equivalentSmartphoneCharges = carbonGrams / CO2_PER_SMARTPHONE_CHARGE;
        
        // Plastic bags equivalent
        this.equivalentPlasticBags = carbonGrams / CO2_PER_PLASTIC_BAG;
    }

    /**
     * Get a human-readable description of the environmental impact
     */
    public String getImpactDescription() {
        if (carbonEmissionsGrams < 1.0) {
            return "Minimal environmental impact - Great job!";
        } else if (carbonEmissionsGrams < 10.0) {
            return "Low environmental impact - Good code efficiency!";
        } else if (carbonEmissionsGrams < 50.0) {
            return "Moderate environmental impact - Room for optimization.";
        } else if (carbonEmissionsGrams < 100.0) {
            return "High environmental impact - Optimization recommended!";
        } else {
            return "Very high environmental impact - Urgent optimization needed!";
        }
    }

    // Getters
    public double getCarbonEmissionsGrams() {
        return carbonEmissionsGrams;
    }

    public double getEquivalentDrivingMeters() {
        return equivalentDrivingMeters;
    }

    public String getEquivalentDrivingDescription() {
        if (equivalentDrivingMeters < 1000) {
            return String.format("%.1f meters", equivalentDrivingMeters);
        } else {
            return String.format("%.2f kilometers", equivalentDrivingMeters / 1000);
        }
    }

    public double getEquivalentTreesNeeded() {
        return equivalentTreesNeeded;
    }

    public String getEquivalentTreesDescription() {
        if (equivalentTreesNeeded < 0.001) {
            return String.format("%.4f tree-days", equivalentTreesNeeded * 365);
        } else if (equivalentTreesNeeded < 1.0) {
            return String.format("%.3f tree-years", equivalentTreesNeeded);
        } else {
            return String.format("%.1f tree-years", equivalentTreesNeeded);
        }
    }

    public double getEquivalentSmartphoneCharges() {
        return equivalentSmartphoneCharges;
    }

    public double getEquivalentPlasticBags() {
        return equivalentPlasticBags;
    }

    @Override
    public String toString() {
        return String.format("EnvironmentalMetrics{CO2=%.2fg, driving=%s, trees=%s, phone_charges=%.1f}",
                carbonEmissionsGrams, getEquivalentDrivingDescription(), 
                getEquivalentTreesDescription(), equivalentSmartphoneCharges);
    }
}
