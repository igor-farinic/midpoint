/*
 * Copyright (c) 2010-2014 Evolveum
 *
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

package com.evolveum.midpoint.model.scripting;

import com.evolveum.midpoint.model.scripting.expressions.SearchEvaluator;
import com.evolveum.midpoint.model.scripting.expressions.SelectEvaluator;
import com.evolveum.midpoint.model.scripting.helpers.JaxbHelper;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.query.ObjectFilter;
import com.evolveum.midpoint.schema.QueryConvertor;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.task.api.TaskManager;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ActionExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ConstantExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ExecuteScriptType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ExpressionPipelineType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ExpressionSequenceType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.FilterExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ForeachExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ObjectFactory;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.SearchExpressionType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.SelectExpressionType;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for evaluating scripting expressions.
 *
 * @author mederly
 */
@Component
public class ScriptingExpressionEvaluator {

    private static final Trace LOGGER = TraceManager.getTrace(ScriptingExpressionEvaluator.class);
    private static final String DOT_CLASS = ScriptingExpressionEvaluator.class + ".";

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private SearchEvaluator searchEvaluator;

    @Autowired
    private SelectEvaluator selectEvaluator;

    @Autowired
    private JaxbHelper jaxbHelper;

    @Autowired
    private PrismContext prismContext;

    private ObjectFactory objectFactory = new ObjectFactory();

    private Map<String,ActionExecutor> actionExecutors = new HashMap<>();

    /**
     * Asynchronously executes simple scripting expressions, consisting of one search command and one action.
     *
     * @param objectType Object type to search (e.g. c:UserType)
     * @param filter Filter to be applied (ObjectFilter)
     * @param actionName Action to be executed on objects found (e.g. "disable", "delete", "recompute", etc).
     * @param task Task in context of which the script should execute. The task should be "clean", i.e.
     *             (1) transient, (2) without any handler. This method puts the task into background,
     *             and assigns ScriptExecutionTaskHandler to it, to execute the script.
     * @param parentResult
     * @throws SchemaException
     */
    public void evaluateExpressionInBackground(QName objectType, ObjectFilter filter, String actionName, Task task, OperationResult parentResult) throws SchemaException {
        Validate.notNull(objectType);
        Validate.notNull(actionName);
        Validate.notNull(task);

        SearchExpressionType search = new SearchExpressionType();
        search.setType(objectType);
        if (filter != null) {
            search.setSearchFilter(QueryConvertor.createFilterType(filter, DOMUtil.getDocument(), prismContext));
        }
        ActionExpressionType action = new ActionExpressionType();
        action.setType(actionName);
        search.setExpression(objectFactory.createAction(action));
        evaluateExpressionInBackground(search, task, parentResult);
    }

    /**
     * Asynchronously executes any scripting expression.
     *
     * @param expression Expression to be executed.
     * @param task Task in context of which the script should execute. The task should be "clean", i.e.
     *             (1) transient, (2) without any handler. This method puts the task into background,
     *             and assigns ScriptExecutionTaskHandler to it, to execute the script.
     * @param parentResult
     * @throws SchemaException
     */
    public void evaluateExpressionInBackground(ExpressionType expression, Task task, OperationResult parentResult) throws SchemaException {
        OperationResult result = parentResult.createSubresult(DOT_CLASS + "evaluateExpressionInBackground");
        if (!task.isTransient()) {
            throw new IllegalStateException("Task must be transient");
        }
        if (task.getHandlerUri() != null) {
            throw new IllegalStateException("Task must not have a handler");
        }
        ExecuteScriptType executeScriptType = new ExecuteScriptType();
        executeScriptType.setExpression(jaxbHelper.toJaxbElement(expression));
        task.setExtensionPropertyValue(SchemaConstants.SE_EXECUTE_SCRIPT, executeScriptType);
        task.setHandlerUri(ScriptExecutionTaskHandler.HANDLER_URI);
        taskManager.switchToBackground(task, result);
        result.computeStatus();
    }

    /**
     * Entry point to _synchronous_ script execution, with no input data.
     *
     * @param expression Scripting expression to execute.
     * @param task Task in context of which the script should execute (in foreground!)
     * @param result Operation result
     * @return ExecutionContext, from which the caller can retrieve the output data via getFinalOutput() method,
     *         and the console output via getConsoleOutput() method.
     * @throws ScriptExecutionException
     */
    public ExecutionContext evaluateExpression(ExpressionType expression, Task task, OperationResult result) throws ScriptExecutionException {
        ExecutionContext context = new ExecutionContext(task);
        Data output = evaluateExpression(expression, Data.createEmpty(), context, result);
        result.computeStatusIfUnknown();
        context.setFinalOutput(output);
        return context;
    }

    public ExecutionContext evaluateExpression(ExecuteScriptType executeScript, Task task, OperationResult result) throws ScriptExecutionException {
        return evaluateExpression(executeScript.getExpression().getValue(), task, result);
    }

    public ExecutionContext evaluateExpression(ExpressionType expression, OperationResult result) throws ScriptExecutionException {
        Task task = taskManager.createTaskInstance();
        return evaluateExpression(expression, task, result);
    }

    public Data evaluateExpression(ExpressionType expression, Data input, ExecutionContext context, OperationResult parentResult) throws ScriptExecutionException {
        OperationResult result = parentResult.createMinorSubresult(DOT_CLASS + "evaluateExpression");
        Data output;
        if (expression instanceof ExpressionPipelineType) {
            output = executePipeline((ExpressionPipelineType) expression, input, context, result);
        } else if (expression instanceof ExpressionSequenceType) {
            output = executeSequence((ExpressionSequenceType) expression, input, context, result);
        } else if (expression instanceof ForeachExpressionType) {
            output = executeForEach((ForeachExpressionType) expression, input, context, result);
        } else if (expression instanceof SelectExpressionType) {
            output = selectEvaluator.evaluate((SelectExpressionType) expression, input, context, result);
        } else if (expression instanceof FilterExpressionType) {
            output = executeFilter((FilterExpressionType) expression, input, context, result);
        } else if (expression instanceof SearchExpressionType) {
            output = searchEvaluator.evaluate((SearchExpressionType) expression, input, context, result);
        } else if (expression instanceof ActionExpressionType) {
            output = executeAction((ActionExpressionType) expression, input, context, result);
        } else if (expression instanceof ConstantExpressionType) {
            output = evaluateConstantExpression((ConstantExpressionType) expression, context, result);
        } else {
            throw new IllegalArgumentException("Unsupported expression type: " + expression.getClass());
        }
        result.computeStatusIfUnknown();
        return output;
    }

    private Data executeAction(ActionExpressionType command, Data input, ExecutionContext context, OperationResult result) throws ScriptExecutionException {
        Validate.notNull(command, "command");
        Validate.notNull(command.getType(), "command.actionType");

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Executing action {} on {}", command.getType(), input.debugDump());
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing action {}", command.getType());
        }
        ActionExecutor executor = actionExecutors.get(command.getType());
        if (executor == null) {
            throw new IllegalStateException("Unsupported action type: " + command.getType());
        } else {
            return executor.execute(command, input, context, result);
        }
    }


    private Data executeFilter(FilterExpressionType command, Data input, ExecutionContext context, OperationResult result) {
        throw new NotImplementedException();
    }

    private Data executeForEach(ForeachExpressionType command, Data input, ExecutionContext context, OperationResult result) {
        return null;
    }

    private Data executePipeline(ExpressionPipelineType pipeline, Data data, ExecutionContext context, OperationResult result) throws ScriptExecutionException {
        for (JAXBElement<? extends ExpressionType> expressionElement : pipeline.getExpression()) {
            data = evaluateExpression(expressionElement.getValue(), data, context, result);
        }
        return data;
    }

    private Data executeSequence(ExpressionSequenceType sequence, Data input, ExecutionContext context, OperationResult result) throws ScriptExecutionException {
        Data lastOutput = null;
        for (JAXBElement<? extends ExpressionType> expressionElement : sequence.getExpression()) {
            lastOutput = evaluateExpression(expressionElement.getValue(), input, context, result);
        }
        return lastOutput;
    }

    private Data evaluateConstantExpression(ConstantExpressionType expression, ExecutionContext context, OperationResult result) throws ScriptExecutionException {
        if (expression.getValue() instanceof Element) {
            Element element = (Element) expression.getValue();              // this should be <s:value> element
            NodeList children = element.getChildNodes();
            JAXBElement jaxbValue = null;
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    if (jaxbValue != null) {
                        throw new ScriptExecutionException("More than one expression value is not supported for now");
                    }
                    try {
                        jaxbValue = prismContext.getPrismJaxbProcessor().unmarshalElement(child, Object.class);
                    } catch (JAXBException|SchemaException e) {
                        throw new ScriptExecutionException("Couldn't unmarshal value of element: " + element.getNodeName());
                    }
                }
            }
            if (jaxbValue != null) {
                return Data.createProperty(jaxbValue.getValue(), prismContext);
            } else {
                String text = element.getTextContent();
                if (StringUtils.isNotEmpty(text)) {             // todo what if there will be only LF's among child elements? this needs to be thought out...
                    return Data.createProperty(text, prismContext);
                } else {
                    return Data.createEmpty();
                }
            }
        } else {
            return Data.createProperty(expression.getValue(), prismContext);
        }
    }

    public void registerActionExecutor(String actionName, ActionExecutor executor) {
        actionExecutors.put(actionName, executor);
    }

}
