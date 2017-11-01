package org.fcrepo.lambdora.ldp;

import com.amazonaws.serverless.proxy.internal.model.ApiGatewayRequestContext;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * AWS Request Handler to proxy requests through to Jersey
 *
 * @author gtriggs
 */
public class JerseyRequestHandler implements RequestHandler<AwsProxyRequest,AwsProxyResponse> {
    private JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    private static final Logger LOGGER = getLogger(JerseyRequestHandler.class);

    /**
     * Default constructor (used by aws lambda)
     */
    public JerseyRequestHandler() {
        this(new JerseyApplication());
    }

    /**
     * Initialise the request handler with JerseyApplication (for testing purposes)
     *
     * @param application
     */
    public JerseyRequestHandler(final JerseyApplication application) {
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
    }
    /**
     * Mpas requests through AWS to Jersey endpoints
     *
     * @param awsProxyRequest
     * @param context
     * @return
     */
    @Override
    public AwsProxyResponse handleRequest(final AwsProxyRequest awsProxyRequest, final Context context) {
        // Captures the HTTP request in the log
        final ApiGatewayRequestContext requestContext = awsProxyRequest.getRequestContext();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\n\n*********** BEGIN HTTP REQUEST ***********" +
                         "\nHTTP Method={}" +
                         "\nPath={}" +
                         "\nResource={}" +
                         "\nPath Parameters={}" +
                         "\nQuery String Parameters={}" +
                         "\nStage Variables={}" +
                         "\nHeaders={}" +
                         "\nBody={}",
                         "\ncontext.apiId={}" +
                         "\ncontext.accountId={}" +
                         "\ncontext.path={}" +
                         "\ncontext.resourceId={}" +
                         "\ncontext.resourcePath={}" +
                         "\ncontext.stage={}" +
                         "\n*********** END HTTP REQUEST ***********\n",
                         awsProxyRequest.getHttpMethod(),
                         awsProxyRequest.getPath(),
                         awsProxyRequest.getResource(),
                         awsProxyRequest.getPathParameters(),
                         awsProxyRequest.getQueryStringParameters(),
                         awsProxyRequest.getStageVariables(),
                         awsProxyRequest.getHeaders(),
                         awsProxyRequest.getBody(),
                         requestContext.getApiId(),
                         requestContext.getAccountId(),
                         requestContext.getPath(),
                         requestContext.getResourceId(),
                         requestContext.getResourcePath(),
                         requestContext.getStage());
        }

        awsProxyRequest.getHeaders().put("context", requestContext.getStage());
        return handler.proxy(awsProxyRequest, context);
    }
}
