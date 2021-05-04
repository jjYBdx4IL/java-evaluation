package org.yaml.snakeyaml;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.Version;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class YamlTest {

    private static final Logger LOG = LoggerFactory.getLogger(YamlTest.class);

    @Test
    public void test() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "test");
        data.put("tags", Arrays.asList("1", "2", "3"));
        data.put("tags2", Arrays.asList(4, 5, 6));

        DumperOptions opts = new DumperOptions();
        opts.setDefaultScalarStyle(ScalarStyle.PLAIN);
        opts.setDefaultFlowStyle(FlowStyle.BLOCK);
        opts.setCanonical(false);
        opts.setIndent(4);
        opts.setIndicatorIndent(2);
        opts.setExplicitStart(true);
        opts.setExplicitEnd(true);
        opts.setPrettyFlow(true);
        opts.setVersion(Version.V1_1);
        Yaml yaml = new Yaml(opts);
        String dump = yaml.dump(data);
        LOG.info(dump);
    }
}
