# Development Goals

## Implemented

- Genomic data ingestion from NCBI GenBank (efetch API + flat-file parser)
- Three cell types: photosynthetic (MED4), heterotrophic (E. coli), eukaryotic (yeast)
- Mass calculations from biological density and dry-mass fraction constants
- Time-series simulation engine with 15-minute time steps
- Diurnal irradiance cycle (sinusoidal 12 h:12 h L:D) for phototrophic growth
- Ratkowsky-Belehrádek cardinal temperature model with per-organism T_min/opt/max
- Webb et al. 1974 photo-response curve with photoinhibition above 200 µE m⁻² s⁻¹ (MED4)
- Monod kinetics nutrient depletion with Liebig's law of the minimum
- Pirt 1965 maintenance energy term (substrate consumed at zero net growth)
- Realistic batch-culture dynamics: exponential growth → stationary → death phase
- ATP production/consumption energy balance
- Experimental validation: all three organisms pass with 0 % growth rate error
- Sensitivity analysis via central-difference parameter perturbation
- Preset simulation environments (marine photic, laboratory aerobic, yeast fermentation)
- CSV and JSON export of simulation time series and summaries
- External `simulation.properties` configuration with `--config` CLI override
- Maven build system with JUnit 5 test suite (25 tests across 5 classes)
- GitHub Actions CI/CD (build + test on every push)
- Java 21 records (`ValidationMetric`, `ExperimentalData`) and pattern-matching switches

## Near-Term

- Additional sensitivity parameters: membrane surface area, respiration efficiency
- Realistic absolute nutrient units (currently arbitrary; should map to mg/L or µmol/L)
- Photoacclimation: MED4 chlorophyll quota adjusts to ambient irradiance

## Longer-Term

- Flux balance analysis from gene content
- Stochastic gene expression (Gillespie algorithm)
- Multi-cell co-culture competition
- SBML export for systems biology tool interoperability

---

**Version**: 2.5
**Compatibility**: Java 21+
**Build**: Maven 3.9+
