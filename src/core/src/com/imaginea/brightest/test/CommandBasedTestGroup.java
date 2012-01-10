package com.imaginea.brightest.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A collection of tests. The group has a name which could be the file name or could be generated.
 */
public class CommandBasedTestGroup {
    private final String groupName;
    private final List<CommandBasedTest> tests = new ArrayList<CommandBasedTest>();

    public CommandBasedTestGroup() {
        this(UUID.randomUUID().toString(), Collections.<CommandBasedTest> emptyList());
    }

    public CommandBasedTestGroup(String groupName, List<CommandBasedTest> originalTtests) {
        this.groupName = groupName;
        this.tests.addAll(originalTtests);
    }

    public void addTest(CommandBasedTest test) {
        tests.add(test);
    }

    public List<CommandBasedTest> getTests() {
        return Collections.unmodifiableList(tests);
    }

    public String toString() {
        return String.format("Group[%s, tests [%s]]", this.groupName, this.tests);
    }
}
