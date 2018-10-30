/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.statements.transaction;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for committed aborted handlers TransactionStatement.
 */
public class TransactionBlockTest {

    private CompileResult programFile;

    @BeforeClass
    public void setup() {
        programFile = BCompileUtil.compile("test-src/statements/transaction/transaction_block_test.bal");
    }

    private BValue[] runFunctionWithTxConfig(int txFailures, boolean abort) {
        BValue[] params = {new BInteger(txFailures), new BBoolean(abort)};
        return BRunUtil.invoke(programFile, "testTransactionStmtWithCommitedAndAbortedBlocks", params);
    }

    @Test
    public void testTransactionStmtWithnoAbortNoFailure() {
        BValue[] returns = runFunctionWithTxConfig(0, false);
        Assert.assertEquals(returns[0].stringValue(), "start inTrx endTrx committed end");
    }

    @Test
    public void testTransactionStmtWithAbortNoFailure() {
        BValue[] returns = runFunctionWithTxConfig(0, true);
        Assert.assertEquals(returns[0].stringValue(), "start inTrx aborted end");
    }

    @Test
    public void testTransactionStmtWithnoAbortSingleFailure() {
        BValue[] returns = runFunctionWithTxConfig(1, false);
        Assert.assertEquals(returns[0].stringValue(), "start inTrx retry inTrx endTrx committed end");
    }

    @Test
    public void testTransactionStmtWithnoAbortFailureFailure() {
        BValue[] returns = runFunctionWithTxConfig(2, false);
        Assert.assertEquals(returns[0].stringValue(), "start inTrx retry inTrx retry end");
    }

    @Test
    public void testTransactionStmtWithFailureAndAbort() {
        BValue[] returns = runFunctionWithTxConfig(1, true);
        Assert.assertEquals(returns[0].stringValue(), "start inTrx retry inTrx aborted end");
    }
}
