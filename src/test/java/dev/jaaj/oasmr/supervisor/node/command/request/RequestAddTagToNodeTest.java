/*
 * Copyright (c) 2019. JaaJ-dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jaaj.oasmr.supervisor.node.command.request;

import dev.jaaj.oasmr.supervisor.Supervisor;
import dev.jaaj.oasmr.supervisor.node.Node;
import dev.jaaj.oasmr.supervisor.node.Tag;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RequestAddTagToNodeTest {
    private Node n;
    private Supervisor s;

    @Before
    public void setUp() throws Exception {
        s = new Supervisor(5852);
        n = s.getNodeFlyweightFactory().getNode(InetAddress.getByName("127.02.20.5"), 5869);
    }

    @Test
    public void executeAddTagToNode() throws Exception {
        Set<Tag> old = new HashSet<>(n.getTags());
        new RequestAddTagToNode(n.getId(), new Tag("jeej")).execute(s);
        Set<Tag> newTag = n.getTags();
        assertEquals(old.size() + 1, newTag.size());
    }
}