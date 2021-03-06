/**
 * Copyright 2017 Matthieu Jimenez.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package meow.languageprocessing.ngram.actions;

import greycat.ActionFunction;
import greycat.TaskContext;
import meow.languageprocessing.ActionTest;
import org.junit.jupiter.api.Test;

import static greycat.Tasks.newTask;
import static meow.tokens.TokensConstants.ENTRY_POINT_INDEX;
import static meow.tokens.TokensConstants.ENTRY_POINT_NODE_NAME;
import static meow.languageprocessing.ngram.NgramConstants.NGRAM_NODE_NAME;
import static meow.languageprocessing.ngram.actions.NgramActions.initializeNgram;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionInitializeNgramTest extends ActionTest {
    @Test
    public void test() {
        int counter = 1;
        final int[] i = {0};
        initGraph();
        newTask()
                .travelInTime("0")
                .then(initializeNgram())
                .readGlobalIndex(ENTRY_POINT_INDEX, ENTRY_POINT_NODE_NAME, NGRAM_NODE_NAME)
                .thenDo(new ActionFunction() {
                            public void eval(TaskContext context) {
                                assertEquals(context.resultAsNodes().size(), 1);
                                i[0]++;
                                context.continueTask();
                            }
                        }
                )
                .execute(graph, null);
        assertEquals(counter, i[0]);
        removeGraph();
    }

    @Test
    public void test2() {
        int counter = 1;
        final int[] i = {0};
        initGraph();
        newTask()
                .travelInTime("0")
                .then(initializeNgram())
                .travelInTime("1")
                .readGlobalIndex(ENTRY_POINT_INDEX, ENTRY_POINT_NODE_NAME, NGRAM_NODE_NAME)
                .thenDo(new ActionFunction() {
                            public void eval(TaskContext context) {
                                assertEquals(context.resultAsNodes().size(), 1);
                                i[0]++;
                                context.continueTask();
                            }
                        }
                )
                .execute(graph, null);
        assertEquals(counter, i[0]);
        removeGraph();
    }

}