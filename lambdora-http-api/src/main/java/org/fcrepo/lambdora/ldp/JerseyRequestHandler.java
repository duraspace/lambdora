package org.fcrepo.lambdora.ldp;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * JerseyRequestHandler class
 *
 * @author gtriggs
 */
public class JerseyRequestHandler implements RequestHandler<AwsProxyRequest,AwsProxyResponse> {
    private JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     *
     */
    public JerseyRequestHandler() {
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(new JerseyApplication());
    }

    /**
     *
     * @param awsProxyRequest
     * @param context
     * @return
     */
    @Override
    public AwsProxyResponse handleRequest(final AwsProxyRequest awsProxyRequest, final Context context) {
        handler.stripBasePath("/rest");
        return handler.proxy(awsProxyRequest, context);
    }
}
