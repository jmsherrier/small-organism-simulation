# Prochlorococcus Cellular Simulation Framework

[![Build & Test](https://github.com/jmsherrier/small-organism-simulation/actions/workflows/build.yml/badge.svg)](https://github.com/jmsherrier/small-organism-simulation/actions/workflows/build.yml)

A Java 21 simulation framework that models the physiology of three biologically distinct cell types — a marine cyanobacterium, a heterotrophic bacterium, and a unicellular eukaryote — using real genomic data fetched directly from NCBI GenBank.

All cellular parameters are calibrated against published experimental literature. The simulation reproduces classic batch-culture and diurnal growth curves and predicts peak effective growth rates within ~1–22 % of independent published time-series measurements (see [Validation Results](#validation-results)).

---

## Overview

The framework integrates:

- **Genomic data ingestion** — fetches and parses GenBank flat files for *Prochlorococcus* MED4 and *E. coli* K-12 (accessions BX548174, U00096); generates a 6,600-gene synthetic genome for *S. cerevisiae* using nomenclature from the Saccharomyces Genome Database
- **Biologically grounded cell models** — mass calculations derived from measured cellular densities (~1.1 g/cm³), literature-tuned dry-mass fractions, and a 650 Da/bp genome mass constant
- **Time-series simulation** — discrete 15-minute steps; growth rate at each step is `μ_max · f_env(T,pH,S,O₂,I) · f_Monod(nutrients) − m_S`
- **Cardinal temperature kinetics** — Ratkowsky-Belehrádek model (asymmetric bell with sharp drop above T_opt) per organism
- **Monod nutrient kinetics with Liebig's law** — stoichiometric depletion each step; transitions reproduce real stationary/death-phase behaviour
- **Pirt 1965 maintenance energy** — substrate consumed at `m_S` even when growth is zero; produces realistic post-exhaustion biomass decline
- **Diurnal light cycles** — half-sine irradiance with Webb et al. 1974 P-I curve and photoinhibition above 200 µE m⁻² s⁻¹
- **CSV / JSON export** — time series and summary statistics written to `output/` with timestamped filenames
- **Experimental validation** — simulated outputs compared against published rates and compositional data with percentage-error reporting
- **Sensitivity analysis** — central-difference perturbation (±10%) on model parameters

---

## Validation Results

The validation compares the simulation's **predicted** time-series output against **independent** published measurements — the comparison values come from research groups and experimental systems different from those used to set the simulation's input parameters. (Earlier versions had tautological validation that compared a stored μ_max constant against itself, always producing 0 % error; that has been removed.)

| Cell | Peak effective μ (sim → published) | Source | Error |
|---|---|---|---|
| *Prochlorococcus* MED4 | 0.043 → 0.055 h⁻¹ | Vaulot et al. 1995 — in situ N. Pacific gyre midday | **22.4 %** |
| *E. coli* K-12 | 1.64 → 1.98 h⁻¹ | Sezonov et al. 2007 — LB doubling time 21 min | **17.0 %** |
| *S. cerevisiae* | 0.456 → 0.45 h⁻¹ | Verduyn et al. 1990 — batch glucose μ_max | **1.4 %** |

These are real prediction errors. They reflect the simulation's coarse-grained nature (no explicit enzyme kinetics, no flux balance, no photoacclimation) and the fact that input μ_max values are taken from different studies and conditions than the comparison rates. The yeast result is unusually close because both our μ_max and the comparison value come from the same body of batch-fermentation literature.

Compositional predictions (cellular dry mass from the multi-component sum vs. an empirical bulk dry-mass fraction; genome mass from parsed GenBank CDS lengths vs. an average gene length estimate) are also reported with errors of 0.3–12.5 %.

### Key Biological Parameters

| Parameter | Value | Source |
|---|---|---|
| μ_max — *Prochlorococcus* MED4 | 0.15 h⁻¹ | Partensky et al. 1999; Rocap et al. 2003 |
| μ_max — *E. coli* K-12 | 1.7 h⁻¹ | Monod 1949; Bremer & Dennis 1996 |
| μ_max — *S. cerevisiae* | 0.5 h⁻¹ | Verduyn et al. 1990 |
| Monod K_s — MED4 N / P / Fe | 0.5 µM / 0.03 µM / 1 pM | Cavender-Bares et al. 2001 |
| Monod K_s — *E. coli* C / N | 1.0 / 0.5 mg/L | Shehata & Marr 1971 |
| Monod K_s — yeast glucose / N | 1.0 / 0.5 mg/L | Postma et al. 1989 |
| Maintenance m_S — MED4 / *E. coli* / yeast | 0.005 / 0.04 / 0.015 h⁻¹ | Geider & Osborne 1989; Pirt 1965; Verduyn 1990 |
| Cardinal temps — MED4 (T_min/opt/max) | 13 / 24 / 30 °C | Johnson et al. 2006; Zinser et al. 2007 |
| Cardinal temps — *E. coli* | 7.5 / 37 / 49 °C | Ratkowsky et al. 1983 |
| Cardinal temps — yeast | 4 / 30 / 42 °C | Salvadó et al. 2011 |
| Dry-mass fraction — MED4 / *E. coli* / yeast | 33 / 30 / 28 % | Bertilsson 2003; Bremer & Dennis 1996; Albers 1996 |
| Photosynthesis I_k (MED4) | 120 µE m⁻² s⁻¹ | Moore et al. 1995 |

---

## Build & Run

Requirements: **Java 21+**, **Maven 3.9+**

```bash
# Build and run all 25 JUnit tests
mvn verify

# Run main simulation
java -jar target/small-organism-simulation-2.3.0.jar

# Run with custom config
java -jar target/small-organism-simulation-2.3.0.jar --config simulation.properties
```

Genomic data is fetched on first run and cached in `genbank_data/`. CSV/JSON output is written to `output/` with timestamped filenames. Both directories are git-ignored.

---

## Sample Output

```
════════════════════════════════════════════════════════════
  PROCHLOROCOCCUS CELLULAR SIMULATION FRAMEWORK
  Version 2.5  |  Java 21  |  NCBI GenBank Integration
════════════════════════════════════════════════════════════

  Prochlorococcus MED4 — marine euphotic zone, 12 h:12 h L:D
  Hour     Light(µE/m²s)  Growth(h⁻¹)    Population
  ────────────────────────────────────────────────────
  0.0      0.0            -0.0050        0.999      ← dark, maintenance respiration
  6.0      250.0          0.0424          1.226      ← peak midday irradiance
  12.0     0.0           -0.0050          1.460      ← sunset
  24.0     0.0           -0.0050          1.375      ← net 0.46 doublings/day ✓ Partensky 1999

  Escherichia coli K-12 — laboratory aerobic, 37 °C
  0.0      0.0            1.6432          1.508      ← lag → exponential
  3.0      0.0            1.6317          207.165
  4.5      0.0           -0.0400          428.336    ← carbon exhausted (Monod K_s)
  12.0     0.0           -0.0400          317.319    ← death phase at maintenance rate
  Peak doubling time: 25 min ✓ Bremer & Dennis 1996

  Saccharomyces cerevisiae — batch fermentation, 30 °C
  0.0      0.0            0.3880          1.102
  15.0     0.0            0.3501          359.183    ← glucose limitation begins
  18.0     0.0           -0.0150          367.295    ← stationary → death
  Peak doubling time: 1.79 h ✓ Verduyn 1990

  [E. coli] peak_effective_growth_h: simulated=1.643, expected=1.980, error=17.0%
            (Sezonov et al. 2007 — LB doubling time 21 min)
  Overall validation: PASS
```

---

## Architecture

```
src/main/java/biological/
├── AdvancedCellSimulation.java     Entry point
│
├── cells/                          Cell type hierarchy (Cell, Prochlorococcus,
│                                   MED4Strain, HeterotrophicBacterium, EukaryoticCell)
├── simulation/                     Time-stepping engine
│   ├── SimulationEngine.java         μ_eff = μ_max × f_env × f_Monod − m_S
│   ├── MonodKinetics.java            Liebig minimum nutrient limitation
│   ├── SimulationEnvironment.java    Diurnal light + nutrient pools
│   ├── SimulationResult.java         Time series + CSV/JSON export
│   └── SimulationConfig.java         --config CLI override
│
├── properties/                     Physiology implementations
│   ├── BacterialPhysiology.java, EukaryoticPhysiology.java
│   ├── RespirationProperties.java
│   └── CardinalTemperatureModel.java Ratkowsky-Belehrádek (1983)
│
├── components/                     Subcellular structures
├── organelles/                     Eukaryote-specific
├── interfaces/                     Behavioural contracts
├── validation/                     Records-based experimental benchmarking
├── sensitivity/                    Central-difference parameter perturbation
├── thermodynamics/                 ATP production/consumption balance
├── util/                           GenBank parser, mass conversions
└── factory/                        Cell assembly

src/test/java/biological/           5 JUnit 5 test classes, 25 tests total
```

---

## Potential Extensions

- Flux balance analysis from gene content to determine metabolic capacity
- Multi-cell co-culture competition experiments
- Stochastic gene expression noise using the Gillespie algorithm
- SBML export for interoperability with systems biology tools

---

*Version 2.5 — Java 21+ — Maven 3.9+*
*Data: NCBI GenBank, Saccharomyces Genome Database*
