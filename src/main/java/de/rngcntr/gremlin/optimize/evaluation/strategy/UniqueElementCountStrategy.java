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

import de.rngcntr.gremlin.optimize.evaluation.step.UniqueVertexCountStep;
import de.rngcntr.gremlin.optimize.evaluation.step.UniqueEdgeCountStep;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.FilterStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.EdgeVertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.AbstractTraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class UniqueElementCountStrategy extends AbstractTraversalStrategy<TraversalStrategy.FinalizationStrategy> implements TraversalStrategy.FinalizationStrategy {
    private static final UniqueElementCountStrategy INSTANCE = new UniqueElementCountStrategy();

    public static UniqueElementCountStrategy instance() {
        return INSTANCE;
    }

    @Override
    public void apply(Traversal.Admin<?, ?> traversal) {
        TraversalHelper.getStepsOfAssignableClass(GraphStep.class, traversal).forEach(this::countUniqueElements);
        TraversalHelper.getStepsOfAssignableClass(VertexStep.class, traversal).forEach(this::countUniqueElements);
        //TraversalHelper.getStepsOfAssignableClass(EdgeVertexStep.class, traversal).forEach(this::countUniqueElements);
        TraversalHelper.getStepsOfAssignableClass(FilterStep.class, traversal).forEach(this::countUniqueElements);
    }

    private void countUniqueElements(EdgeVertexStep edgeVertexStep) {
        insertVertexCountStepAfter(edgeVertexStep);
    }

    private void countUniqueElements(FilterStep<Edge> filterStep) {
        Step<?,?> previousStep = filterStep.getPreviousStep();
        insertVertexCountStepAfter(previousStep);
    }

    @SuppressWarnings("unchecked")
    private void countUniqueElements(VertexStep<?> vertexStep) {
        insertVertexCountStepAfter(vertexStep.getPreviousStep());
        if (vertexStep.returnsEdge()) {
            insertEdgeCountStepAfter(vertexStep);
        }
        if (vertexStep.returnsVertex()) {
            makeVertexStepExplicit((VertexStep<Vertex>) vertexStep);
        }
    }

    @SuppressWarnings("unchecked")
    private void countUniqueElements(GraphStep<?,?> graphStep) {
        if (graphStep.returnsEdge()) {
            insertEdgeCountStepAfter(graphStep);
        }
        if (graphStep.returnsVertex()) {
            insertVertexCountStepAfter(graphStep);
        }
    }

    private void insertEdgeCountStepAfter(Step<?, ?> previousStep) {
        TraversalHelper.insertAfterStep(new UniqueEdgeCountStep(previousStep.getTraversal()),
                previousStep, previousStep.getTraversal());
    }

    private void insertVertexCountStepAfter(Step<?, ?> previousStep) {
        TraversalHelper.insertAfterStep(new UniqueVertexCountStep(previousStep.getTraversal()),
                previousStep, previousStep.getTraversal());
    }

    private void makeVertexStepExplicit(VertexStep<Vertex> vertexStep) {
        VertexStep<Edge> veStep = new VertexStep<>(vertexStep.getTraversal(), Edge.class, vertexStep.getDirection(), vertexStep.getEdgeLabels());
        EdgeVertexStep evStep = new EdgeVertexStep(vertexStep.getTraversal(), vertexStep.getDirection().opposite());
        Step<?, Vertex> previousStep = vertexStep.getPreviousStep();

        TraversalHelper.insertAfterStep(veStep, previousStep, vertexStep.getTraversal());
        TraversalHelper.insertAfterStep(evStep, veStep, vertexStep.getTraversal());
        vertexStep.getTraversal().removeStep(vertexStep);

        insertEdgeCountStepAfter(veStep);
    }
}