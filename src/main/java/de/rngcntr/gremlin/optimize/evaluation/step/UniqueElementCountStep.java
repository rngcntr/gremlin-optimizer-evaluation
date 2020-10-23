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

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.MapStep;
import org.apache.tinkerpop.gremlin.structure.Element;

public abstract class UniqueElementCountStep<E extends Element> extends MapStep<E, E> {

    public UniqueElementCountStep(Traversal.Admin<?,?> traversal) {
        super(traversal);
    }

    @Override
    protected E map(Traverser.Admin<E> traverser) {
        registerElement(traverser.get());
        return traverser.get();
    }

    abstract void registerElement(E element);
}