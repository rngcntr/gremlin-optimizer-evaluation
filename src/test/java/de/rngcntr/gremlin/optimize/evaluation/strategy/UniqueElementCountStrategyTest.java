// Copyright 2020 Florian Grieskamp
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package de.rngcntr.gremlin.optimize.evaluation.strategy;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.rngcntr.gremlin.optimize.evaluation.Main;
import de.rngcntr.gremlin.optimize.evaluation.graph.Runner;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueEdgeCounter;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueVertexCounter;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.tinkerpop.optimize.JanusGraphStepStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class UniqueElementCountStrategyTest {
    protected GraphTraversalSource g;

    protected abstract void initializeGraph();

    public void assertSameResultAfterOptimization(GraphTraversal<?,?> traversal) {
        GraphTraversal<?,?> unoptimizedTraversal = traversal.asAdmin().clone();
        Multiset<?> unoptimizedResults = HashMultiset.create(unoptimizedTraversal.toList());
        TraversalStrategies strategies = TraversalStrategies.GlobalCache.getStrategies(Graph.class);
        if (traversal.asAdmin().getGraph().get() instanceof StandardJanusGraph) {
            strategies.addStrategies(JanusGraphStepStrategy.instance(), new UniqueElementCountStrategy());
        } else {
            strategies.addStrategies(new UniqueElementCountStrategy());
        }
        traversal.asAdmin().setStrategies(strategies);
        Multiset<?> optimizedResults = HashMultiset.create(traversal.toList());
        assertEquals(unoptimizedResults, optimizedResults);
    }

    @ParameterizedTest
    @MethodSource("testedTraversals")
    public void testTraversal(int numVertices, int numEdges, Function<GraphTraversalSource, GraphTraversal<?,?>> t){
        GraphTraversal<?,?> traversal = t.apply(g);
        assertSameResultAfterOptimization(traversal);
        assertEquals(numEdges, Runner.accumulate(UniqueEdgeCounter.resetCount()));
        assertEquals(numVertices, Runner.accumulate(UniqueVertexCounter.resetCount()));
    }
}
