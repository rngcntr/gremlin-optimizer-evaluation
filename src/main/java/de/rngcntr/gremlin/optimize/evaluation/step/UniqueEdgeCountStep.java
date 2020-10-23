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

package de.rngcntr.gremlin.optimize.evaluation.step;

import de.rngcntr.gremlin.optimize.evaluation.util.UniqueEdgeCounter;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;

public class UniqueEdgeCountStep extends UniqueElementCountStep {

    private static int instanceCounter = 0;

    private int stepInstance;

    public UniqueEdgeCountStep(Traversal.Admin<?, ?> traversal) {
        super(traversal);
        stepInstance = instanceCounter++;
    }

    @Override
    void registerElement(Element element) {
        if (element instanceof Edge) {
            UniqueEdgeCounter.registerEdge(stepInstance, (Edge) element);
        }
    }

    public String toString() {
        return "UniqueEdgeCountStep(" + stepInstance + ')';
    }
}