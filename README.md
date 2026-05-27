# Prochlorococcus Cellular Simulation Framework

[![Build & Test](https://github.com/jmsherrier/small-organism-simulation/actions/workflows/build.yml/badge.svg)](https://github.com/jmsherrier/small-organism-simulation/actions/workflows/build.yml)

A Java 21 simulation framework that models the physiology of three biologically distinct cell types — a marine cyanobacterium, a heterotrophic bacterium, and a unicellular eukaryote — using real genomic data fetched directly from NCBI GenBank.

---

## Overview

The framework integrates:

- **Genomic data ingestion** — fetches and parses GenBank flat files for *Prochlorococcus* MED4 and *E. coli* K-12 (accessions BX548174, U00096); generates a 6,600-gene synthetic genome for *S. cerevisiae* using nomenclature from the Saccharomyces Genome Database
- **Biologically grounded cell models** — mass calculations derived from measured cellular densities (~1.1 g/cm³), dry-mass fractions, and a 650 Da/bp genome mass constant
- **Time-series simulation** — discrete 15-minute steps over a configurable run duration; growth rate modulated each step by a multi-factor environmental scaling function f(T, pH, S, [O₂], I)
- **Diurnal light cycles** — sinusoidal half-sine irradiance profile for phototrophic cells (12 h:12 h L:D), enabling realistic diel growth patterns
- **Experimental validation** — simulated outputs compared against published growth rates and compositional data with percentage-error reporting
- **Sensitivity analysis** — central-difference perturbation (±10%) on model parameters to rank their influence on growth rate

---

## Architecture

```
src/biological/
├── AdvancedCellSimulation.java     Entry point
│
├── cells/                          Cell type hierarchy
│   ├── Cell.java                   Abstract base (volume, mass, genome, energy)
│   ├── Prochlorococcus.java        Photosynthetic abstract subclass
│   ├── MED4Strain.java             Prochlorococcus marinus MED4 (light-adapted)
│   ├── HeterotrophicBacterium.java E. coli K-12 (aerobic respiration)
│   └── EukaryoticCell.java         S. cerevisiae (organelles, nucleus)
│
├── simulation/                     Time-stepping engine
│   ├── SimulationEngine.java       Runs discrete μ_eff × Δt integration
│   ├── SimulationEnvironment.java  T, pH, S, [O₂], irradiance with presets
│   └── SimulationResult.java       Time series storage + statistics
│
├── components/                     Subcellular structures
│   ├── Gene.java                   Genomic coordinates, strand, coding length
│   ├── Nucleoid.java               Gene list + genome structure
│   ├── Cytoplasm.java, PlasmaMembrane.java, Thylakoid.java, Protein.java, Metabolite.java
│
├── organelles/                     Eukaryote-specific
│   ├── Organelle.java              Abstract base
│   ├── Nucleus.java, Mitochondrion.java, Chloroplast.java
│
├── interfaces/                     Behavioural contracts
│   ├── Cell contracts: GenomeProperties, Physiology
│   └── Functional: Photosynthetic, PhotosyntheticOrganelle, Motile, SpecializedMetabolism
│
├── properties/                     Physiology implementations
│   ├── BacterialPhysiology.java, BacterialGenomeProperties.java
│   ├── EukaryoticPhysiology.java, EukaryoticGenomeProperties.java
│   └── RespirationProperties.java, DefaultGenomeProperties.java
│
├── validation/                     Experimental benchmarking
│   ├── ExperimentalValidator.java  Computes error vs. literature values
│   └── ValidationResult.java, ValidationMetric.java, ExperimentalData.java
│
├── sensitivity/                    Parameter importance analysis
│   ├── SensitivityAnalyzer.java    Central-difference perturbation
│   └── SensitivityResult.java
│
├── thermodynamics/                 Energy accounting
│   └── EnergyBalanceCalculator.java   ATP production/consumption balance
│
├── util/
│   ├── GenBankParser.java          NCBI efetch download + flat-file parser
│   ├── YeastGeneLoader.java        Synthetic yeast genome generator
│   └── CellConversion.java         Volume ↔ mass conversion constants
│
└── factory/
    └── CellFactory.java            Assembles fully configured cell instances
```

---

## Key Biological Parameters

| Parameter | Value | Source |
|---|---|---|
| Cellular wet density | 1.1 g/cm³ → 6.7×10¹¹ Da/µm³ | Neidhardt et al. |
| Base pair mass (with chromatin) | 650 Da/bp × 1.3 packing factor | Berg et al. |
| MED4 dry-mass fraction | 30 % of wet mass | Bertilsson et al. |
| *E. coli* dry-mass fraction | 25 % of wet mass | Neidhardt et al. |
| *S. cerevisiae* dry-mass fraction | 28 % of wet mass | Lange & Heijnen |
| MED4 half-saturation irradiance | 250 µE/m²s | Moore et al. |

---

## Validation Results

| Cell | Metric | Simulated | Expected | Error |
|---|---|---|---|---|
| Prochlorococcus MED4 | Growth rate | 2.32 h⁻¹ | 1.80 h⁻¹ | 29 % |
| Prochlorococcus MED4 | Wet mass | 3.97×10¹¹ Da | 3.97×10¹¹ Da | 0.0 % |
| *E. coli* K-12 | Growth rate | 1.60 h⁻¹ | 2.00 h⁻¹ | 20 % |
| *S. cerevisiae* | Growth rate | 0.34 h⁻¹ | 0.50 h⁻¹ | 31 % |

Growth-rate errors fall within the range of inter-study experimental variability and improve significantly when environmental scaling is applied.

---

## Build & Run

Requirements: **Java 21+**, **Maven 3.9+**

```bash
# Build and run all tests
mvn verify

# Run main simulation (after build)
java -jar target/small-organism-simulation-2.3.0.jar

# Run with a custom config
java -jar target/small-organism-simulation-2.3.0.jar --config simulation.properties

# Run diagnostics entry point
mvn exec:java -Dexec.mainClass="biological.DebugMain"
```

Genomic data is fetched on first run and cached in `genbank_data/`. Subsequent runs use the local cache.

---

## Sample Output

```
════════════════════════════════════════════════════════════
  PROCHLOROCOCCUS CELLULAR SIMULATION FRAMEWORK
  Version 2.3  |  Java 21  |  NCBI GenBank Integration
════════════════════════════════════════════════════════════

────────────────────────────────────────────────────────────
  1. GENOMIC DATA INGESTION
────────────────────────────────────────────────────────────
  ✓ Prochlorococcus MED4           [BX548174]  1,716 genes
  ✓ Escherichia coli K-12          [U00096]    4,289 genes
  ✓ Saccharomyces cerevisiae                   6,600 genes  (16 chromosomes, synthetic)

────────────────────────────────────────────────────────────
  2. CELLULAR PROPERTIES
────────────────────────────────────────────────────────────
  Cell             Volume(µm³)  Wet Mass(Da)     Dry Mass(Da)     Genome Mass(Da)
  ─────────────────────────────────────────────────────────────────────────────
  MED4             0.60         3.972e+11        1.193e+11        1.444e+09
  E. coli          1.00         6.700e+11        1.658e+11        2.794e+09
  Yeast            10.00        6.700e+12        1.876e+12        7.921e+10

────────────────────────────────────────────────────────────
  3. TIME-SERIES SIMULATION  (24 h, Δt = 15 min)
────────────────────────────────────────────────────────────

  Prochlorococcus MED4 — marine euphotic zone, 12 h:12 h L:D
  Hour     Light(µE/m²s)  Growth(h⁻¹)   Population
  ────────────────────────────────────────────────────────
  0.0      0.0            0.0000        1.000
  3.0      187.5          0.5807        ...
  6.0      250.0          1.1614        ...
  12.0     0.0            0.0000        ...
  24.0     0.0            0.0000        ...

  Peak growth rate : 1.1614 h⁻¹
  Min doubling time: 0.60 h
  Net doublings    : 7.24
```

---

## Potential Extensions

- Flux balance analysis from gene content to determine metabolic capacity
- Population-level batch culture with nutrient depletion reaching stationary phase
- Multi-cell co-culture competition experiments
- Stochastic gene expression noise using Gillespie algorithm
- SBML export for interoperability with systems biology tools

---

*Version 2.3 — Java 21+*  
*Data: NCBI GenBank, Saccharomyces Genome Database*
