/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.tools.testng;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * Created by IntelliJ IDEA.
 * User: mamut
 * Date: 22.11.2011
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class TestMethodLoggerListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("-----------------------------------------------------------");
        System.out.println("TestNG running method: " + method.getTestMethod().toString());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("TestNG finish method : " + method.getTestMethod().toString() + " Result: " + (testResult.isSuccess() ? "PASS" : "FAIL"));
        System.out.println("-----------------------------------------------------------");
    }
}
