package biological.properties;

/**
 * Properties for respiratory metabolism
 */
public class RespirationProperties {
    private final boolean aerobicRespiration;
    private final boolean anaerobicRespiration;
    private final double respirationEfficiency;
    private final String primaryElectronAcceptor;
    
    public RespirationProperties(boolean aerobic, boolean anaerobic, 
                               double efficiency, String electronAcceptor) {
        this.aerobicRespiration = aerobic;
        this.anaerobicRespiration = anaerobic;
        this.respirationEfficiency = efficiency;
        this.primaryElectronAcceptor = electronAcceptor;
    }
    
    public boolean canPerformAerobicRespiration() { return aerobicRespiration; }
    public boolean canPerformAnaerobicRespiration() { return anaerobicRespiration; }
    public double getRespirationEfficiency() { return respirationEfficiency; }
    public String getPrimaryElectronAcceptor() { return primaryElectronAcceptor; }
    
    public double calculateRespirationRate() {
        double baseRate = 100.0;
        if (aerobicRespiration) baseRate *= 1.5;
        if (anaerobicRespiration) baseRate *= 0.7;
        return baseRate * respirationEfficiency;
    }
}
