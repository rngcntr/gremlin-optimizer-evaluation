# gremlin-optimizer-evaluation
This repository contains simulated statistics for the [gremlin-optimizer](https://github.com/rngcntr/gremlin-optimizer),
along with a small set of sample queries. These queries are explained in further detail below.

## Queries

#### Q1
Find all countries with stops on one stop routes between DTM (Dortmund) and Larnaca.

**[Q1.1]** ... by finding all airports which have incoming flights from DTM and outgoing flights to Larnaca:
```java
g.V().hasLabel("airport").as("a")
    .in("route").has("airport", "code", "DTM")
    .select("a").out("route").has("airport", "city", "Larnaca")
    .select("a").in("contains").hasLabel("country")
```

**[Q1.2]** ... starting at Larnaca and traversing two levels of incoming flight routes:
```java
g.V().has("airport", "city", "Larnaca")
    .in("route").hasLabel("airport").as("a")
    .in("route").has("airport", "code", "DTM")
    .select("a").in("contains").hasLabel("country")
```

**[Q1.3]** ... starting at DTM and traversing two levels of outgoing flights:
```java
g.V().has("airport", "code", "DTM")
    .out("route").hasLabel("airport").as("a")
    .out("route").has("airport", "city", "Larnaca")
    .select("a").in("contains").hasLabel("country")
```

---

#### Q2
// Find all airports reachable in one stop range of DUS (Düsseldorf).

**[Q2.1]** ... starting at DUS and traversing two levels of outgoing flight routes:
```java
g.V().has("airport", "code", "DUS")
    .out("route").hasLabel("airport")
    .out("route").hasLabel("airport")
```

---

#### Q3
// Existence of three stop flights from Qaanaaq (Greenland) to Copenhagen.

**[Q3.1]** ... by traversing four levels of incoming flight routes from Copenhagen. Due to the high fan-out in this direction, this escalates quickly!
```java
g.V().has("airport", "city", "Copenhagen")
    .in("route").hasLabel("airport")
    .in("route").hasLabel("airport")
    .in("route").hasLabel("airport")
    .in("route").has("airport", "city", "Qaanaaq")
```

**[Q3.2]** ... by traversing four levels of outgoing flight routes from Quanaaq. This airport is so remote that the search space stays small even after four iterations.
```java
g.V().has("airport", "city", "Qaanaaq")
    .out("route").hasLabel("airport")
    .out("route").hasLabel("airport")
    .out("route").hasLabel("airport")
    .out("route").has("airport", "city", "Copenhagen")
```

---

#### Q4
Find the direct route from DUS (Düsseldorf) to AMS (Amsterdam).

**[Q4.1]** ... starting from DUS.
```java
g.V().has("airport", "code", "DUS")
    .outE("route").as("r")
    .inV().has("airport", "code", "AMS")
    .select("r")
```

**[Q4.2]** ... starting from AMS.
```java
g.V().has("airport", "code", "AMS")
    .inE("route").as("r")
    .outV().has("airport", "code", "DUS")
    .select("r")
```

**[Q4.3]** ... starting from DUS with pattern matching.
```java
g.V().has("airport", "code", "DUS").as("a")
    .match(
        __.as("a").outE("route").as("r"),
        __.as("r").inV().has("airport", "code", "AMS")
    )
    .select("r")
```

**[Q4.4]** ... starting from AMS with pattern matching.
```java
g.V().has("airport", "code", "AMS").as("a")
    .match(
            __.as("a").inE("route").as("r"),
            __.as("r").outV().has("airport", "code", "DUS")
    )
    .select("r")
```

---

#### Q5
Find all airports in Germany.

**[Q5.1]** ... checking each airport manually.
```java
g.V().hasLabel("airport").as("a")
    .in("contains")
    .has("country", "code", "DE")
    .select("a")
```

**[Q5.2]** ... starting from the country vertex.
```java
g.V().has("country", "code", "DE")
    .out("contains")
    .hasLabel("airport")
```

## Results

|      Query | #Vertices | #Edges | Network [kB] | Runtime [ms] |
|:-----------|----------:|-------:|-------------:|-------------:|
|       Q1.1 |      3385 |  43426 |         6176 |      1335.00 |
|       Q1.2 |       908 |   7037 |         1138 |        53.25 |
|       Q1.3 |       482 |   2259 |          547 |        32.73 |
|  opt(Q1.*) |        26 |    126 |           26 |         5.06 |

|      Query | #Vertices | #Edges | Network [kB] | Runtime [ms] |
|:-----------|----------:|-------:|-------------:|-------------:|
|       Q2.1 |      1508 |  13217 |          952 |         6.49 |
|  opt(Q2.1) |      1508 |  13217 |          952 |         6.76 |

|      Query | #Vertices | #Edges | Network [kB] | Runtime [ms] |
|:-----------|----------:|-------:|-------------:|-------------:|
|       Q3.1 |      3270 |  42581 |         5381 |       130.63 |
|       Q3.2 |        16 |     31 |           21 |         0.87 |
|  opt(Q3.*) |       150 |  13088 |          589 |        29.00 |

|      Query | #Vertices | #Edges | Network [kB] | Runtime [ms] |
|:-----------|----------:|-------:|-------------:|-------------:|
|       Q4.1 |       167 |    166 |          167 |         1.31 |
|       Q4.2 |       273 |    272 |          270 |         1.91 |
|       Q4.3 |       167 |    166 |          168 |         3.22 |
|       Q4.4 |       273 |    272 |          271 |         4.74 |
|  opt(Q4.*) |         2 |    166 |           14 |         0.71 |

|      Query | #Vertices | #Edges | Network [kB] | Runtime [ms] |
|:-----------|----------:|-------:|-------------:|-------------:|
|       Q5.1 |      3612 |   6748 |         2454 |       862.44 |
|       Q5.2 |        34 |     33 |           16 |         0.28 |
|  opt(Q5.*) |        34 |     33 |           16 |         0.35 |
