# Development Goals

## Implemented

- Genomic data ingestion from NCBI GenBank (efetch API + flat-file parser)
- Three cell types: photosynthetic (MED4), heterotrophic (E. coli), eukaryotic (yeast)
- Mass calculations from biological density and dry-mass fraction constants
- Time-series simulation engine with 15-minute time steps
- Diurnal irradiance cycle (sinusoidal 12 h:12 h L:D) for phototrophic growth
- Multi-factor environmental scaling: f(T, pH, salinity, [O₂], irradiance)
- Experimental validation against published growth rates and cell composition
- Sensitivity analysis via central-difference parameter perturbation
- ATP production/consumption energy balance
- Preset simulation environments (marine photic, laboratory aerobic, yeast fermentation)

## Near-Term

- Nutrient depletion model coupling uptake rates to population density
- Population stationary phase via substrate saturation (Monod kinetics)
- Additional sensitivity parameters: membrane surface area, respiration efficiency

## Longer-Term

- Flux balance analysis from gene content
- Stochastic gene expression (Gillespie algorithm)
- Multi-cell co-culture competition
- SBML export for systems biology tool interoperability

---

**Version**: 2.3  
**Compatibility**: Java 21+
