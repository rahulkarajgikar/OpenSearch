/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.wlm.stats;

import org.opensearch.Version;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.cluster.node.DiscoveryNodeRole;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.common.io.stream.Writeable;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.test.AbstractWireSerializingTestCase;
import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.test.VersionUtils;
import org.opensearch.wlm.ResourceType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.mock;

public class WlmStatsTests extends AbstractWireSerializingTestCase<WlmStats> {

    public void testToXContent() throws IOException {
        final Map<String, WorkloadGroupStats.WorkloadGroupStatsHolder> stats = new HashMap<>();
        final String workloadGroupId = "afakjklaj304041-afaka";
        stats.put(
            workloadGroupId,
            new WorkloadGroupStats.WorkloadGroupStatsHolder(
                123456789,
                13,
                2,
                0,
                Map.of(ResourceType.CPU, new WorkloadGroupStats.ResourceStats(0.3, 13, 2))
            )
        );
        XContentBuilder builder = JsonXContent.contentBuilder();
        WorkloadGroupStats workloadGroupStats = new WorkloadGroupStats(stats);
        WlmStats wlmStats = new WlmStats(mock(DiscoveryNode.class), workloadGroupStats);
        builder.startObject();
        wlmStats.toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        assertEquals(
            "{\"workload_groups\":{\"afakjklaj304041-afaka\":{\"total_completions\":123456789,\"total_rejections\":13,\"total_cancellations\":0,\"cpu\":{\"current_usage\":0.3,\"cancellations\":13,\"rejections\":2}}}}",
            builder.toString()
        );
    }

    @Override
    protected Writeable.Reader<WlmStats> instanceReader() {
        return WlmStats::new;
    }

    @Override
    protected WlmStats createTestInstance() {
        DiscoveryNode discoveryNode = new DiscoveryNode(
            "node",
            OpenSearchTestCase.buildNewFakeTransportAddress(),
            emptyMap(),
            DiscoveryNodeRole.BUILT_IN_ROLES,
            VersionUtils.randomCompatibleVersion(random(), Version.CURRENT)
        );
        WorkloadGroupStatsTests workloadGroupStatsTests = new WorkloadGroupStatsTests();
        return new WlmStats(discoveryNode, workloadGroupStatsTests.createTestInstance());
    }
}
