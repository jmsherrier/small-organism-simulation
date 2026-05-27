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
- Monod kinetics nutrient depletion with Liebig's law minimum factor
- Population stationary phase via substrate saturation
- CSV and JSON export of simulation time series and summaries
- External `simulation.properties` configuration with `--config` CLI override
- Maven build system with JUnit 5 test suite
- GitHub Actions CI/CD (build + test on every push)
- Java 21 records (`ValidationMetric`, `ExperimentalData`) and pattern-matching switches

## Near-Term

- Additional sensitivity parameters: membrane surface area, respiration efficiency
- Tune Monod Ks constants against experimental batch culture data

## Longer-Term

- Flux balance analysis from gene content
- Stochastic gene expression (Gillespie algorithm)
- Multi-cell co-culture competition
- SBML export for systems biology tool interoperability

---

**Version**: 2.4  
**Compatibility**: Java 21+  
**Build**: Maven 3.9+
