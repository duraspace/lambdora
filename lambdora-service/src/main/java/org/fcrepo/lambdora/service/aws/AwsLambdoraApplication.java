package org.fcrepo.lambdora.service.aws;

import dagger.Component;
import org.fcrepo.lambdora.service.api.LambdoraApplication;

/**
 * AwsLambdoraApplication
 *
 * @author dbernstein
 */
@Component(modules = AwsServiceModule.class)
public interface AwsLambdoraApplication extends LambdoraApplication {
}
