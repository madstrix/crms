package com.github.modelflat.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;

public class CommandLineManagerTests {

    @Test(dataProvider = "helpCases")
    public void helpOptionTest(String[] args) throws Exception {
        CommandLineManager manager = new CommandLineManager(args);
        assertThat("Should print help for " + Arrays.toString(args), manager.shouldPrintHelp());
    }

    @DataProvider(name = "helpCases")
    public Object[][] helpOptionArgsProvider() {
        return new String[][][] {
                // should print help in any case presented above
                {
                        {
                            "--" + CommandLineManager.OPTION_HELP.getLongOpt()
                        }
                },
                {
                        {
                            "-" + CommandLineManager.OPTION_HELP.getOpt()
                        }
                },
                {
                        {}
                }
        };
    }

    @Test(dataProvider = "rankCases")
    public void rankTest(int expected, String[] args) throws Exception {
        assertThat("Should be properly extracted and 0 by default",
                new CommandLineManager(args).getHostNodeRank() == expected);
    }

    @DataProvider(name = "rankCases")
    public Object[][] rankOptionArgsProvider() {
        return new Object[][] {
                {
                    5, new String[] {"--" + CommandLineManager.OPTION_HOST_RANK.getLongOpt() + "=5"}
                },
                {
                    4, new String[] {"-" + CommandLineManager.OPTION_HOST_RANK.getOpt() + "=4"}
                },
                { // zero by default
                    0, new String[] {}
                }
        };
    }

}
